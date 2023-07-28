package ru.kostapo.services;

import ru.kostapo.dto.CurrencyResDTO;
import ru.kostapo.dto.ExchangeRateResDTO;
import ru.kostapo.dto.ExchangeReqDTO;
import ru.kostapo.dto.ExchangeResDTO;
import ru.kostapo.exceptions.BadParameterException;
import ru.kostapo.exceptions.NotFoundException;
import ru.kostapo.utils.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeServiceImpl implements ExchangeService{
    private final StringUtils stringUtils;
    private final ExchangeRateService exchangeRateService;

    public ExchangeServiceImpl() {
        this.stringUtils = new StringUtils();
        this.exchangeRateService = new ExchangeRateServiceImpl();
    }

    @Override
    public Optional<ExchangeResDTO> getExchange(ExchangeReqDTO exchangeReqDTO) {
        Optional<ExchangeRateResDTO> responseDTO
                = exchangeRateService.findByCodes(exchangeReqDTO.getFrom(), exchangeReqDTO.getTo());
        if(responseDTO.isPresent()) {
            BigDecimal rate = responseDTO.get().getRate();
            return getExchange(responseDTO.get().getBaseCurrency(), responseDTO.get().getTargetCurrency(),
                    rate.divide(rate, 2, RoundingMode.FLOOR), exchangeReqDTO.getAmount());
        }
        responseDTO = exchangeRateService.findByCodes(exchangeReqDTO.getTo(), exchangeReqDTO.getFrom());
        if(responseDTO.isPresent()) {
            BigDecimal rate = calcReversRate(responseDTO.get().getRate());
            return getExchange(responseDTO.get().getBaseCurrency(), responseDTO.get().getTargetCurrency(),
                    rate, exchangeReqDTO.getAmount());
        }
        Optional<ExchangeRateResDTO> usdFromDTO = exchangeRateService.findByCodes("USD", exchangeReqDTO.getFrom());
        Optional<ExchangeRateResDTO> usdToDTO = exchangeRateService.findByCodes("USD", exchangeReqDTO.getTo());
        if(usdFromDTO.isPresent() && usdToDTO.isPresent()) {
            BigDecimal rate = calcRateViaUsd(usdFromDTO.get().getRate(), usdToDTO.get().getRate());
            return getExchange(usdFromDTO.get().getTargetCurrency(), usdToDTO.get().getTargetCurrency(),
                    rate, exchangeReqDTO.getAmount());
        }
        throw new NotFoundException("ОБМЕННЫЙ КУРС НЕ НАЙДЕН");
    }

    @Override
    public boolean isRequestDataValid(ExchangeReqDTO exchangeReqDTO) {
        if (exchangeReqDTO.getFrom() == null || exchangeReqDTO.getFrom().isEmpty())
            throw new BadParameterException("ОТСУТСТВУЕТ НУЖНОЕ ПОЛЕ ФОРМЫ (from не указан)");
        if (exchangeReqDTO.getTo() == null || exchangeReqDTO.getTo().isEmpty())
            throw new BadParameterException("ОТСУТСТВУЕТ НУЖНОЕ ПОЛЕ ФОРМЫ (to не указан)");
        try {
            stringUtils.isCodeValid(exchangeReqDTO.getFrom());
            stringUtils.isCodeValid(exchangeReqDTO.getTo());
        } catch (BadParameterException ex) {
            throw new BadParameterException(ex.getMessage());
        }
        return true;
    }

    private BigDecimal calcReversRate (BigDecimal rate) {
        return new BigDecimal(1).divide(rate, 2, RoundingMode.FLOOR);
    }

    private BigDecimal calcRateViaUsd (BigDecimal usdFromRate, BigDecimal usdToRate) {
        return  calcReversRate(usdFromRate).divide(calcReversRate(usdToRate),6, RoundingMode.FLOOR);
    }

    private Optional<ExchangeResDTO> getExchange(CurrencyResDTO from, CurrencyResDTO to,
                                                 BigDecimal rate, BigDecimal amount) {
        return Optional.of(new ExchangeResDTO(from, to, rate, amount,
                rate.multiply(amount).setScale(2, RoundingMode.FLOOR)));
    }
}
