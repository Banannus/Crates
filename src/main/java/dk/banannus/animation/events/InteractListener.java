package dk.banannus.animation.events;

import dk.banannus.animation.tasks.ArmorStandAnimation;
import dk.banannus.animation.utils.Chat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

	@EventHandler
	public void onBlockClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock.getType() == Material.ENDER_CHEST) {
			e.setCancelled(true);
			Location location = e.getClickedBlock().getLocation();
			ArmorStandAnimation animation = new ArmorStandAnimation(player, location);
			animation.run();
		} else {
			return;
		}
	}
}
