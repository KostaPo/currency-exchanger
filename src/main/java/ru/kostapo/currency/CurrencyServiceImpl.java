package ru.kostapo.services;

import ru.kostapo.dto.CurrencyReqDTO;
import ru.kostapo.dto.CurrencyResDTO;
import ru.kostapo.common.exceptions.BadParameterException;
import ru.kostapo.currency.CurrencyMapper;
import ru.kostapo.currency.Currency;
import ru.kostapo.currency.CurrencyRepository;
import ru.kostapo.utils.StringUtils;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final StringUtils stringUtils;

    public CurrencyServiceImpl(Connection connection) {
        this.currencyRepository = new CurrencyRepository(connection);
        this.currencyMapper = new CurrencyMapper();
        this.stringUtils = new StringUtils();
    }

    @Override
    public Optional<List<CurrencyResDTO>> findAllCurrencies() {
        Optional<List<Currency>> currencies = currencyRepository.findAll();
        if(currencies.isPresent()) {
            List<CurrencyResDTO> list = currencyMapper.toDtoList(currencies.get());
            return Optional.of(list);
        }
        return Optional.empty();
    }

    @Override
    public Optional<CurrencyResDTO> findByCode(String code) {
        Optional<Currency> currency = currencyRepository.findByCode(code);
        if(currency.isPresent()) {
            CurrencyResDTO currencyResDTO = currencyMapper.toDTO(currency.get());
            return Optional.of(currencyResDTO);
        }
        return Optional.empty();
    }

    @Override
    public Optional<CurrencyResDTO> findById(Integer id) {
        Optional<Currency> currency = currencyRepository.findById(id);
        if(currency.isPresent()) {
            CurrencyResDTO currencyResDTO = currencyMapper.toDTO(currency.get());
            return Optional.of(currencyResDTO);
        }
        return Optional.empty();
    }

    @Override
    public CurrencyResDTO save(CurrencyReqDTO currencyReqDTO) {
        Currency currency = currencyRepository.save(currencyMapper.toModel(currencyReqDTO));
        return currencyMapper.toDTO(currency);
    }

    @Override
    public void delete(Integer id) {
        currencyRepository.delete(id);
    }

    @Override
    public boolean isRequestDataValid(CurrencyReqDTO currencyReqDTO) {
        if(currencyReqDTO.getCode() == null
                || currencyReqDTO.getName() == null
                || currencyReqDTO.getSign() == null) {
            throw new BadParameterException("ОТСУТСТВУЕТ НУЖНОЕ ПОЛЕ ФОРМЫ");
        }
        if(currencyReqDTO.getCode().isEmpty()
                || currencyReqDTO.getName().isEmpty()
                || currencyReqDTO.getSign().isEmpty())
            throw new BadParameterException("ОТСУТСТВУЕТ НУЖНОЕ ПОЛЕ ФОРМЫ");
        if(!stringUtils.isCodeValid(currencyReqDTO.getCode()))
            throw new BadParameterException("ОТСУТСТВУЕТ НУЖНОЕ ПОЛЕ ФОРМЫ (Code не валидный)");
        if(!stringUtils.isNameValid(currencyReqDTO.getName()))
            throw new BadParameterException("ОТСУТСТВУЕТ НУЖНОЕ ПОЛЕ ФОРМЫ (Name не валидный)");
        return true;
    }

}
