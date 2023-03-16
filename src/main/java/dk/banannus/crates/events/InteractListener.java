package dk.banannus.crates.events;

import dk.banannus.crates.tasks.ArmorStandAnimation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
			Block enderChest = e.getClickedBlock();
			BlockFace blockFace = getDirectionOfEnderChest(enderChest);
			new ArmorStandAnimation(player, location, blockFace);


		} else {
			return;
		}
	}


	@SuppressWarnings("deprecation")
	public static BlockFace getDirectionOfEnderChest(Block enderChest) {
		byte data = enderChest.getData();
		switch (data) {
			case 2:
				return BlockFace.NORTH;
			case 3:
				return BlockFace.SOUTH;
			case 4:
				return BlockFace.WEST;
			case 5:
				return BlockFace.EAST;
			default:
				return BlockFace.NORTH;
		}
	}
}

