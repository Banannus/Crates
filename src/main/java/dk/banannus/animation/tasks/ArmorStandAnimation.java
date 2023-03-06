package dk.banannus.animation.tasks;

import dk.banannus.animation.Animation;
import dk.banannus.animation.utils.Chat;
import dk.banannus.animation.utils.GetSkull;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.EnderChest;
import org.bukkit.scheduler.BukkitRunnable;

public class ArmorStandAnimation extends BukkitRunnable {
	private final Player player;

	private Location location;


	public ArmorStandAnimation(Player player, Location location) {
		this.player = player;
		this.location = location;

	}

	private static void sendPacket(Packet packet) {
		Bukkit.getOnlinePlayers().forEach(cur -> ((CraftPlayer) cur).getHandle().playerConnection.sendPacket(packet));
	}

	@Override
	public void run() {

		// Spawn armor stand
		EntityArmorStand stand = new EntityArmorStand(((CraftWorld)location.getWorld()).getHandle()); // Opret armorstand
		stand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()); // Set Lokation
		stand.setBasePlate(false); // Fjern stone slap
		stand.setInvisible(true); // Gør den usynlig

		ItemStack skull = GetSkull.getSkull("http://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec"); // Get skull

		PacketPlayOutEntityEquipment equi = new PacketPlayOutEntityEquipment(stand.getId(), 4, CraftItemStack.asNMSCopy(skull)); // Set skull på Armor standen
		PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(stand); // Spawn Armor Standen
		sendPacket(spawn); // Send Packets
		sendPacket(equi);

		// Teleport Armor standen 2 blocks inden for 2 sekunder.
		int sec = 2;
		int ticks = sec * 20; //  sec * 20 ticks | 1 sec = 20 ticks
		double startY = location.getY(); // Start lokationen
		double endY = location.getY() + 2; // Slut lokationen. 2 = +2 blocks
		double increment = (endY - startY) / ticks; // Hvor mange blocks der rykker op afgangen ift. endY, startY og Ticks.


		Location chestLoc = location.clone();
		playChestAction(chestLoc, true);

		location = location.clone().add(0.5,-0.5,0.5);

		new BukkitRunnable() {
			double currentY = startY;
			int count = 0;

			@Override
			public void run() {
				// Update the location of the armor stand
				Location newLocation = new Location(location.getWorld(), location.getX(), currentY, location.getZ(), location.getYaw(), location.getPitch());
				stand.setLocation(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
				PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(stand);
				sendPacket(packet);

				// Update the current Y position and check if we've reached the end
				currentY += increment;
				count++;
				if (count >= ticks) {
					// Remove the armor stand after 5 seconds
					PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(stand.getId());
					sendPacket(destroy);
					player.sendMessage(Chat.colored("&8&l[ &d&lANIMATION &8&l] &7Animation done."));
					this.cancel();
					playChestAction(chestLoc, false);

				}
			}
		}.runTaskTimer(Animation.getInstance(), 0, 1);
	}

	@SuppressWarnings("deprecation")
	public void playChestAction(Location location, boolean open) {
		World world = ((CraftWorld) location.getWorld()).getHandle();
		BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
		byte dataByte = (open) ? (byte) 1 : 0;
		PacketPlayOutBlockAction blockActionPacket = new PacketPlayOutBlockAction(position, net.minecraft.server.v1_8_R3.Block.getById(location.getBlock().getTypeId()), (byte) 1, dataByte);
		sendPacket(blockActionPacket);
	}
}