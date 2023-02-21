package com.notverygoodatthis.omegareborn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmegaItemCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            try {
                p.getInventory().addItem(OmegaReborn.getOmegaItem(OmegaReborn.OmegaItemType.valueOf(args[0]), Integer.parseInt(args[1])));
                p.sendMessage("§l§bGave you a " + args[0]);
            } catch(NullPointerException e) {
                p.sendMessage("§l§bInvalid item type");
            } catch(IllegalArgumentException e) {
                p.sendMessage("§l§bInvalid item type");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        String[] enumValues = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS", "SWORD", "BOW",
                "PICKAXE", "APPLE", "SHARD", "LIFE", "HEAD"};
        return new ArrayList<String>(Arrays.asList(enumValues));
    }
}
