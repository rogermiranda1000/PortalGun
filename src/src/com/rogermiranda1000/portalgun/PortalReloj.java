package com.rogermiranda1000.portalgun;

import com.rogermiranda1000.portalgun.portals.Portal;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PortalReloj implements Runnable, Listener{

    final Location l1,l2;
    final String look1,look2,player;
    final boolean b1,b2;
    int part_task=0;
    BukkitTask bt;
    Chunk a,b;

    public PortalReloj(final Location loc1, final Location loc2, final String look1, final String look2, final boolean f1, final boolean f2, final String player){
        l1=loc1;
        l2=loc2;
        this.look1=look1;
        this.look2=look2;
        b1=f1;
        b2=f2;
        this.player=player;
        a=loc1.getChunk();
        b=loc2.getChunk();
        Bukkit.getPluginManager().registerEvents(this,PortalGun.instancia);
        if(a.isLoaded()||b.isLoaded())iniciar();
    }

    public void run() {


        for (int a = 0; a != 2; a++) {
            Location loc = l2;
            String look = look2;
            Boolean f = b2;
            int color = 1;
            if (a == 0) {
                loc = l1;
                look = look1;
                f = b1;
                color = 0;
            }
            if (PortalGun.instancia.all_particles) {
                for (int proc = 0; proc != 22; proc++) {
                    PortalGun.playParticle(loc, Integer.valueOf(proc), look, f, Integer.valueOf(color), player);
                }
            } else {
                PortalGun.playParticle(loc, Integer.valueOf(part_task), look, f, Integer.valueOf(color), player);
                part_task += 10;
                if (part_task > 21) part_task -= 21;
                PortalGun.playParticle(loc, Integer.valueOf(part_task), look, f, Integer.valueOf(color), player);
                part_task++;
                if (part_task > 21) part_task -= 21;
            }
        }
        List<Entity> entidades = new ArrayList<Entity>();
        entidades.addAll(Arrays.asList(l1.getWorld().getChunkAt(l1).getEntities()));
        if(b1) entidades.addAll(Arrays.asList(l1.getWorld().getChunkAt(PortalGun.getGroundBlock(look1, l1)).getEntities()));
        entidades.addAll(Arrays.asList(l2.getWorld().getChunkAt(l2).getEntities()));
        if(b2) entidades.addAll(Arrays.asList(l2.getWorld().getChunkAt(PortalGun.getGroundBlock(look2, l2)).getEntities()));
        if (entidades.size() > 0) for (Entity player : entidades) {
            try {
                World mundo = player.getLocation().getWorld();
                double xp = player.getLocation().getBlockX();
                double yp = player.getLocation().getBlockY();
                double zp = player.getLocation().getBlockZ();
                double xloc1 = l1.getBlockX();
                //if (xloc1 < 0.0D) xloc1++;
                double xloc2 = l2.getBlockX();
                //if (xloc2 < 0.0D) xloc2++;
                double zloc1 = l1.getBlockZ();
                //if (zloc1 < 0.0D) zloc1++;
                double zloc2 = l2.getBlockZ();
                //if (zloc2 < 0.0D) zloc2++;
                if (player instanceof Player) {
                    //isPlayerTp((Player) player, loc1, loc2, look1, look2);
                    continue;
                }
                if((mundo.equals(l1.getWorld()) && xp == xloc1 && yp == l1.getBlockY() && zp == zloc1) ||
                        (b1 && mundo.equals(l1.getWorld()) && xp == PortalGun.getGroundBlock(look1, l1).getBlockX() &&
                                yp == PortalGun.getGroundBlock(look1, l1).getBlockY() && zp == PortalGun.getGroundBlock(look1, l1).getBlockZ())) {
                    //PortalGun.instancia.teletransporte(l2, player, look2, look1, b2, b1);
                    continue;
                }
                if (mundo.equals(l2.getWorld()) && xp == xloc2 && yp == l2.getBlockY() && zp == zloc2||
                        (b2 && mundo.equals(l2.getWorld()) && xp == PortalGun.getGroundBlock(look2, l2).getBlockX() &&
                                yp == PortalGun.getGroundBlock(look2, l2).getBlockY() && zp == PortalGun.getGroundBlock(look2, l2).getBlockZ())) {
                    //PortalGun.instancia.teletransporte(l1, player, look1, look2, b1, b2);
                    continue;
                }
                if (PortalGun.instancia.entidad_portal.contains(player)) PortalGun.instancia.entidad_portal.remove(player);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public boolean isActivo(){
        return bt!=null;
    }

    public void detener(){
        if(bt==null)return;
        bt.cancel();
        bt=null;
    }

    public void iniciar(){
        if(bt!=null)return;
        bt=Bukkit.getScheduler().runTaskTimer(PortalGun.instancia,this,0,PortalGun.instancia.delay);
    }

    public void eliminar(){
        HandlerList.unregisterAll(this);
        detener();
    }

    @EventHandler
    public void alCargar(ChunkLoadEvent e){
        if(bt!=null)return;
        Chunk c=e.getChunk();
        if(a==c||b==c)iniciar();
    }

    @EventHandler
    public void alDescargar(ChunkUnloadEvent e){
        if(bt==null)return;
        Chunk c=e.getChunk();
        if((a==c&&!b.isLoaded())||(b==c&&!a.isLoaded()))detener();
    }

}
