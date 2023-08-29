package ru.kostapo.services;

import ru.kostapo.exchangerate.dto.ExchangeRateResDTO;
import ru.kostapo.exchange.dto.ExchangeReqDTO;
import ru.kostapo.exchange.dto.ExchangeResDTO;
import ru.kostapo.common.exceptions.BadParameterException;
import ru.kostapo.common.exceptions.NotFoundException;
import ru.kostapo.exchangerate.ExchangeRateService;
import ru.kostapo.exchangerate.ExchangeRateServiceImpl;
import ru.kostapo.utils.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.Optional;

public class ExchangeServiceImpl implements ExchangeService{

    private final StringUtils stringUtils;
    private final ExchangeRateService exchangeRateService;

    public ExchangeServiceImpl(Connection connection) {
        this.stringUtils = new StringUtils();
        this.exchangeRateService = new ExchangeRateServiceImpl(connection);
    }

    @Override
    public ExchangeResDTO getExchange(ExchangeReqDTO exchangeReqDTO) {
        Optional<ExchangeRateResDTO> directExchangeRateDTO =
                exchangeRateService.findByCodes(exchangeReqDTO.getFrom(), exchangeReqDTO.getTo());
        if(directExchangeRateDTO.isPresent()) {
            return getDirectExchange(directExchangeRateDTO.get(), exchangeReqDTO.getAmount());
        }
        Optional<ExchangeRateResDTO> reversExchangeRateDTO =
                exchangeRateService.findByCodes(exchangeReqDTO.getTo(), exchangeReqDTO.getFrom());
        if(reversExchangeRateDTO.isPresent()) {
            return getReverseExchange(reversExchangeRateDTO.get(), exchangeReqDTO.getAmount());
        }
        Optional<ExchangeRateResDTO> usdFromDTO = exchangeRateService.findByCodes("USD", exchangeReqDTO.getFrom());
        Optional<ExchangeRateResDTO> usdToDTO = exchangeRateService.findByCodes("USD", exchangeReqDTO.getTo());
        if(usdFromDTO.isPresent() && usdToDTO.isPresent()) {
            return getCrossExchange(usdFromDTO.get(), usdToDTO.get(), exchangeReqDTO.getAmount());
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

    private ExchangeResDTO getDirectExchange(ExchangeRateResDTO exchangeRate, BigDecimal amount) {
        return new ExchangeResDTO(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                exchangeRate.getRate().multiply(amount)
                        .setScale(2, RoundingMode.FLOOR));
    }

    private ExchangeResDTO getReverseExchange(ExchangeRateResDTO exchangeRate, BigDecimal amount) {
        BigDecimal rate = calcReversRate(exchangeRate.getRate());
        return new ExchangeResDTO(
                exchangeRate.getTargetCurrency(),
                exchangeRate.getBaseCurrency(),
                rate,
                amount,
                rate.multiply(amount).setScale(2, RoundingMode.FLOOR));
    }

    private ExchangeResDTO getCrossExchange(ExchangeRateResDTO usdFrom, ExchangeRateResDTO usdTo, BigDecimal amount) {
        BigDecimal rate = calcCrossRate(usdFrom.getRate(), usdTo.getRate());
        return new ExchangeResDTO(
                usdFrom.getTargetCurrency(),
                usdTo.getTargetCurrency(),
                rate,
                amount,
                rate.multiply(amount).setScale(2, RoundingMode.FLOOR));
    }

    private BigDecimal calcReversRate (BigDecimal rate) {
        return new BigDecimal(1).divide(rate, 2, RoundingMode.FLOOR);
    }

    private BigDecimal calcCrossRate (BigDecimal usdFromRate, BigDecimal usdToRate) {
        return  calcReversRate(usdFromRate).divide(calcReversRate(usdToRate),2, RoundingMode.FLOOR);
    }
}
