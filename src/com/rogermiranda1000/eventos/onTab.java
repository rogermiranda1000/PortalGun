package com.rogermiranda1000.eventos;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class onTab implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onTabCompleteEvent(TabCompleteEvent e) {
        String msg = e.getBuffer().replaceFirst("/","");
        String[] lista = {"portalgun ?","portalgun remove","portalgun remove all"};
        List<String> s = new ArrayList<String>();
        for(String l: lista) {
            if(msg.length()>l.length()) continue;
            if(l.substring(0, msg.length()).equalsIgnoreCase(msg)) s.add(l.substring(msg.length(), l.length()));
        }
        if(s.size()>0) e.setCompletions(s);
        /*if (msg.equalsIgnoreCase("portalgun ")) e.setCompletions(Arrays.asList(new String[] { "?", "remove", "remove all" }));
        else if (msg.equalsIgnoreCase("portalgun remove ")) e.setCompletions(Arrays.asList(new String[] { "all" }));*/
    }
}
