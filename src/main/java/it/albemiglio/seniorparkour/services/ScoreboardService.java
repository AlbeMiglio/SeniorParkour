package it.albemiglio.seniorparkour.services;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.CurrentParkour;
import it.albemiglio.seniorparkour.objects.Service;
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

public class ScoreboardService extends Service {

    private SeniorParkour main;
    private List<String> scoreboardLines;

    public ScoreboardService(SeniorParkour main) {
        this.main = main;
        this.start();
    }

    @Override
    protected void start() {
        this.scoreboardLines = this.main.getFileService().getConfig().getStringList("scoreboard");
    }

    public void sendScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        if (this.main.getParkourService().getCurrentPlayers().containsKey(player.getUniqueId())) {
            Message linesMsg = new Message(scoreboardLines);
            CurrentParkour current = this.main.getParkourService().getCurrentPlayers().get(player.getUniqueId());
            String parkourName = current.getParkourName();
            long elapsedTime = Instant.now().getEpochSecond() - current.getStartingTime();
            String time = TimeUtils.formattedTime(elapsedTime);
            String bestTime = "None";
            if (this.main.getStatsService().getStats().get(player.getUniqueId()).containsKey(parkourName)) {
                bestTime = TimeUtils.formattedTime(this.main.getStatsService()
                        .getStats().get(player.getUniqueId()).get(parkourName));
            }
            int position = this.main.getStatsService().getPositionOf(player.getUniqueId(), parkourName);
            linesMsg = linesMsg
                    .addPlaceHolder("%parkour_name%", parkourName)
                    .addPlaceHolder("%parkour_time%", time)
                    .addPlaceHolder("%parkour_position%", position)
                    .addPlaceHolder("%parkour_position_time%", bestTime);
            List<String> formattedLines = linesMsg.hex().getTextList();
            for (int i = 0; i < formattedLines.size(); i++) {
                Objective o = scoreboard.registerNewObjective("line" + (i + 1), "dummy", formattedLines.get(i));
                Score s = o.getScore(formattedLines.get(i));
                s.setScore(15 - i);
                o.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
        }
        player.setScoreboard(scoreboard);
    }

    @Override
    protected void stop() {

    }
}
