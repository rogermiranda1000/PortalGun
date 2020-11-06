package com.rogermiranda1000.portalgun.eventos;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.files.Language;
import com.rogermiranda1000.portalgun.portals.Portal;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class onCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (!cmd.getName().equalsIgnoreCase("portalgun")) return false;

        if (player == null) {
            sender.sendMessage("Don't use this command in console.");
            return true;
        }

        if (args.length == 0) {
            // Get the PortalGun (and/or PortalBoots)
            if (player.hasPermission("portalgun.portalgun")) {
                player.getInventory().addItem(new ItemStack[] { PortalGun.item });
                if(player.hasPermission("portalgun.boots")) player.getInventory().addItem(new ItemStack[] { PortalGun.botas });
                player.sendMessage(PortalGun.clearPrefix + ChatColor.GREEN + Language.USER_GET.getText());
            } else {
                player.sendMessage(PortalGun.errorPrefix + Language.USER_NO_PERMISSIONS.getText());
            }

            return true;
        }

        // Remove portals
        if (args[0].equalsIgnoreCase("remove")) {
            if (!player.hasPermission("portalgun.remove")) {
                player.sendMessage(PortalGun.errorPrefix + Language.USER_NO_PERMISSIONS.getText());
                return true;
            }

            if (args.length == 1) {
                if (Portal.removePortal(player)) player.sendMessage(PortalGun.clearPrefix + ChatColor.GREEN + Language.USER_REMOVE.getText());
                else player.sendMessage(PortalGun.errorPrefix + Language.USER_NO_PORTALS.getText());

                return true;
            }

            // TODO: remove <player>
            if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
                if (!player.hasPermission("portalgun.remove.all")) {
                    player.sendMessage(PortalGun.errorPrefix + Language.USER_NO_PERMISSIONS.getText());

                    return true;
                }

                // TODO: warn players (Language.OTHER_USER_REMOVE.getText({"player", player.getName()}))
                Portal.removeAllPortals();
                player.sendMessage(PortalGun.errorPrefix + Language.USER_REMOVE_ALL.getText());

                return true;
            }
        }

        // Help command
        player.sendMessage(PortalGun.clearPrefix);
        player.sendMessage(ChatColor.GOLD + "  /portalgun " + ChatColor.GREEN + "- " + Language.HELP_GET.getText());
        player.sendMessage(ChatColor.GOLD + "  /portalgun remove " + ChatColor.GREEN + "- " + Language.HELP_REMOVE.getText());
        player.sendMessage(ChatColor.GOLD + "  /portalgun remove all " + ChatColor.GREEN + "- " + Language.HELP_REMOVE_ALL.getText());

        return true;
    }
}
