package ru.kostapo.repositories;

import ru.kostapo.exceptions.DublicationException;
import ru.kostapo.exceptions.NotFoundException;
import ru.kostapo.mappers.ResultSetMapper;
import ru.kostapo.models.ExchangeRate;
import ru.kostapo.services.DatabaseServiceImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.sqlite.core.Codes.SQLITE_CONSTRAINT;

public class ExchangeRateRepository implements CrudRepository<ExchangeRate>{

    private final DatabaseServiceImpl databaseServiceImpl;
    private final ResultSetMapper resultSetMapper;

    public ExchangeRateRepository() {
        this.databaseServiceImpl = new DatabaseServiceImpl();
        this.resultSetMapper = new ResultSetMapper();
    }

    @Override
    public Optional<List<ExchangeRate>> findAll() {
        final String query = "SELECT * FROM ExchangeRates";
        try (Connection connection = databaseServiceImpl.getDataBaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.isBeforeFirst()) {
                List<ExchangeRate> exchangeRates = new ArrayList<>();
                while (resultSet.next()) {
                    exchangeRates.add(ResultSetMapper.toExchangeRate(resultSet));
                }
                return Optional.of(exchangeRates);
            } else {
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Integer id) {
        final String query = "SELECT * FROM ExchangeRates WHERE id = " + id.toString();
        try (Connection connection = databaseServiceImpl.getDataBaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.isBeforeFirst()) {
                if (resultSet.next())
                    return Optional.of(ResultSetMapper.toExchangeRate(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        final String query = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?,?,?)";
        try (Connection connection = databaseServiceImpl.getDataBaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, exchangeRate.getBaseCurrencyId());
            statement.setInt(2, exchangeRate.getTargetCurrencyId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            statement.execute();
        } catch (SQLException ex) {
            if(SQLITE_CONSTRAINT == ex.getErrorCode())
                throw new DublicationException("ВАЛЮТНАЯ ПАРА С ТАКИМ КОДОМ УЖЕ СУЩЕСТВУЕТ");
            throw new RuntimeException(ex);
        }
        return findByIntPair(exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId())
                .orElseThrow(() -> new NotFoundException("НЕ ПОЛУЧАЕТСЯ НАЙТИ ПОСЛЕ СОХРАНЕНИЯ"));
    }

    @Override
    public ExchangeRate update(ExchangeRate exchangeRate) {
        final String query = "UPDATE ExchangeRates " +
                "SET rate = "+exchangeRate.getRate()+
                " WHERE BaseCurrencyId = +"+exchangeRate.getBaseCurrencyId()+
                " AND TargetCurrencyId = "+exchangeRate.getTargetCurrencyId();
        try (Connection connection = databaseServiceImpl.getDataBaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
        } catch (SQLException ex) {
            throw new NotFoundException("ВАЛЮТНАЯ ПАРА ОТСУТСТВУЕТ В БД");
        }
        return findByIntPair(exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId())
                .orElseThrow(() -> new NotFoundException("НЕ ПОЛУЧАЕТСЯ НАЙТИ ПОСЛЕ ОБНОВЛЕНИЯ"));
    }

    @Override
    public void delete(Integer id) {
        final String query = "DELETE FROM ExchangeRates WHERE id = " + id.toString();
        try (Connection connection = databaseServiceImpl.getDataBaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<ExchangeRate> findByIntPair(Integer base, Integer target) {
        final String query = "SELECT * FROM ExchangeRates " +
                "WHERE BaseCurrencyId = " + base + " AND TargetCurrencyId = " + target;
        try (Connection connection = databaseServiceImpl.getDataBaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.isBeforeFirst()) {
                if (resultSet.next())
                    return Optional.of(ResultSetMapper.toExchangeRate(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    public Optional<ExchangeRate> findByStrPair(String base, String target) {
        final String query = "SELECT er.* " +
                "FROM ExchangeRates er " +
                "JOIN Currencies base ON er.baseCurrencyId = base.id " +
                "JOIN Currencies target ON er.targetCurrencyId = target.id " +
                "WHERE base.code = '"+base+"' AND target.code = '"+target+"'";
        try (Connection connection = databaseServiceImpl.getDataBaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.isBeforeFirst()) {
                if (resultSet.next())
                    return Optional.of(ResultSetMapper.toExchangeRate(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    public boolean isContain(String base, String target) {
        final String query = "SELECT COUNT(*) > 0 AS contain " +
                "FROM ExchangeRates " +
                "WHERE BaseCurrencyId='"+base+"' " +
                "AND TargetCurrencyId ='"+target+"'";
        try (Connection connection = databaseServiceImpl.getDataBaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                return resultSet.getBoolean("contain");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return false;
    }
}
