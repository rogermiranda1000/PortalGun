package com.rogermiranda1000.portalgun.events;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.files.Language;
import com.rogermiranda1000.portalgun.portals.Portal;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class onCommand implements CommandExecutor {
    private Player getPlayer(CommandSender sender) {
        return (sender instanceof Player) ? (Player)sender : null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = getPlayer(sender);
        if (!cmd.getName().equalsIgnoreCase("portalgun")) return false;

        // Remove portals
        if (args.length >= 1 && args[0].equalsIgnoreCase("remove")) {
            if (player != null && !player.hasPermission("portalgun.remove")) {
                player.sendMessage(PortalGun.plugin.errorPrefix + Language.USER_NO_PERMISSIONS.getText());
                return true;
            }

            if (args.length == 1) {
                if (player == null) {
                    sender.sendMessage(PortalGun.plugin.errorPrefix + "You can't use this command on console. Use '/portalgun remove [player]' or '/portalgun remove all'.");
                } else {
                    if (Portal.removePortal(player)) player.sendMessage(PortalGun.plugin.clearPrefix + Language.USER_REMOVE.getText());
                    else player.sendMessage(PortalGun.plugin.errorPrefix + Language.USER_NO_PORTALS.getText());
                }

                return true;
            }

            if (args.length == 2) {
                // remove all
                if (args[1].equalsIgnoreCase("all")) {
                    if (player != null && !player.hasPermission("portalgun.remove.all")) {
                        player.sendMessage(PortalGun.plugin.errorPrefix + Language.USER_NO_PERMISSIONS.getText());
                        return true;
                    }

                    // TODO: warn players (Language.OTHER_USER_REMOVE.getText({"player", player.getName()}))
                    Portal.removeAllPortals();
                    sender.sendMessage(PortalGun.plugin.clearPrefix + Language.USER_REMOVE_ALL.getText());

                    return true;
                }

                // remove other player's portals
                if (player != null && !player.hasPermission("portalgun.remove.others")) {
                    player.sendMessage(PortalGun.plugin.errorPrefix + Language.USER_NO_PERMISSIONS.getText());
                    return true;
                }
                removeOthers(sender, args[1]);
                return true;
            }

            sender.sendMessage(PortalGun.plugin.errorPrefix + "Use '/portalgun remove [player]' or '/portalgun remove all'.");
            return true;
        }



        if (player == null) {
            sender.sendMessage(PortalGun.plugin.errorPrefix + "You can only use the '/portalgun remove' command in console.");
            return true;
        }

        // Get the PortalGun
        if (args.length == 0) {
            if (!player.hasPermission("portalgun.portalgun")) {
                player.sendMessage(PortalGun.plugin.errorPrefix + Language.USER_NO_PERMISSIONS.getText());
                return true;
            }

            // TODO: give portal gun to others
            player.getInventory().addItem(PortalGun.item);
            player.sendMessage(PortalGun.plugin.clearPrefix + ChatColor.GREEN + Language.USER_GET_GUN.getText());

            return true;
        }

        // Get the PortalBoots
        if (args[0].equalsIgnoreCase("boots")) {
            if(!player.hasPermission("portalgun.boots")) {
                player.sendMessage(PortalGun.plugin.errorPrefix + Language.USER_NO_PERMISSIONS.getText());
                return true;
            }

            // TODO: give portal boots to others
            player.getInventory().addItem(PortalGun.botas);
            player.sendMessage(PortalGun.plugin.clearPrefix + ChatColor.GREEN + Language.USER_GET_BOOTS.getText());

            return true;
        }

        // Help command
        player.sendMessage(PortalGun.plugin.clearPrefix);
        player.sendMessage(ChatColor.GOLD + "  /portalgun " + ChatColor.GREEN + "- " + Language.HELP_GET_GUN.getText());
        player.sendMessage(ChatColor.GOLD + "  /portalgun boots " + ChatColor.GREEN + "- " + Language.HELP_GET_BOOTS.getText());
        player.sendMessage(ChatColor.GOLD + "  /portalgun remove " + ChatColor.GREEN + "- " + Language.HELP_REMOVE.getText());
        player.sendMessage(ChatColor.GOLD + "  /portalgun remove [user] " + ChatColor.GREEN + "- " + Language.HELP_REMOVE_OTHERS.getText());
        player.sendMessage(ChatColor.GOLD + "  /portalgun remove all " + ChatColor.GREEN + "- " + Language.HELP_REMOVE_ALL.getText());

        return true;
    }

    private void removeOthers(CommandSender sender, String playerName) {
        Player player = getPlayer(sender);
        String []replacePlayer = {"player", playerName};

        Player jugadorEliminar = Bukkit.getPlayer(playerName);
        // player not found?
        if (jugadorEliminar == null || !playerName.equalsIgnoreCase(jugadorEliminar.getName())) {
            sender.sendMessage(PortalGun.plugin.errorPrefix + Language.USER_NOT_FOUND.getText(replacePlayer));
            return;
        }

        // player without portals?
        if (Portal.removePortal(jugadorEliminar)) {
            if (jugadorEliminar.isOnline()) jugadorEliminar.sendMessage(PortalGun.plugin.clearPrefix + Language.OTHER_USER_REMOVE.getText(new String[]{"player", sender.getName()}));
            sender.sendMessage(PortalGun.plugin.clearPrefix + Language.USER_REMOVE_OTHERS.getText(replacePlayer));
        }
        else sender.sendMessage(PortalGun.plugin.errorPrefix + Language.OTHER_USER_NO_PORTALS.getText(replacePlayer));
    }
}
