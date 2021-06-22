package com.m1zark.pixeledit.util.Database;

import com.m1zark.pixeledit.PixelEdit;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class DataSource extends SQLStatements {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public DataSource(String mainTable) { super(mainTable); }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void shutdown() {
        if (ds != null) {
            ds.close();
        }
    }

    static {
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.addDataSourceProperty("URL", "jdbc:h2:" + PixelEdit.getInstance().getConfigDir() + "/pb_logs;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MSSQLServer");
        config.setPoolName("PokeBuilder");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(10);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(5000);
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(10));
        config.setConnectionTestQuery("/* PokeBuilder ping */ SELECT 1");
        config.setInitializationFailTimeout(1);
        ds = new HikariDataSource(config);
    }
}
