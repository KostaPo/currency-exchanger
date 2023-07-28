package ru.kostapo.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateReqDTO {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;
}
