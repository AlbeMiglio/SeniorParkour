package it.albemiglio.seniorparkour.services;

import com.google.common.io.Files;
import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Service;
import it.mycraft.powerlib.bukkit.config.ConfigManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

public class FileService extends Service {

    private final SeniorParkour main;
    private ConfigManager configManager;

    public FileService(SeniorParkour main) {
        this.main = main;
        this.configManager = new ConfigManager(this.main);
        this.start();
    }

    @Override
    protected void start() {
        this.configManager.create("config.yml");
        this.configManager.create("messages.yml");
        File parkoursFolder = new File(this.main.getDataFolder() + "/SeniorParkour/parkours");
        File[] files = parkoursFolder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                this.createParkour(Files.getNameWithoutExtension(files[i].getName()));
            }
        }
    }

    public void reload() {
        this.configManager.reload("config.yml");
        this.configManager.reload("messages.yml");
        for(String parkour : this.main.getParkourService().getParkours().keySet()) {
            this.configManager.reload("parkours/"+parkour+".yml");
        }
    }

    public void createParkour(String name) {
        this.configManager.create("parkours/"+name+".yml", "parkour.yml");
    }

    public void saveParkour(String name) {
        this.configManager.save("parkours/"+name+".yml");
    }

    public FileConfiguration getParkour(String name) {
        return this.configManager.get("parkours/"+name+".yml");
    }
    public FileConfiguration getConfig() {
        return this.configManager.get("config.yml");
    }

    public FileConfiguration getMessages() {
        return this.configManager.get("messages.yml");
    }

    @Override
    protected void stop() { }
}
