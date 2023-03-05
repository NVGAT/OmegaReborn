package com.notverygoodatthis.omegareborn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//Command to withdraw lives in an item form
public class OmegaWithdrawCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //If the sender is a player
        if(sender instanceof Player) {
            //We wrap it in an OmegaPlayer
            OmegaPlayer player = new OmegaPlayer((Player) sender);
            //If the amount of lives is correct, we drop the lives and deduct the player's lives
            if(!(Integer.parseInt(args[0]) > player.getLives())) {
                player.setLives(player.getLives() - Integer.parseInt(args[0]));
                player.getPlayer().getWorld().dropItemNaturally(player.getPlayer().getLocation(),
                        OmegaReborn.getOmegaItem(OmegaReborn.OmegaItemType.LIFE, Integer.parseInt(args[0])));
                if(player.getLives() < 1) {
                    //If the lives are less than one, we ban the player. Farewell...
                    player.lifeBan();
                }
            } else {
                //If the amount of lives is not okay, we warn the player about this
                player.getPlayer().sendMessage("§l§bYou can't withdraw more lives than you have.");
            }
        }
        return false;
    }
}
