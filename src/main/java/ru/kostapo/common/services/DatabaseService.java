package ru.kostapo.common;

import java.sql.Connection;

public interface DatabaseService {
    Connection getDataBaseConnection();
}
