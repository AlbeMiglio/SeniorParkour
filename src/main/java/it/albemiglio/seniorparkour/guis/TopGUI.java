package it.albemiglio.seniorparkour.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.albemiglio.seniorparkour.utils.TimeUtils;
import it.mycraft.powerlib.bukkit.item.ItemBuilder;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TopGUI extends GUI {

    private ChestGui gui;
    private Parkour parkour;
    private SeniorParkour main;

    public TopGUI(SeniorParkour main, Parkour parkour) {
        this.main = main;
        this.parkour = parkour;
        loadGUI();
    }

    public void loadGUI() {
        String title = getMessage("gui.top.title")
                .addPlaceHolder("%parkour", parkour.getName())
                .getText();
        this.gui = new ChestGui(3, title);
        PaginatedPane paginatedPane = new PaginatedPane(0, 1, 9, 1);
        List<ItemStack> skulls = new ArrayList<>();
        List<AbstractMap.SimpleEntry<UUID, Long>> entries = this.main.getStatsService().getTopStatsOf(100, parkour.getName());
        for (int j = 0; j < entries.size(); j++) {
            ItemStack i = new ItemStack(Material.PLAYER_HEAD);
            AbstractMap.SimpleEntry<UUID, Long> en = entries.get(j);
            SkullMeta meta = (SkullMeta) i.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(en.getKey()));
            meta.setDisplayName(getMessage("gui.top.player-button.name")
                    .addPlaceHolder("%n", j + 1)
                    .addPlaceHolder("%player", Bukkit.getOfflinePlayer(en.getKey()).getName())
                    .addPlaceHolder("%time", TimeUtils.formattedTime(en.getValue())).getText());
            meta.setLore(getMessageList("gui.top.player-button.lore")
                    .addPlaceHolder("%n", j + 1)
                    .addPlaceHolder("%player", Bukkit.getOfflinePlayer(en.getKey()).getName())
                    .addPlaceHolder("%time", TimeUtils.formattedTime(en.getValue())).getTextList());
            i.setItemMeta(meta);
            skulls.add(i);
        }
        paginatedPane.populateWithGuiItems(skulls.stream().map((i) -> new GuiItem(i, event -> event.setCancelled(true))).collect(Collectors.toList()));
        gui.addPane(paginatedPane);
        StaticPane pane = new StaticPane(0, 2, 9, 1);
        pane.addItem(new GuiItem(new ItemBuilder().fromConfig(this.main.getFileService().getConfig(), "gui.top.previous-page")
                .addPlaceHolder("%page", paginatedPane.getPage())
                .addPlaceHolder("%pages", paginatedPane.getPages()).build(), event -> {
            paginatedPane.setPage(Math.max(1, paginatedPane.getPage() - 1));
            gui.update();
        }), 1, 1);
        pane.addItem(new GuiItem(new ItemBuilder().fromConfig(this.main.getFileService().getConfig(), "gui.top.next-page")
                .addPlaceHolder("%page", paginatedPane.getPage())
                .addPlaceHolder("%pages", paginatedPane.getPages()).build(), event -> {
            paginatedPane.setPage(Math.min(paginatedPane.getPages(), paginatedPane.getPage() + 1));
            gui.update();
        }), 9, 1);
        gui.addPane(pane);
    }

    public void showGUI(Player p) {
        this.gui.show(p);
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getConfig().getString(path)).hex();
    }

    private Message getMessageList(String path) {
        return new Message(this.main.getFileService().getConfig().getStringList(path)).hex();
    }
}
