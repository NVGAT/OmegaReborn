package com.notverygoodatthis.omegareborn;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

import static com.notverygoodatthis.omegareborn.OmegaReborn.getOmegaItem;

//Best decision I ever made was creating the OmegaPlayer object
public class OmegaPlayer {
    //Player object
    Player player;
    //LifeOutcome enumerator that reports if settings the lives went through successfully
    public enum LifeOutcome {
        SUCCESS,
        FAILURE
    }

    //Constructor
    public OmegaPlayer(Player player) {
        this.player = player;
    }

    public int getLives() {
        //Method to get the current omega lives
        return OmegaReborn.lifeMap.get(player.getName());
    }

    public LifeOutcome setLives(int newLives) {
        //Method to set the current omega lives, let me walk you through it...
        try {
            //If the lives are less or equal to three (max lives)
            if(newLives <= 3) {
                //Then we remove the current value containing this player from the lifeMap object in the OmegaReborn class
                OmegaReborn.lifeMap.remove(player.getName(), OmegaReborn.lifeMap.get(player));
                //After that we replace it with the player name and the new lives
                OmegaReborn.lifeMap.put(player.getName(), newLives);
                //After that we save the config...
                Bukkit.getPluginManager().getPlugin("OmegaReborn").getConfig().set("players", new ArrayList<String>(OmegaReborn.lifeMap.keySet()));
                Bukkit.getPluginManager().getPlugin("OmegaReborn").getConfig().set("lives", new ArrayList<Integer>(OmegaReborn.lifeMap.values()));
                Bukkit.getPluginManager().getPlugin("OmegaReborn").saveConfig();
                //Update the tablist, log setting the player's lives and return LifeOutcome.SUCCESS
                updateTablist();
                Bukkit.getLogger().info(String.format("Settings the lives of %s to %d went through with status SUCCESS", player.getName(), newLives));
                return LifeOutcome.SUCCESS;
            } else {
                //However, if the new lives are not less or equal to three, we return FAILURE and log the error
                Bukkit.getLogger().info(String.format("Settings the lives of %s to %d went through with status FAILURE", player.getName(), newLives));
                return LifeOutcome.FAILURE;
            }
        } catch(Exception e) {
            //If any sort of exception occurs we automatically return FAILURE and log it to the console.
            Bukkit.getLogger().warning(String.format("An exception occured while setting the lives of %s. Stack trace:\n", player.getName()));
            e.printStackTrace();
            return LifeOutcome.FAILURE;
        }
    }

    //Player getter
    public Player getPlayer() {
        return player;
    }

    //Tablist updater
    public void updateTablist() {
        player.setPlayerListName(String.format("[%d] %s", getLives(), player.getName()));
    }

    //Bans the player for losing all of their lives
    public void omegaBan() {
        player.getInventory().clear();
        Random random = new Random();
        player.getWorld().dropItemNaturally(player.getLocation(), getOmegaItem(OmegaReborn.OmegaItemType.SHARD, random.nextInt(8)));
        String reason = String.format("§bYou've lost your last life to %s. Thank you for playing on the Omega SMP.", player.getKiller().getName());
        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason, null, "Omega SMP plugin");
        player.kickPlayer(reason);
    }

    //Bans the player for depositing all of their lives
    public void lifeBan() {
        player.getInventory().clear();
        Random random = new Random();
        player.getWorld().dropItemNaturally(player.getLocation(), getOmegaItem(OmegaReborn.OmegaItemType.SHARD, random.nextInt(8)));
        String reason = "You've withdrawn your last life. Thank you for playing on the Omega SMP.";
        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason, null, "Omega SMP plugin");
        player.kickPlayer(reason);
    }
}
