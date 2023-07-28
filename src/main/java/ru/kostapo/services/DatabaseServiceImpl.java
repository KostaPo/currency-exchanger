package ru.kostapo.services;

import org.sqlite.SQLiteConfig;
import ru.kostapo.exceptions.DatabaseException;

import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;

public class DatabaseServiceImpl implements DatabaseService {

    @Override
    public Connection getDataBaseConnection() throws DatabaseException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("ДРАЙВЕР НЕ НАЙДЕН");
        }
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.setEncoding(SQLiteConfig.Encoding.UTF8);
        try {
            Connection connection = DriverManager.getConnection(getConnectionUrl(), config.toProperties());
            connection.setAutoCommit(true);
            return connection;

        } catch (SQLException e) {
            throw new DatabaseException("БАЗА ДАННЫХ НЕДОСТУПНА");
        }
    }

    private String getConnectionUrl() {
        URL url = getClass().getClassLoader().getResource("currency_exchange.db");
        if (url != null) {
            try {
                return String.format("jdbc:sqlite:" + url.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException("ОШИБКА СИНТАКСИСА URI");
            }
        }
        return null;
    }
}
