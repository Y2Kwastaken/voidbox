package sh.miles.voidbox;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import sh.miles.megumi.core.command.MegumiCommand;
import sh.miles.megumi.core.menu.listener.MenuListener;
import sh.miles.megumi.core.world.block.CustomBlock;
import sh.miles.megumi.core.world.block.event.CustomBlockListener;
import sh.miles.voidbox.block.VoidBox;
import sh.miles.voidbox.command.VoidboxCommand;
import sh.miles.voidbox.events.ItemDropListeners;

public class VoidBoxPlugin extends JavaPlugin {

    @Getter
    private static VoidBoxPlugin instance;

    private VoidboxCommand voidboxCommand;

    @Override
    public void onEnable() {
        instance = this;

        // voidbox
        VoidBox voidbox = new VoidBox(this);
        CustomBlock.register(voidbox);

        // commands
        this.voidboxCommand = new VoidboxCommand(voidbox);
        MegumiCommand.register(voidboxCommand);

        // required events for this plugin
        getServer().getPluginManager().registerEvents(new ItemDropListeners(voidbox), this);

        // required events for megumi
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new CustomBlockListener(this), this);

        // logs start of plugin
        getLogger().info("VoidBox has been enabled!");
    }

    @Override
    public void onDisable() {

        // unregisters commands
        MegumiCommand.unregister(voidboxCommand);

        // logs end of plugin
        getLogger().info("VoidBox has been disabled!");
    }

}
