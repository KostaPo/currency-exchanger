package ru.kostapo.services;

import ru.kostapo.exchange.dto.ExchangeReqDTO;
import ru.kostapo.exchange.dto.ExchangeResDTO;

public interface ExchangeService {
    ExchangeResDTO getExchange(ExchangeReqDTO exchangeReqDTO);
    boolean isRequestDataValid(ExchangeReqDTO exchangeReqDTO);
}
