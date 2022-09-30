package it.albemiglio.seniorparkour.commands.subcommands;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.guis.StatsGUI;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Stats implements Subcommand {

    private SeniorParkour main;

    public Stats(SeniorParkour main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length < 2) {
            getMessage("correct-usage-stats").send(sender);
            return false;
        }
        if(!this.main.getStatsService().getStats().containsKey(Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
            getMessage("player-not-found").send(sender);
            return false;
        }
        new StatsGUI(this.main, Bukkit.getOfflinePlayer(args[1])).showGUI((Player) sender);
        return false;
    }

    @Override
    public String getPermission() {
        return "parkour.stats";
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path)).hex();
    }
}
