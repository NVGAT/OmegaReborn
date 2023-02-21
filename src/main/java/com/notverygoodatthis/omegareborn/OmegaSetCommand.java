package com.notverygoodatthis.omegareborn;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

//Command to set a player's omega lives
public class OmegaSetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            //We try to wrap the mentioned player in an OmegaPlayer and cast the second argument to an integer
            OmegaPlayer p = new OmegaPlayer(Bukkit.getPlayerExact(args[0]));
            int newLives = Integer.parseInt(args[1]);
            //We set their lives. We don't have to worry about exception handling because the OmegaPlayer object already does it
            //for us. Like I said, absolute W of an object
            p.setLives(newLives);
            return true;
        } catch(NullPointerException e) {
            //A NullPointerException means that the getPlayerExact() method failed.
            sender.sendMessage(OmegaReborn.OMEGA_PREFIX + "Invalid target");
        } catch(NumberFormatException e) {
            //A NumberFormatException means that the parseInt() method failed
            sender.sendMessage(OmegaReborn.OMEGA_PREFIX + "Invalid amount");
        }
        return false;
    }
}
