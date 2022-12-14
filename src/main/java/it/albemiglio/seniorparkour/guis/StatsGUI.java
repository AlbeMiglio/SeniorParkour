package it.albemiglio.seniorparkour.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.utils.TimeUtils;
import it.mycraft.powerlib.bukkit.item.ItemBuilder;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class StatsGUI extends GUI {

    private OfflinePlayer player;
    private ChestGui gui;
    private final SeniorParkour main;

    public StatsGUI(SeniorParkour main, OfflinePlayer player) {
        this.main = main;
        this.player = player;
        this.loadGUI();
    }

    public void loadGUI() {
        String title = new Message(this.main.getFileService().getConfig().getString("gui.stats.title"))
                .addPlaceHolder("%player", player.getName()).hex().getText();
        this.gui = new ChestGui(5, title);
        StaticPane pane = new StaticPane(0, 0, 9, 5);
        int i = 0;
        for (String parkour : this.main.getStatsService().getStats().get(player.getUniqueId()).keySet()) {
            int x = i % 9;
            int y = i / 9;
            pane.addItem(new GuiItem(new ItemBuilder()
                    .fromConfig(this.main.getConfig(), "gui.stats.parkour-info-button")
                    .addPlaceHolder("%position", this.main.getStatsService()
                            .getPositionOf(player.getUniqueId(), parkour))
                    .addPlaceHolder("%player", player.getName())
                    .addPlaceHolder("%time", TimeUtils.formattedTime(this.main.getStatsService()
                            .getStats().get(player.getUniqueId()).get(parkour))).hex().build(), event ->
                    event.setCancelled(true)), x, y);
            i++;
        }
        this.gui.setOnGlobalClick(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
        this.gui.addPane(pane);
    }

    public void showGUI(Player p) {
        this.gui.show(p);
    }
}
