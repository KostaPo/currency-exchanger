package ru.kostapo.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Currency {
    private Integer id;
    @NonNull
    private String code;
    @NonNull
    private String fullName;
    @NonNull
    private String sign;
}
