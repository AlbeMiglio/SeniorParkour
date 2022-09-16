package it.albemiglio.seniorparkour;

import it.albemiglio.seniorparkour.commands.ParkourCommand;
import it.albemiglio.seniorparkour.listeners.FlyListener;
import it.albemiglio.seniorparkour.listeners.JoinListener;
import it.albemiglio.seniorparkour.listeners.PlateListener;
import it.albemiglio.seniorparkour.services.*;
import it.albemiglio.seniorparkour.tasks.UpdateScoreboardTask;
import it.albemiglio.seniorparkour.utils.Misc;
import it.mycraft.powerlib.bukkit.config.ConfigManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SeniorParkour extends JavaPlugin {


    @Getter
    private FileService fileService;
    @Getter
    private DBService dbService;
    @Getter
    private ParkourService parkourService;
    @Getter
    private ScoreboardService scoreboardService;
    @Getter
    private StatsService statsService;
    @Getter
    private Misc misc;

    @Override
    public void onEnable() {
        registerInstances();
    }

    @Override
    public void onDisable() {
        this.getStatsService().stop();
        this.getDbService().stop();
    }

    private void registerInstances() {
        registerServices();
        registerCommands();
        registerListeners();
        registerTasks();
    }

    private void registerCommands() {
        getCommand("parkour").setExecutor(new ParkourCommand(this));
        getCommand("parkour").setTabCompleter(new ParkourCommand(this));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlateListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FlyListener(this), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
    }

    private void registerServices() {
        fileService = new FileService(this);
        dbService = new DBService(this);
        parkourService = new ParkourService(this);
        statsService = new StatsService(this);
        scoreboardService = new ScoreboardService(this);
        misc = new Misc(this);
    }

    private void registerTasks() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new UpdateScoreboardTask(this), 20L, 20L);
    }
}
