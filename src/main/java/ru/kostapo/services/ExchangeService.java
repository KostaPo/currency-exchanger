package ru.kostapo.services;

import ru.kostapo.dto.ExchangeRateReqDTO;
import ru.kostapo.dto.ExchangeReqDTO;
import ru.kostapo.dto.ExchangeResDTO;

import java.util.Optional;

public interface ExchangeService {

    Optional<ExchangeResDTO> getExchange(ExchangeReqDTO exchangeReqDTO);

    boolean isRequestDataValid(ExchangeReqDTO exchangeReqDTO);

}
