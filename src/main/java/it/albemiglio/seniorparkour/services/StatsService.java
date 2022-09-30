package it.albemiglio.seniorparkour.services;

import it.albemiglio.seniorparkour.SeniorParkour;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatsService extends Service {

    private final SeniorParkour main;
    @Getter
    private HashMap<UUID, HashMap<String, Long>> stats;

    public StatsService(SeniorParkour main) {
        this.main = main;
        this.start();
    }

    @Override
    protected void start() {
        this.stats = new HashMap<>();
        Connection conn = this.main.getDbService().getConnection();
        String query = "SELECT * FROM `stats`;";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String identifier = rs.getString("identifier");
                UUID uuid = UUID.fromString(identifier.split(":")[0]);
                String parkourName = identifier.split(":")[1];
                Long elapsedTime = rs.getLong("bestTime");
                updateTime(uuid, parkourName, elapsedTime, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveStats(UUID uuid, String parkourName, Long time) {
            Connection conn = this.main.getDbService().getConnection();
            String query = "INSERT INTO `stats` VALUES(?,?) ON DUPLICATE KEY UPDATE identifier=?, bestTime=?;";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, uuid + ":" + parkourName);
                statement.setLong(2, time);
                statement.setString(3, uuid + ":" + parkourName);
                statement.setLong(4, time);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public int getPositionOf(UUID uuid, String parkourName) {
        Stream<AbstractMap.SimpleEntry<UUID, Long>> sort =
                this.stats.entrySet().stream().filter((en) -> en.getValue().containsKey(parkourName))
                        .map((en) -> new AbstractMap.SimpleEntry<>(en.getKey(), en.getValue().get(parkourName)))
                        .sorted(Map.Entry.comparingByValue());
        int count = 0;
        for (AbstractMap.SimpleEntry<UUID, Long> entry : sort.collect(Collectors.toList())) {
            count++;
            if (entry.getKey().compareTo(uuid) == 0) {
                return count;
            }
        }
        return 0;
    }

    public List<AbstractMap.SimpleEntry<UUID, Long>> getTopStatsOf(int limit, String parkourName) {
        return this.stats.entrySet().stream().filter((en) -> en.getValue().containsKey(parkourName))
                        .map((en) -> new AbstractMap.SimpleEntry<>(en.getKey(), en.getValue().get(parkourName)))
                        .sorted(Map.Entry.comparingByValue()).limit(limit).collect(Collectors.toList());
    }

    public void deleteStats(UUID uuid) {
        Connection conn = this.main.getDbService().getConnection();
        String query = "DELETE FROM stats WHERE identifier LIKE '?%'";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        this.stats.remove(uuid);
    }

    public void deleteStats(UUID uuid, String parkourName) {
        Connection conn = this.main.getDbService().getConnection();
        String query = "DELETE FROM stats WHERE identifier=?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, uuid + ":" + parkourName);
            statement.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        this.stats.get(uuid).remove(parkourName);
    }

    public void deleteStats(String parkourName) {
        Connection conn = this.main.getDbService().getConnection();
        String query = "DELETE FROM stats WHERE identifier LIKE '%?'";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, parkourName);
            statement.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        this.stats.keySet().forEach((uuid) -> this.stats.get(uuid).remove(parkourName));
    }

    public void deleteStatsAsync(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(this.main, () -> deleteStats(uuid));
    }

    public void deleteStatsAsync(UUID uuid, String parkourName) {
        Bukkit.getScheduler().runTaskAsynchronously(this.main, () -> deleteStats(uuid, parkourName));
    }

    public void deleteStatsAsync(String parkourName) {
        Bukkit.getScheduler().runTaskAsynchronously(this.main, () -> deleteStats(parkourName));
    }

    public void deleteAll() {
        this.stats.keySet().forEach(this::deleteStats);
        Connection conn = this.main.getDbService().getConnection();
        try (PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE stats")) {
            statement.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(this.main, this::deleteAll);
    }

    public void saveAll() {
        this.stats.forEach(((uuid, statsMap) ->
                statsMap.forEach((parkourName, time) -> this.saveStats(uuid, parkourName, time))));
    }

    public void saveStatsAsync(UUID uuid, String parkourName, Long time) {
        Bukkit.getScheduler().runTaskAsynchronously(this.main, () -> this.saveStats(uuid, parkourName, time));
    }

    public void saveAllAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(this.main, this::saveAll);
    }

    public void updateTime(UUID uuid, String parkourName, Long time, boolean updateOnDB) {
        if (!this.stats.containsKey(uuid)) {
            this.stats.put(uuid, new HashMap<>());
        }
        if (this.stats.get(uuid).containsKey(parkourName) && this.stats.get(uuid).get(parkourName) <= time) {
            return;
        }
        this.stats.get(uuid).put(parkourName, time);
        if (updateOnDB) {
            saveStatsAsync(uuid, parkourName, time);
        }
    }

    @Override
    public void stop() {
        saveAll();
    }
}
