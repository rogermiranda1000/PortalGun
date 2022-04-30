package com.rogermiranda1000.portalgun.eventos;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class onTab implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onTabCompleteEvent(TabCompleteEvent e) {
        if(!e.getBuffer().startsWith("/portalgun ") && !e.getBuffer().startsWith("/portalgun:portalgun ")) return;
        String cmd = e.getBuffer().substring(e.getBuffer().indexOf(" ")+1);

        List<String> options = new ArrayList<>();
        options.add("?");
        options.add("boots");
        options.add("remove");
        options.add("remove all");
        for(Player p: Bukkit.getOnlinePlayers()) {
            options.add(p.getName());
            if(cmd.startsWith("remove ")) options.add("remove "+p.getName());
        }

        // is it valid?
        List<String> candidates = new ArrayList<>();
        for(String option : options) {
            if(cmd.length()>option.length()) continue;
            if(!option.substring(0, cmd.length()).equalsIgnoreCase(cmd)) continue; // what's already written doesn't match

            int iF = cmd.indexOf(" ");
            if(iF==0 || option.length()<iF+1/* && !msg.equalsIgnoreCase("remove")*/) candidates.add(option);
            else candidates.add(option.substring(iF+1,option.length()));
        }
        e.setCompletions(candidates);
    }
}
