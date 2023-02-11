package com.notverygoodatthis.omegareborn;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.notverygoodatthis.omegareborn.OmegaReborn.getOmegaItem;

public class OmegaPlayer {
    Player player;
    public enum LifeOutcome {
        SUCCESS,
        FAILURE
    }

    public OmegaPlayer(Player player) {
        this.player = player;
    }

    public int getLives() {
        return OmegaReborn.lifeMap.get(player.getName());
    }

    public LifeOutcome setLives(int newLives) {
        if(newLives <= 5) {
            OmegaReborn.lifeMap.remove(player.getName(), OmegaReborn.lifeMap.get(player));
            OmegaReborn.lifeMap.put(player.getName(), newLives);
            Bukkit.getPluginManager().getPlugin("OmegaReborn").getConfig().set("players", new ArrayList<String>(OmegaReborn.lifeMap.keySet()));
            Bukkit.getPluginManager().getPlugin("OmegaReborn").getConfig().set("lives", new ArrayList<Integer>(OmegaReborn.lifeMap.values()));
            Bukkit.getPluginManager().getPlugin("OmegaReborn").saveConfig();
            updateTablist();
            Bukkit.getLogger().info(String.format("Settings the lives of %s to %d went through with status SUCCESS", player.getName(), newLives));
            return LifeOutcome.SUCCESS;
        } else {
            Bukkit.getLogger().info(String.format("Settings the lives of %s to %d went through with status FAILURE", player.getName(), newLives));
            return LifeOutcome.FAILURE;
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void updateTablist() {
        player.setPlayerListName(String.format("[%d] %s", getLives(), player.getName()));
    }

    public void omegaBan() {
        player.getInventory().clear();
        Random random = new Random();
        player.getWorld().dropItemNaturally(player.getLocation(), getOmegaItem(OmegaReborn.OmegaItemType.SHARD, random.nextInt(8)));
        String reason = String.format("§bYou've lost your last life to %s. Thank you for playing on the Omega SMP.", player.getKiller().getName());
        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason, null, "Omega SMP plugin");
        player.kickPlayer(reason);
    }

    public void lifeBan() {
        player.getInventory().clear();
        Random random = new Random();
        player.getWorld().dropItemNaturally(player.getLocation(), getOmegaItem(OmegaReborn.OmegaItemType.SHARD, random.nextInt(8)));
        String reason = "You've withdrawn your last life. Thank you for playing on the Omega SMP.";
        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason, null, "Omega SMP plugin");
        player.kickPlayer(reason);
    }
}
