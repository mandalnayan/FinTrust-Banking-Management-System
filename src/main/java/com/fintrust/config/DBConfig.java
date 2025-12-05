package com.fintrust.config;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fintrust.dao.UserDetailsDAO;
import com.fintrust.dao.impl.UserDetailsDAOImpl;
import com.fintrust.db.DBConnection;

@Configuration
public class DBConfig {

    @Bean
    public Connection connection() throws SQLException {
        return DBConnection.getConnection(); // or a DataSource
    }

    @Bean
    public UserDetailsDAO userDAOImpl(Connection connection) {
        return new UserDetailsDAOImpl(connection);
    }
}
