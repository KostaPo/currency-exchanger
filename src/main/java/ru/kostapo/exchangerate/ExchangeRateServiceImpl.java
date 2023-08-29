package ru.kostapo.services;

import ru.kostapo.dto.ExchangeRateReqDTO;
import ru.kostapo.dto.ExchangeRateResDTO;
import ru.kostapo.common.exceptions.BadParameterException;
import ru.kostapo.mappers.ExchangeRateMapper;
import ru.kostapo.models.ExchangeRate;
import ru.kostapo.repositories.ExchangeRateRepository;
import ru.kostapo.utils.StringUtils;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateMapper exchangeRateMapper;

    private final StringUtils stringUtils;

    public ExchangeRateServiceImpl(Connection connection) {
        this.exchangeRateRepository = new ExchangeRateRepository(connection);
        this.exchangeRateMapper = new ExchangeRateMapper(connection);
        this.stringUtils = new StringUtils();
    }

    @Override
    public Optional<List<ExchangeRateResDTO>> findAllExchangeRates() {
        Optional<List<ExchangeRate>> exchangeRates = exchangeRateRepository.findAll();
        if(exchangeRates.isPresent()) {
            List<ExchangeRateResDTO> list = exchangeRateMapper.toDtoList(exchangeRates.get());
            return Optional.of(list);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ExchangeRateResDTO> findById(Integer id) {
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findById(id);
        if(exchangeRate.isPresent()) {
            ExchangeRateResDTO exchangeRateResDTO = exchangeRateMapper.toDTO(exchangeRate.get());
            return Optional.of(exchangeRateResDTO);
        }
        return Optional.empty();
    }

    @Override
    public ExchangeRateResDTO update(ExchangeRateReqDTO exchangeRateReqDTO) {
        ExchangeRate exchangeRate = exchangeRateRepository.update(exchangeRateMapper.toModel(exchangeRateReqDTO));
        return exchangeRateMapper.toDTO(exchangeRate);
    }

    @Override
    public Optional<ExchangeRateResDTO> findByCodes(String base, String target) {
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByStrPair(base, target);
        if(exchangeRate.isPresent()) {
            ExchangeRateResDTO exchangeRateResDTO = exchangeRateMapper.toDTO(exchangeRate.get());
            return Optional.of(exchangeRateResDTO);
        }
        return Optional.empty();
    }

    @Override
    public ExchangeRateResDTO save(ExchangeRateReqDTO exchangeRateReqDTO) {
        ExchangeRate exchangeRate = exchangeRateRepository.save(exchangeRateMapper.toModel(exchangeRateReqDTO));
        return exchangeRateMapper.toDTO(exchangeRate);
    }

    @Override
    public void delete(Integer id) {
        exchangeRateRepository.delete(id);
    }

    @Override
    public boolean isRequestDataValid(ExchangeRateReqDTO exchangeRateReqDTO) {
        if(exchangeRateReqDTO.getRate() == null)
            throw new BadParameterException("ОТСУТСТВУЕТ НУЖНОЕ ПОЛЕ ФОРМЫ");
        if(!stringUtils.isCodeValid(exchangeRateReqDTO.getBaseCurrencyCode()))
            throw new BadParameterException("ОТСУТСТВУЕТ НУЖНОЕ ПОЛЕ ФОРМЫ (BaseCurrencyCode не валидный)");
        if(!stringUtils.isCodeValid(exchangeRateReqDTO.getTargetCurrencyCode()))
            throw new BadParameterException("ОТСУТСТВУЕТ НУЖНОЕ ПОЛЕ ФОРМЫ (TargetCurrencyCode не валидный)");
        return true;
    }

}
