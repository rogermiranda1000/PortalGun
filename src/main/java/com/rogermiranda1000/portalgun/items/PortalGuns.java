package com.rogermiranda1000.portalgun.items;

import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.entities.VCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class PortalGuns implements Listener {
    public static ItemStack portalGun;
    public static boolean swipeColorsAnimation = true;

    @Nullable
    public static ResourcepackedItem orangePortalGun, bluePortalGun;

    public static boolean userUsedPortalGun(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL)) return false;

        VCPlayer player = new VCPlayer(e.getPlayer());
        if (player.hasItemInHand(PortalGuns.portalGun)) return true;
        if (player.hasItemInHand(PortalGuns.orangePortalGun)) return true;
        return player.hasItemInHand(PortalGuns.bluePortalGun);
    }

    public static void addItemToPlayer(Player p) {
        p.getInventory().addItem(PortalGuns.portalGun);
    }

    // TODO use the API (so if, for example, the player launch an entity the color doesn't change); onPlayerCastPortalEvent
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerUse(PlayerInteractEvent event) {
        if (!PortalGuns.swipeColorsAnimation) return; // disabled by config
        if (!PortalGuns.userUsedPortalGun(event)) return;

        Action lastClick = Action.PHYSICAL; // last click; physical if none TODO get the last click based on the held
        Action currentClick = (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) ? Action.LEFT_CLICK_AIR : Action.RIGHT_CLICK_AIR;
        if (lastClick.equals(currentClick)) return;
        ResourcepackedItem targetGun = (currentClick.equals(Action.LEFT_CLICK_AIR) ? PortalGuns.orangePortalGun : PortalGuns.bluePortalGun);
        if (targetGun == null) return; // can't update

        // TODO replace user's PortalGun
        VCPlayer player = new VCPlayer(event.getPlayer());
        ItemStack []handItems = player.getItemInHand();
        ArrayList<Integer> handsIndexesHoldingPortalGun = new ArrayList<>();
        for (int index = 0; index < handItems.length; index++) {
            if (VersionController.get().sameItem(PortalGuns.portalGun, handItems[index]) ||
                    VersionController.get().sameItem(PortalGuns.orangePortalGun, handItems[index]) ||
                    VersionController.get().sameItem(PortalGuns.bluePortalGun, handItems[index])) {
                handsIndexesHoldingPortalGun.add(index);
            }
        }

        for (Integer index : handsIndexesHoldingPortalGun) {
            boolean leftHand = (index == 1);
            player.setItemInHand(targetGun, leftHand);
        }
    }
}
