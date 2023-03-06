package dk.banannus.animation;

import dk.banannus.animation.commands.AnimationCommand;
import dk.banannus.animation.events.InteractListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Animation extends JavaPlugin {

    public static Animation instance;

    @Override
    public void onEnable() {

        instance = this;

        getCommand("animation").setExecutor(new AnimationCommand());
        getServer().getPluginManager().registerEvents(new InteractListener(), this);

    }

    @Override
    public void onDisable() {

    }

    public static Animation getInstance() {
        return instance;
    }
}
