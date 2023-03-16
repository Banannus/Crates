package dk.banannus.crates.tasks;

import dk.banannus.crates.Crates;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static dk.banannus.crates.utils.Packets.*;

public class ArmorStandAnimation {

	private final Player player;

	private final Location location;

	private final BlockFace blockFace;

	private final World world;
	private static final double radius = 1.2;

	public ArmorStandAnimation(Player player, Location location, BlockFace blockFace) {
		this.player = player;
		this.location = location;
		this.blockFace = blockFace;
		this.world = location.getWorld();
		this.location.add(0.5,-0.4875,0.5);
		startAnimation();
	}

	private final ArrayList<EntityArmorStand> stands = new ArrayList<>();
	HashMap<EntityArmorStand, Location> locations = new HashMap<>();


	// TODO: Færdiggør animation
	// TODO: Optimiserer diverse ting, samt sæt det ordentligt op.

	public void startAnimation() {
		Location chest = location.clone().add(0, 0.9875, 0); // Middle of chest
		playChestAction(chest, true);
		task.runTaskTimer(Crates.getInstance(), 40L, 3L);
		Bukkit.getScheduler().runTaskLater(Crates.getInstance(), this::fastCircle, 40);
		startAnimationSound.runTaskTimer(Crates.getInstance(), 0L, 6L);

		new BukkitRunnable() {
			int i = 0;

			@Override
			public void run() {
				if (i >= 8) {
					startAnimationSound.cancel();
					cancel();
					return;
				}

				final double currentMinusDegrees = (360.0 / 8) * i; // Regn nuværende currentMinusDegrees

				EntityArmorStand stand = spawnArmorStand(location, blockFace);

				stands.add(stand);
				circleAnimation(currentMinusDegrees, stand, 0, 9, 360, false);
				i++;
			}
		}.runTaskTimer(Crates.getInstance(), 0, 5);
	}

	BukkitRunnable startAnimationSound = new BukkitRunnable() {
		@Override
		public void run() {
			world.playSound(location, Sound.NOTE_STICKS,1, 1);
		}
	};


	// CIRCLE ANIMATION
	public void circleAnimation(double minusDegrees, EntityArmorStand stand, double plusDegrees, double anglePlusDegrees, double stopDegrees, boolean lastCircle) {
		new BukkitRunnable() {
			float angleDegrees = 0f;
			@Override
			public void run() {

				double angleRadians = (angleDegrees + 90 + (plusDegrees)) * Math.PI / 180;
				Location originalLocation = getLocationOnCircle(location, angleRadians, radius, blockFace);
				Location movingLocation = originalLocation.clone();

				sendTeleportPacket(stand, movingLocation);

				if (angleDegrees >= stopDegrees - minusDegrees) {
					cancel();
					if(lastCircle) {
						locations.put(stand, movingLocation.add(0,0.9875,0));
						cancel();
					}
				}
				angleDegrees += anglePlusDegrees;
			}
		}.runTaskTimer(Crates.getInstance(), 0, 1);

	}


