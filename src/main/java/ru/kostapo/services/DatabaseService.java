package ru.kostapo.services;

import java.sql.Connection;
import java.sql.ResultSet;

public interface DatabaseService {

    Connection getDataBaseConnection();

}
