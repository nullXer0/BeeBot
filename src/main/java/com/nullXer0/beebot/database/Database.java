package com.nullXer0.beebot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database
{
    private static HikariDataSource dataSource;

    public static void initialize(String jdbcUrl, String username, String password)
    {
        if (dataSource != null) {
            throw new IllegalStateException("Database already initialized");
        }

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClassName("org.postgresql.ds.PGSimpleDataSource");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(10);
    }

    public static void createTables() throws SQLException
    {
        try (Connection connection = getConnection()) {

        }
    }

    public static Connection getConnection() throws SQLException
    {
        if (dataSource == null) {
            throw new IllegalStateException("Database not initialized");
        }
        return dataSource.getConnection();
    }
}
