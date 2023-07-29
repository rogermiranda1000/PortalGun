package com.rogermiranda1000.portalgun;

import com.rogermiranda1000.helper.CustomCommand;
import com.rogermiranda1000.portalgun.blocks.ResetBlocks;
import com.rogermiranda1000.portalgun.files.Language;
import com.rogermiranda1000.portalgun.portals.Portal;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PortalGunCommands {
    public final CustomCommand[]commands;
    public PortalGunCommands(final String clearPrefix, final String errorPrefix) {
        this.commands = new CustomCommand[]{
                new CustomCommand("portalgun \\?", null, true, "portalgun ?", null, (sender, args) -> {
                    sender.sendMessage(clearPrefix);
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun " + ChatColor.GREEN + "- " + Language.HELP_GET_GUN.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun boots " + ChatColor.GREEN + "- " + Language.HELP_GET_BOOTS.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun emancipator " + ChatColor.GREEN + "- " + Language.HELP_GET_EMANCIPATOR.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun remove " + ChatColor.GREEN + "- " + Language.HELP_REMOVE.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun remove [player] " + ChatColor.GREEN + "- " + Language.HELP_REMOVE_OTHERS.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun remove all " + ChatColor.GREEN + "- " + Language.HELP_REMOVE_ALL.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun report [contact/-] [report] " + ChatColor.GREEN + "- " + Language.HELP_REPORT.getText());
                }),
                new CustomCommand("portalgun", "portalgun.portalgun", false, "portalgun", Language.HELP_GET_GUN.getText(), (sender, args) -> {
                    Player player = (Player) sender;
                    player.getInventory().addItem(PortalGun.item);
                    player.sendMessage(clearPrefix + ChatColor.GREEN + Language.USER_GET_GUN.getText());
                }),
                // TODO: give portal gun to others
                new CustomCommand("portalgun boots", "portalgun.boots", false, "portalgun boots", Language.HELP_GET_BOOTS.getText(), (sender, args) -> {
                    Player player = (Player) sender;
                    player.getInventory().addItem(PortalGun.botas);
                    player.sendMessage(clearPrefix + ChatColor.GREEN + Language.USER_GET_BOOTS.getText());
                }),
                // TODO: give portal boots to others
                new CustomCommand("portalgun emancipator", "portalgun.emancipator", false, "portalgun emancipator", Language.HELP_GET_EMANCIPATOR.getText(), (sender, args) -> {
                    Player player = (Player) sender;
                    player.getInventory().addItem(ResetBlocks.resetBlockItem);
                    player.sendMessage(clearPrefix + ChatColor.GREEN + Language.USER_GET_EMANCIPATOR.getText());
                }),
                new CustomCommand("portalgun remove", "portalgun.remove", false, "portalgun remove", Language.HELP_REMOVE.getText(), (sender, args) -> {
                    Player player = (Player) sender;
                    if (Portal.removePortal(player)) player.sendMessage(clearPrefix + Language.USER_REMOVE.getText());
                    else player.sendMessage(errorPrefix + Language.USER_NO_PORTALS.getText());
                }),
                new CustomCommand("portalgun remove \\S+", "portalgun.remove.others", true, "portalgun remove [player]", Language.HELP_REMOVE_OTHERS.getText(), (sender, args) -> {
                    String playerName = args[1];
                    String[] replacePlayer = {"player", playerName};

                    Player jugadorEliminar = Bukkit.getPlayer(playerName);
                    // player not found?
                    if (jugadorEliminar == null || !playerName.equalsIgnoreCase(jugadorEliminar.getName())) {
                        sender.sendMessage(errorPrefix + Language.USER_NOT_FOUND.getText(replacePlayer));
                        return;
                    }

                    // player without portals?
                    if (Portal.removePortal(jugadorEliminar)) {
                        if (jugadorEliminar.isOnline()) jugadorEliminar.sendMessage(clearPrefix + Language.OTHER_USER_REMOVE.getText(new String[]{"player", sender.getName()}));
                        sender.sendMessage(clearPrefix + Language.USER_REMOVE_OTHERS.getText(replacePlayer));
                    }
                    else sender.sendMessage(errorPrefix + Language.OTHER_USER_NO_PORTALS.getText(replacePlayer));
                }),
                new CustomCommand("portalgun remove all", "portalgun.remove.all", true, "portalgun remove all", Language.HELP_REMOVE_ALL.getText(), (sender, args) -> {
                    // TODO: warn players (Language.OTHER_USER_REMOVE.getText({"player", player.getName()}))
                    Portal.removeAllPortals();
                    sender.sendMessage(clearPrefix + Language.USER_REMOVE_ALL.getText());
                }),
                new CustomCommand("portalgun report \\S+ .+", "portalgun.report", true, "portalgun report [contact] [report]", Language.HELP_REPORT.getText(), (sender, args) -> {
                    String contact = args[1];
                    if (!contact.equals("-") && !contact.contains("@") && !contact.contains("#")) {
                        sender.sendMessage(errorPrefix + Language.REPORT_CONTACT_ERROR.getText());
                        return;
                    }

                    StringBuilder msg = new StringBuilder();
                    for (int n = 2; n < args.length; n++) msg.append(args[n]).append(' ');
                    msg.setLength(msg.length() - 1); // remove last ' '

                    PortalGun.plugin.userReport(contact.equals("-") ? null : contact, (sender instanceof Player) ? ((Player) sender).getName() : null, msg.toString());
                    sender.sendMessage(clearPrefix + Language.REPORT_SENT.getText());
                })
        };
    }
}
