package ru.kostapo.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyResDTO {
    private Integer id;
    private String code;
    private String name;
    private String sign;
}