	private void fastCircle() {
		ArrayList<EntityArmorStand> standsCopy = new ArrayList<>(stands);

		Random random = new Random();

		int randomIndex = random.nextInt(8);
		EntityArmorStand randomStand = standsCopy.get(randomIndex);

		int anglePlus = (randomIndex % 2 == 0) ? 18 : 15;

		int[] scheduleDelays = {30, 32, 33, 34, 37, 40, 41, 46};
		int scheduleDelay = scheduleDelays[randomIndex % scheduleDelays.length];

		new BukkitRunnable() {

			@Override
			public void run() {

				int j = 0;
				for(EntityArmorStand stand : stands) {
					circleAnimation(0, stand, -45 * j, anglePlus, randomIndex * 45 + 360, true);
					j++;
				}
				stands.clear();
				cancel();

				Bukkit.getScheduler().runTaskLater(Crates.getInstance(), () -> {
					task.cancel();

					int delay = 0;
					for (EntityArmorStand stand : standsCopy) {
						if (!stand.equals(randomStand)) {
							Bukkit.getScheduler().runTaskLater(Crates.getInstance(), () -> {
								Location standLoc = locations.get(stand);
								sendPacket(new PacketPlayOutEntityDestroy(stand.getId()));
								world.playSound(stand.getBukkitEntity().getLocation(), Sound.NOTE_BASS, 1, 2);
								PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
										EnumParticle.SMOKE_NORMAL, true, (float) standLoc.getX(), (float) standLoc.getY(),
										(float) standLoc.getZ(), 0.5f, 0.5f, 0.5f, 0.01f, 5);
								sendPacket(packet);
							}, delay);
							delay += 10;
						}
					}
				}, scheduleDelay);
				Bukkit.getScheduler().runTaskLater(Crates.getInstance(), () -> {
					EntityArmorStand stand = standsCopy.get(randomIndex);
					Location standLoc = locations.get(randomStand);
					Location finalLoc = standLoc.add(0,-1.75,0);
					sendTeleportPacket(stand, finalLoc);
					lastAnimation(stand, finalLoc);
					cancel();
				}, scheduleDelay + 70);

			}
		}.runTaskTimer(Crates.getInstance(), 0, 1);
	}


	public void lastAnimation(EntityArmorStand stand, Location finalLoc) {
		new BukkitRunnable() {

			@Override
			public void run() {
				for (int j = 0; j < 5; j++) {
					Bukkit.getScheduler().runTaskLater(Crates.getInstance(), () -> {
						Location locYaw = new Location(world, finalLoc.getX(), finalLoc.getY(), finalLoc.getZ(), finalLoc.getYaw() + 30f, finalLoc.getPitch());
						sendTeleportPacket(stand, locYaw);
						world.playSound(location, Sound.STEP_WOOL, 1, 0);
					}, j * 10);

					Bukkit.getScheduler().runTaskLater(Crates.getInstance(), () -> {
						Location locYaw = new Location(world, finalLoc.getX(), finalLoc.getY(), finalLoc.getZ(), finalLoc.getYaw() - 60f, finalLoc.getPitch());
						sendTeleportPacket(stand, locYaw);
						world.playSound(location, Sound.STEP_WOOL, 1, 0);
					}, j * 10 + 5);
				}
				Bukkit.getScheduler().runTaskLater(Crates.getInstance(), () -> {
					world.createExplosion(location.getX(), location.getY() + 2, location.getZ(), 1.0f, false, false);
					SkullMeta skull = (SkullMeta) CraftItemStack.getItemMeta(stand.getEquipment(4));
					particleRewards(skull, stand);
					cancel();

				}, 50);
			}
		}.runTaskLater(Crates.getInstance(), 15);
	}

	public void particleRewards(SkullMeta skull, EntityArmorStand stand) {
		switch(skull.getDisplayName()) {

			// DÅRLIGSTE REWARD

			case "http://textures.minecraft.net/texture/65b95da1281642daa5d022adbd3e7cb69dc0942c81cd63be9c3857d222e1c8d9":
				Bukkit.broadcastMessage("IRON");
				PacketPlayOutWorldParticles ironPacket = new PacketPlayOutWorldParticles(
						EnumParticle.REDSTONE, true, (float) location.getX(), (float) location.getY() + 2,
						(float) location.getZ(), 0.5f, 0.5f, 0.5f, 0.1f, 25);
				sendPacket(ironPacket);
				world.playSound(location, Sound.BAT_DEATH, 1, 0);
				break;

			// MELLEM REWARD

			case "http://textures.minecraft.net/texture/9e5bb8b31f46aa9af1baa88b74f0ff383518cd23faac52a3acb96cfe91e22ebc":
				Bukkit.broadcastMessage("GULD");
				PacketPlayOutWorldParticles goldPacket = new PacketPlayOutWorldParticles(
						EnumParticle.FIREWORKS_SPARK, true, (float) location.getX(), (float) location.getY() + 2,
						(float) location.getZ(), 0.5f, 0.5f, 0.5f, 0.1f, 25);
				sendPacket(goldPacket);
				world.playSound(location, Sound.ORB_PICKUP, 1, 0); // FIX
				break;

			// BEDSTE REWARD

			case "http://textures.minecraft.net/texture/c064983c78ef587c5e574b080a8454ffd62cc329c683a68f62af7f92a1f4a":
				Bukkit.broadcastMessage("DIAMOND");
				PacketPlayOutWorldParticles diamondPacket = new PacketPlayOutWorldParticles(
						EnumParticle.VILLAGER_HAPPY, true, (float) location.getX(), (float) location.getY() + 2,
						(float) location.getZ(), 0.5f, 0.5f, 0.5f, 0.1f, 25);
				sendPacket(diamondPacket);
				world.playSound(location, Sound.LEVEL_UP, 1, 0);
				break;
		}
		sendPacket(new PacketPlayOutEntityDestroy(stand.getId()));
		Location chest = location.clone().add(0, 0.9875, 0);
		playChestAction(chest, false);
	}


	BukkitRunnable task = new BukkitRunnable() {
		@Override
		public void run() {
			world.playSound(location, Sound.NOTE_PIANO, 5, 1);
			world.playSound(location, Sound.NOTE_PLING, 1, 2);
		}
	};
}
