package com.javarush.redis;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Language implements RedisDto {
    private String language;

    private Boolean isOfficial;

    private BigDecimal percentage;
}
