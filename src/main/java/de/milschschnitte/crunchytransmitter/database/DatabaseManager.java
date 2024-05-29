package de.milschschnitte.crunchytransmitter.database;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.milschschnitte.crunchytransmitter.ConfigLoader;

import java.sql.Connection;

public class DatabaseManager {
        public static Connection getConnection(ConfigLoader cl) throws SQLException, IOException {

        return DriverManager.getConnection(cl.getDatabaseUrl(), cl.getDatabaseUsername(), cl.getDatabasePassword());
    }
}
