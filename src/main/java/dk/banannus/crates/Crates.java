package dk.banannus.crates;

import dk.banannus.crates.commands.AnimationCommand;
import dk.banannus.crates.events.InteractListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Crates extends JavaPlugin {

    public static Crates instance;

    @Override
    public void onEnable() {

        instance = this;

        getCommand("animation").setExecutor(new AnimationCommand());
        getServer().getPluginManager().registerEvents(new InteractListener(), this);

    }

    @Override
    public void onDisable() {

    }

    public static Crates getInstance() {
        return instance;
    }
}
