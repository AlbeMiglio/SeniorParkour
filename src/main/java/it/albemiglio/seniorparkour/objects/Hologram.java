package it.albemiglio.seniorparkour.objects;

import it.mycraft.powerlib.common.chat.Message;
import lombok.Getter;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * N.B.: as it's developed for skill-test purposes, this only supports 1.16.5!
 * This code is not meant to be used onto production servers!
 */
public class Hologram {

    public Hologram(Location location, List<String> lines) {
        this.location = location;
        this.lines = lines;
        init();
    }

    private final Location location;
    private List<EntityArmorStand> entities;
    @Getter
    private final List<String> lines;

    private void init() {
        this.entities = new ArrayList<>();
        Location loc = location;
        EntityArmorStand stand;
        for(String line : lines) {
             stand = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(),
                    loc.getX(), loc.getY(), loc.getZ());
            stand.setInvisible(true);
            stand.setNoGravity(true);
            stand.setCustomNameVisible(true);
            stand.setCustomName(IChatBaseComponent.ChatSerializer
                    .a(new Message("{\"text\": \"%label\"}").addPlaceHolder("%label", line).hex().getText()));
            stand.setInvulnerable(true);
            entities.add(stand);
            loc.subtract(0, 0.5, 0);
        }
    }

    public void show(Player player) {
        PlayerConnection con = ((CraftPlayer) player).getHandle().playerConnection;
        for(EntityArmorStand hologram : entities) {
            con.sendPacket(new PacketPlayOutSpawnEntity(hologram, 78));
            con.sendPacket(new PacketPlayOutEntityMetadata(hologram.getId(), hologram.getDataWatcher(), false));
        }
    }

    public void showAll() {
        Bukkit.getOnlinePlayers().stream().filter((p) -> p.getWorld() == location.getWorld()).forEach(this::show);
    }

    public void hide(Player player) {
        PlayerConnection con = ((CraftPlayer) player).getHandle().playerConnection;
        for(EntityArmorStand hologram : entities) {
            con.sendPacket(new PacketPlayOutEntityDestroy(hologram.getId()));
        }
    }

    public void hideAll() {
        Bukkit.getOnlinePlayers().forEach(this::hide);
    }
}
