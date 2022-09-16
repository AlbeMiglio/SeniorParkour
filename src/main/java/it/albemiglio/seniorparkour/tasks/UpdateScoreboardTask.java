package it.albemiglio.seniorparkour.tasks;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.CurrentParkour;
import it.albemiglio.seniorparkour.utils.TimeUtils;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.time.Instant;
import java.util.List;

public class UpdateScoreboardTask implements Runnable {

    private SeniorParkour main;

    public UpdateScoreboardTask(SeniorParkour main) {
        this.main = main;
    }

    public void run() {
        Bukkit.getOnlinePlayers().forEach((p) ->
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.main, () ->
                        this.main.getScoreboardService().sendScoreboard(p)));
    }
}
