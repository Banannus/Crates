package dk.banannus.crates.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public class GetSkull {

	public static ItemStack getSkull(String url) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		if(url.isEmpty())return head;

		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
		profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		Field profileField = null;
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		headMeta.setDisplayName(url);
		head.setItemMeta(headMeta);
		return head;
	}

	public static ItemStack getPlayerSkull(String name) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setOwner(name);
		head.setItemMeta(meta);
		return head;
	}

	public static String getInternalKey(SkullMeta skullmeta) {
		Field profileField = null;
		try {
			profileField = skullmeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			GameProfile profile = (GameProfile) profileField.get(skullmeta);
			if (profile != null && profile.getProperties().containsKey("textures")) {
				Property property = profile.getProperties().get("textures").iterator().next();
				String texture = property.getValue();
				return texture;
			}
			return null;
		} catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}



}
