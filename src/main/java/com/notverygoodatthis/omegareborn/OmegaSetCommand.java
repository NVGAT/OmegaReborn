package com.notverygoodatthis.omegareborn;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class OmegaSetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            OmegaPlayer p = new OmegaPlayer(Bukkit.getPlayerExact(args[0]));
            int newLives = Integer.parseInt(args[1]);
            if(p.setLives(newLives) == OmegaPlayer.LifeOutcome.SUCCESS) {
                sender.sendMessage(String.format("%sSuccessfully set the lives of %s to %d", OmegaReborn.OMEGA_PREFIX, p.getPlayer().getName(), newLives));
                return true;
            } else {
                sender.sendMessage(String.format("%sFailed to set the lives of %s to %d", OmegaReborn.OMEGA_PREFIX, p.getPlayer().getName(), newLives));
                return true;
            }
        } catch(NullPointerException e) {
            sender.sendMessage(OmegaReborn.OMEGA_PREFIX + "Invalid target");
        } catch(NumberFormatException e) {
            sender.sendMessage(OmegaReborn.OMEGA_PREFIX + "Invalid amount");
        }
        return false;
    }
}
