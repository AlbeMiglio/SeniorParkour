package it.albemiglio.seniorparkour.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocUtils {

    public static String serialize(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ();
    }

    public static Location deserialize(String s) {
        String[] info = (String[]) Arrays.stream(s.split(":")).toArray();
        return new Location(Bukkit.getWorld(info[0]),
                Double.parseDouble(info[1]), Double.parseDouble(info[2]), Double.parseDouble(info[3]));
    }

    public static List<String> serializeList(List<Location> locationList) {
        return locationList.stream().map(LocUtils::serialize).collect(Collectors.toList());
    }

    public static List<Location> deserializeList(List<String> strings) {
        return strings.stream().map(LocUtils::deserialize).collect(Collectors.toList());
    }

    public static boolean isDeserializable(String s) {
        try {
            deserialize(s);
        }
        catch (Exception ex) {
            return false;
        }
        return true;
    }
}
