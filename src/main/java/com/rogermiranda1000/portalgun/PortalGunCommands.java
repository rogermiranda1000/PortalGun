package com.rogermiranda1000.portalgun;

import com.rogermiranda1000.helper.CustomCommand;
import com.rogermiranda1000.portalgun.blocks.ThermalBeams;
import com.rogermiranda1000.portalgun.blocks.ThermalReceivers;
import com.rogermiranda1000.portalgun.cubes.CompanionCube;
import com.rogermiranda1000.portalgun.cubes.Cube;
import com.rogermiranda1000.portalgun.cubes.Cubes;
import com.rogermiranda1000.portalgun.blocks.ResetBlocks;
import com.rogermiranda1000.portalgun.cubes.RedirectionCube;
import com.rogermiranda1000.portalgun.files.Language;
import com.rogermiranda1000.portalgun.portals.Portal;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PortalGunCommands {
    public final CustomCommand[]commands;
    public PortalGunCommands(final String clearPrefix, final String errorPrefix) {
        this.commands = new CustomCommand[]{
                new CustomCommand("portalgun \\?", null, true, "portalgun ?", null, (sender, args) -> {
                    sender.sendMessage(clearPrefix);
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun " + ChatColor.GREEN + "- " + Language.HELP_GET_GUN.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun boots " + ChatColor.GREEN + "- " + Language.HELP_GET_BOOTS.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun emancipator " + ChatColor.GREEN + "- " + Language.HELP_GET_EMANCIPATOR.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun thermal_beam " + ChatColor.GREEN + "- " + Language.HELP_GET_THERMAL_BEAM.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun thermal_receiver " + ChatColor.GREEN + "- " + Language.HELP_GET_THERMAL_RECEIVER.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun remove " + ChatColor.GREEN + "- " + Language.HELP_REMOVE.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun remove [player] " + ChatColor.GREEN + "- " + Language.HELP_REMOVE_OTHERS.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun remove all " + ChatColor.GREEN + "- " + Language.HELP_REMOVE_ALL.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun companion [world] [x] [y] [z] [remove old; true/false] " + ChatColor.GREEN + "- " + Language.HELP_COMPANION.getText());
                    sender.sendMessage(ChatColor.GOLD + "  /portalgun redirection [world] [x] [y] [z] [remove old; true/false] " + ChatColor.GREEN + "- " + Language.HELP_REDIRECTION.getText());
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
                new CustomCommand("portalgun thermal_beam", "portalgun.thermalbeam", false, "portalgun thermal_beam", Language.HELP_GET_THERMAL_BEAM.getText(), (sender, args) -> {
                    Player player = (Player) sender;
                    player.getInventory().addItem(ThermalBeams.thermalBeamItem);
                    // TODO confirmation msg
                }),
                new CustomCommand("portalgun thermal_receiver", "portalgun.thermalreceiver", false, "portalgun thermal_receiver", Language.HELP_GET_THERMAL_RECEIVER.getText(), (sender, args) -> {
                    Player player = (Player) sender;
                    player.getInventory().addItem(ThermalReceivers.thermalReceiverItem);
                    // TODO confirmation msg
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
                // TODO world hint on VersionController
                new CustomCommand("portalgun companion \\S+ [\\d\\.]+ [\\d\\.]+ [\\d\\.]+ \\S+", "portalgun.companion", true, "portalgun companion [world] [x] [y] [z] [true|false]", Language.HELP_COMPANION.getText(), (sender, args) -> {
                    spawnCompanionCube(clearPrefix, errorPrefix, CompanionCube::new, sender, args);
                }),
                new CustomCommand("portalgun companion \\S+ [\\d\\.]+ [\\d\\.]+ [\\d\\.]+", "portalgun.companion", true, "portalgun companion [world] [x] [y] [z]", Language.HELP_COMPANION.getText(), (sender, args) -> {
                    spawnCompanionCube(clearPrefix, errorPrefix, CompanionCube::new, sender, args);
                }),
                new CustomCommand("portalgun companion [\\d\\.]+ [\\d\\.]+ [\\d\\.]+ \\S+", "portalgun.companion", true, "portalgun companion [x] [y] [z] [true|false]", Language.HELP_COMPANION.getText(), (sender, args) -> {
                    spawnCompanionCube(clearPrefix, errorPrefix, CompanionCube::new, sender, args);
                }),
                new CustomCommand("portalgun companion [\\d\\.]+ [\\d\\.]+ [\\d\\.]+", "portalgun.companion", true, "portalgun companion [x] [y] [z]", Language.HELP_COMPANION.getText(), (sender, args) -> {
                    spawnCompanionCube(clearPrefix, errorPrefix, CompanionCube::new, sender, args);
                }),
                new CustomCommand("portalgun redirection \\S+ [\\d\\.]+ [\\d\\.]+ [\\d\\.]+ \\S+", "portalgun.redirection", true, "portalgun redirection [world] [x] [y] [z] [true|false]", Language.HELP_REDIRECTION.getText(), (sender, args) -> {
                    spawnCompanionCube(clearPrefix, errorPrefix, RedirectionCube::new, sender, args);
                }),
                new CustomCommand("portalgun redirection \\S+ [\\d\\.]+ [\\d\\.]+ [\\d\\.]+", "portalgun.redirection", true, "portalgun redirection [world] [x] [y] [z]", Language.HELP_REDIRECTION.getText(), (sender, args) -> {
                    spawnCompanionCube(clearPrefix, errorPrefix, RedirectionCube::new, sender, args);
                }),
                new CustomCommand("portalgun redirection [\\d\\.]+ [\\d\\.]+ [\\d\\.]+ \\S+", "portalgun.redirection", true, "portalgun redirection [x] [y] [z] [true|false]", Language.HELP_REDIRECTION.getText(), (sender, args) -> {
                    spawnCompanionCube(clearPrefix, errorPrefix, RedirectionCube::new, sender, args);
                }),
                new CustomCommand("portalgun redirection [\\d\\.]+ [\\d\\.]+ [\\d\\.]+", "portalgun.redirection", true, "portalgun redirection [x] [y] [z]", Language.HELP_REDIRECTION.getText(), (sender, args) -> {
                    spawnCompanionCube(clearPrefix, errorPrefix, RedirectionCube::new, sender, args);
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

    private static void spawnCompanionCube(String clearPrefix, String errorPrefix, Function<Location, Cube> generator,
                                           CommandSender sender, @NotNull String[] args) {
        Pattern r = Pattern.compile("portalgun \\S+ (\\S+ )?([\\d\\.]+) ([\\d\\.]+) ([\\d\\.]+)( (?:true)|(?:false))?");
        Matcher m = r.matcher("portalgun " + String.join(" ", args));
        if (!m.matches()) {
            sender.sendMessage(errorPrefix + Language.ERROR_UNKNOWN.getText());
            return;
        }

        // get the world
        World world;
        if (m.group(1) != null) {
            world = Bukkit.getWorld(m.group(1));
            if (world == null) {
                sender.sendMessage(errorPrefix + Language.ERROR_WORLD.getText(new String[]{"world", m.group(1)}));
                return;
            }
        }
        else {
            if (sender instanceof BlockCommandSender) {
                world = ((BlockCommandSender)sender).getBlock().getWorld();
            }
            else if (sender instanceof Player) {
                world = ((Player)sender).getWorld();
            }
            else {
                sender.sendMessage(errorPrefix + Language.ERROR_WORLD.getText(new String[]{"world", "null"}));
                return;
            }
        }

        // get the location & if desired remove
        Location toPlace = new Location(world, Double.parseDouble(m.group(2)), Double.parseDouble(m.group(3)), Double.parseDouble(m.group(4)));
        boolean removeOld = !"false".equals(m.group(5)); // by default, remove it

        Cubes.spawnCube(generator.apply(toPlace), removeOld);
    }
}
