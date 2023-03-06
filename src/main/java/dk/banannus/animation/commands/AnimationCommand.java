package dk.banannus.animation.commands;

import dk.banannus.animation.tasks.ArmorStandAnimation;
import dk.banannus.animation.utils.Chat;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnimationCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(Chat.colored("&cThis command can only be run by a player."));
			return true;
		}

		Player player = (Player) sender;
		Location location = player.getLocation();




		return true;
	}
}