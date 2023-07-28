package ru.kostapo.mappers;

import ru.kostapo.models.Currency;
import ru.kostapo.models.ExchangeRate;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetMapper {
    public static Currency toCurrency(ResultSet resultSet) {
        try {
            return new Currency(
                    resultSet.getInt("id"),
                    resultSet.getString("code"),
                    resultSet.getString("fullName"),
                    resultSet.getString("sign"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ExchangeRate toExchangeRate(ResultSet resultSet) {
        try {
            return new ExchangeRate(
                    resultSet.getInt("id"),
                    resultSet.getInt("baseCurrencyId"),
                    resultSet.getInt("targetCurrencyId"),
                    resultSet.getBigDecimal("rate"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
