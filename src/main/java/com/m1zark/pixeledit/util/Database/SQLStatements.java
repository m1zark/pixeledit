package com.m1zark.pixeledit.util.Database;

import com.google.common.collect.Lists;
import com.m1zark.pixeledit.util.Log.Log;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SQLStatements {
    private String mainTable;

    public SQLStatements(String mainTable) {
        this.mainTable = mainTable;
    }

    public void createTables() {
        try(Connection connection = DataSource.getConnection()) {
            if (connection == null || connection.isClosed()) throw new IllegalStateException("PokeBuilder DB connection is null");

            try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + this.mainTable + "` (ID INTEGER NOT NULL AUTO_INCREMENT, description MEDIUMTEXT, timestamp MEDIUMTEXT, owner CHAR(36), PRIMARY KEY(ID))")) {
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Log> getAllLogs() {
        List<Log> list = new ArrayList<>();

        try(Connection connection = DataSource.getConnection()) {
            if (connection == null || connection.isClosed()) throw new IllegalStateException("PokeBuilder DB connection is null");

            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + this.mainTable + "` ORDER BY ID DESC")) {
                ResultSet logs = statement.executeQuery();
                while(logs.next()) {
                    int id = logs.getInt("ID");
                    String desc = logs.getString("description");
                    String stamp = logs.getString("timestamp");
                    UUID owner = UUID.fromString(logs.getString("owner"));

                    Log log = new Log(id,desc,stamp,owner);
                    list.add(log);
                }

                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }

    public void addLog(HashMap log, Player player) {
        try(Connection connection = DataSource.getConnection()) {
            if (connection == null || connection.isClosed()) throw new IllegalStateException("PokeBuilder DB connection is null");

            try(PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + this.mainTable + "` (description, timestamp, owner) VALUES (?, ?, ?)")) {
                SimpleDateFormat ft = new SimpleDateFormat("MMMMM d yyyy h:mm a z");
                String owner = player.getUniqueId().toString();

                statement.setString(1, log.toString());
                statement.setString(2, ft.format(new Date()));
                statement.setString(3, owner);
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteLog(Log log) {
        try(Connection connection = DataSource.getConnection()) {
            if (connection == null || connection.isClosed()) throw new IllegalStateException("PokeBuilder DB connection is null");

            try(PreparedStatement statement = connection.prepareStatement("DELETE FROM `" + this.mainTable + "` WHERE ID = " + log.getLogID() + "")) {
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
