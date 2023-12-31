package ru.kostapo.repositories;

import ru.kostapo.common.exceptions.DublicationException;
import ru.kostapo.common.exceptions.NotFoundException;
import ru.kostapo.common.mapper.ResultSetMapper;
import ru.kostapo.common.repositories.CrudRepository;
import ru.kostapo.currency.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.sqlite.core.Codes.SQLITE_CONSTRAINT;

public class CurrencyRepository implements CrudRepository<Currency> {

    private final Connection connection;

    public CurrencyRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<List<Currency>> findAll() {
        final String query = "SELECT * FROM Currencies";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.isBeforeFirst()) {
                List<Currency> currencies = new ArrayList<>();
                while (resultSet.next())
                    currencies.add(ResultSetMapper.toCurrency(resultSet));
                return Optional.of(currencies);
            } else {
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<Currency> findById(Integer id) {
        final String query = "SELECT * FROM Currencies WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.isBeforeFirst()) {
                if (resultSet.next())
                    return Optional.of(ResultSetMapper.toCurrency(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    @Override
    public Currency save(Currency currency) {
        final String query = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.execute();
        } catch (SQLException ex) {
            if (SQLITE_CONSTRAINT == ex.getErrorCode())
                throw new DublicationException("ВАЛЮТА С ТАКИМ КОДОМ УЖЕ СУЩЕСТВУЕТ");
            throw new RuntimeException(ex);
        }
        return findByCode(currency.getCode())
                .orElseThrow(() -> new NotFoundException("НЕ ПОЛУЧАЕТСЯ НАЙТИ ПОСЛЕ СОХРАНЕНИЯ"));
    }

    @Override
    public Currency update(Currency entity) {
        return null; //TODO доделать
    }

    @Override
    public void delete(Integer id) {
        final String query = "DELETE FROM Currencies WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<Currency> findByCode(String code) {
        final String query = "SELECT * FROM Currencies WHERE Code = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, code);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.isBeforeFirst()) {
                if (resultSet.next())
                    return Optional.of(ResultSetMapper.toCurrency(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }
}
