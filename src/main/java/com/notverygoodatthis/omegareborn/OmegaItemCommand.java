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
    //Admin command to get any omega item
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            //If the sender is a player, we try to parse the arguments they've provided into an OmegaItemType enumerator and an integer
            Player p = (Player) sender;
            try {
                p.getInventory().addItem(OmegaReborn.getOmegaItem(OmegaReborn.OmegaItemType.valueOf(args[0]), Integer.parseInt(args[1])));
                p.sendMessage("§l§bGave you a " + args[0]);
                return true;
            } catch(NullPointerException e) {
                //Catch statements to properly handle exceptions
                p.sendMessage("§l§bInvalid item type");
            } catch(IllegalArgumentException e) {
                p.sendMessage("§l§bInvalid item type");
            }
        }
        return false;
    }

    //Tab completer with all of the OmegaItemType values
    //TODO: automate this so I don't have to update this list every time I update OmegaItemType
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        String[] enumValues = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS", "SWORD", "BOW",
                "PICKAXE", "APPLE", "SHARD", "LIFE", "HEAD"};
        return new ArrayList<String>(Arrays.asList(enumValues));
    }
}
