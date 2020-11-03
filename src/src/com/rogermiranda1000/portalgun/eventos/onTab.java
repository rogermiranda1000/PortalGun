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
        if(e.getBuffer().length()<11) return;
        String msg = e.getBuffer().substring(11);
        List<String> lista = new ArrayList<String>();
        lista.add("?");
        lista.add("remove");
        lista.add("remove all");
        for(Player p: Bukkit.getOnlinePlayers()) {
            lista.add(p.getName());
            if(msg.contains("remove")) lista.add("remove "+p.getName());
        }

        List<String> s = new ArrayList<String>();
        for(String l: lista) {
            if(msg.length()>l.length()) continue;
            if(l.substring(0, msg.length()).equalsIgnoreCase(msg)) {
                /*if(msg.length()>=7 && msg.substring(0,7).equalsIgnoreCase("remove ")) {
                    s.add("all");
                    for(Player p: Bukkit.getOnlinePlayers()) s.add(p.getName());
                }*/
                //else if(msg.length()>=6 && msg.substring(0,6).equalsIgnoreCase("remove")) s.add("remove all");
                /*else */
                int iF = msg.indexOf(" ");
                if(iF==0 || l.length()<iF+1/* && !msg.equalsIgnoreCase("remove")*/) s.add(l);
                else s.add(l.substring(iF+1,l.length()));
            }
        }
        if(s.size()>0) e.setCompletions(s);
    }
}
