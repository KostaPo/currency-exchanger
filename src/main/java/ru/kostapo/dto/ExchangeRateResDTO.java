package ru.kostapo.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateResDTO {
    private Integer id;
    private CurrencyResDTO baseCurrency;
    private CurrencyResDTO targetCurrency;
    private BigDecimal rate;
}
