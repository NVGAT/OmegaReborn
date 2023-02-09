package com.notverygoodatthis.omegareborn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OmegaWithdrawCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            OmegaPlayer player = new OmegaPlayer((Player) sender);
            if(!(Integer.parseInt(args[0]) > player.getLives())) {
                player.setLives(player.getLives() - 1);
                player.getPlayer().getWorld().dropItemNaturally(player.getPlayer().getLocation(),
                        OmegaReborn.getOmegaItem(OmegaReborn.OmegaItemType.LIFE, Integer.parseInt(args[0])));
                if(player.getLives() < 1) {
                    player.lifeBan();
                }
            } else {
                player.getPlayer().sendMessage("§l§bYou can't deposit more lives than you have.");
            }
        }
        return false;
    }
}
