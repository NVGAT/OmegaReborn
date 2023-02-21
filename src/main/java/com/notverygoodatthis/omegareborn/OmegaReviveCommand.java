package com.notverygoodatthis.omegareborn;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

//omegarevive command, crucial to the plugin
public class OmegaReviveCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //If the sender is a player
        if(sender instanceof Player) {
            //We cast the CommandSender to a Player and check if they're holding a revival item
            Player player = (Player) sender;
            if(player.getInventory().getItemInMainHand().getType() == OmegaReborn.getOmegaItem(OmegaReborn.OmegaItemType.HEAD, 1).getType()) {
                //Then we get the OfflinePlayer that they were mentioning in the command arguments
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                //If the mentioned player is banned by the Omega SMP plugin...
                if(p.isBanned() && Bukkit.getBanList(BanList.Type.NAME).getBanEntry(p.getName()).getSource().equals("Omega SMP plugin")) {
                    //Then we unban them and reset their lives to three
                    Bukkit.getBanList(BanList.Type.NAME).pardon(args[0]);
                    OmegaReborn.lifeMap.remove(p.getName(), OmegaReborn.lifeMap.get(p.getName()));
                    OmegaReborn.lifeMap.put(p.getName(), 3);
                    //After all of that, we send the player a message that they've revived said player and take away one revival item
                    sender.sendMessage(String.format("%sYou've successfully revived %s. If the revival process did not go through " +
                            "contact a server admin.", OmegaReborn.OMEGA_PREFIX, p.getName()));
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                    return true;
                }
            }
        } else {
            //If the sender isn't a player we automatically revive them, because this means that this command was executed through
            //the server console.
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            Bukkit.getBanList(BanList.Type.NAME).pardon(args[0]);
            OmegaReborn.lifeMap.remove(p.getName(), OmegaReborn.lifeMap.get(p.getName()));
            OmegaReborn.lifeMap.put(p.getName(), 3);
            sender.sendMessage(String.format("%sSuccessfully revived %s through the server console.", OmegaReborn.OMEGA_PREFIX, p.getName()));
            return true;
        }
        return false;
    }

    //Tab completer
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> bannedPlayers = new ArrayList<>();
        for(BanEntry entry : Bukkit.getBanList(BanList.Type.NAME).getBanEntries()) {
            bannedPlayers.add(entry.getTarget());
        }
        return bannedPlayers;
    }
}
