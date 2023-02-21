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

public class OmegaReviveCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.getInventory().getItemInMainHand().getType() == OmegaReborn.getOmegaItem(OmegaReborn.OmegaItemType.HEAD, 1).getType()) {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                if(p.isBanned() && Bukkit.getBanList(BanList.Type.NAME).getBanEntry(p.getName()).getSource().equals("Omega SMP plugin")) {
                    Bukkit.getBanList(BanList.Type.NAME).pardon(args[0]);
                    OmegaReborn.lifeMap.remove(p.getName(), OmegaReborn.lifeMap.get(p.getName()));
                    OmegaReborn.lifeMap.put(p.getName(), 3);
                    sender.sendMessage(String.format("%sYou've successfully revived %s. If the revival process did not go through " +
                            "contact a server admin.", OmegaReborn.OMEGA_PREFIX, p.getName()));
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                    return true;
                }
            }
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            Bukkit.getBanList(BanList.Type.NAME).pardon(args[0]);
            OmegaReborn.lifeMap.remove(p.getName(), OmegaReborn.lifeMap.get(p.getName()));
            OmegaReborn.lifeMap.put(p.getName(), 3);
            sender.sendMessage(String.format("%sSuccessfully revived %s through the server console.", OmegaReborn.OMEGA_PREFIX, p.getName()));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> bannedPlayers = new ArrayList<>();
        for(BanEntry entry : Bukkit.getBanList(BanList.Type.NAME).getBanEntries()) {
            bannedPlayers.add(entry.getTarget());
        }
        return bannedPlayers;
    }
}
