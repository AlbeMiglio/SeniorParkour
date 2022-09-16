package it.albemiglio.seniorparkour.services;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Service;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
public class DBService extends Service {

    private HikariDataSource hikariDataSource;
    private SeniorParkour main;
    private final String username;
    private final String hostname;
    private final String password;
    private final String database;
    private final boolean useSSL;
    private final int port;

    public DBService(SeniorParkour main) {
        this.main = main;
        ConfigurationSection db = this.main.getFileService().getConfig().getConfigurationSection("database");
        this.username = db.getString("username");
        this.hostname = db.getString("hostname");
        this.password = db.getString("password");
        this.database = db.getString("database");
        this.useSSL = db.getBoolean("ssl");
        this.port = db.getInt("port");
        this.start();
    }

    public void start() {
        this.connect();
    }

    void connect() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setPoolName("SeniorParkour");
        hikariConfig.addDataSourceProperty("useSSL", useSSL);
        hikariDataSource = new HikariDataSource(hikariConfig);

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS stats(" +
                    "identifier VARCHAR(64) NOT NULL PRIMARY KEY," +
                    "bestTime INT NOT NULL" +
                    ");");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.disconnect();
    }

    public void disconnect() {
        if (hikariDataSource != null)
            hikariDataSource.close();
    }

    public Connection getConnection() {
        if (hikariDataSource == null)
            throw new IllegalStateException("There isn't any connected Data Source!");
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

