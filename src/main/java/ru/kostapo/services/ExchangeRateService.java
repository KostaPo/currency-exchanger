package ru.kostapo.services;

import ru.kostapo.dto.CurrencyResDTO;
import ru.kostapo.dto.ExchangeRateReqDTO;
import ru.kostapo.dto.ExchangeRateResDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateService {

    Optional<List<ExchangeRateResDTO>> findAllExchangeRates();

    Optional<ExchangeRateResDTO> findByCodes(String base, String target);

    Optional<ExchangeRateResDTO> findById(Integer id);

    ExchangeRateResDTO update(ExchangeRateReqDTO exchangeRateReqDTO);

    ExchangeRateResDTO save(ExchangeRateReqDTO exchangeRateReqDTO);

    void delete(Integer id);

    boolean isContain(String base, String target);

    boolean isRequestDataValid(ExchangeRateReqDTO exchangeRateReqDTO);

}
