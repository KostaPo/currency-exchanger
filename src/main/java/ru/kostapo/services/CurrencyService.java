package ru.kostapo.services;

import ru.kostapo.dto.CurrencyReqDTO;
import ru.kostapo.dto.CurrencyResDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CurrencyService {

    Optional<List<CurrencyResDTO>> findAllCurrencies();

    Optional<CurrencyResDTO> findByCode(String code);

    Optional<CurrencyResDTO> findById(Integer id);

    CurrencyResDTO save(CurrencyReqDTO currencyReqDTO);

    void delete(Integer id);

    boolean isRequestDataValid(CurrencyReqDTO currencyReqDTO);

    boolean isContain(String code);
}
