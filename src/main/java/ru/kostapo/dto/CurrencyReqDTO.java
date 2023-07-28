package ru.kostapo.dto;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyReqDTO {
    private String code;
    private String name;
    private String sign;
}


