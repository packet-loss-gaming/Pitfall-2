package com.skelril.Pitfall.sponge;

import com.google.inject.Inject;
import com.skelril.Pitfall.LocalConfiguration;
import com.skelril.Pitfall.util.yaml.YAMLFormat;
import com.skelril.Pitfall.util.yaml.YAMLProcessor;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.data.property.block.PassableProperty;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Plugin(id = "com.skelril.pitfall", name = "Pitfall", version = "2.3", description = "Make traps! Catch griefers!")
public class PitfallPlugin {

    private static PitfallPlugin inst;
    private SpongeConfiguration config;
    private PitfallSpongeWorker pitfallSpongeWorker;

    @Inject
    private PluginContainer container;

    @Inject
    private Logger logger;

    public PitfallPlugin() {
        inst = this;
    }

    public Logger getLogger() {
        return logger;
    }

    public File getDataFolder() {
        ConfigManager cfgManager = Sponge.getGame().getConfigManager();
        return cfgManager.getPluginConfig(PitfallPlugin.inst()).getDirectory().toFile();
    }

    @Listener
    public void onStart(GameStartingServerEvent event) {
        // Config
        File configFile = new File(getDataFolder(), "config.yml");
        boolean newFile = initConfig(configFile);
        config = new SpongeConfiguration(new YAMLProcessor(
                configFile,
                true,
                YAMLFormat.EXTENDED
        ));

        if (newFile) {
            config.init();
        }
        config.load();

        // New version printing
        getLogger().info(
                container.getName() + " "
                        + (container.getVersion().isPresent() ? container.getVersion().get() + " " : "")
                        + "enabled."
        );

        // Setup the Engine
        pitfallSpongeWorker = new PitfallSpongeWorker();
        pitfallSpongeWorker.setDefaultTrapDelay(config.trapDelay);
        pitfallSpongeWorker.setDefaultReturnDelay(config.returnDelay);
        pitfallSpongeWorker.activateItemCheck(config.enableItemTrap);
        pitfallSpongeWorker.activateCreatureCheck(config.enableMonsterTrap);
        pitfallSpongeWorker.setMaxRadius(config.maxRadius);
        pitfallSpongeWorker.setDestructiveHeight(config.destrutiveHeight);
        pitfallSpongeWorker.setTargetBlock(config.targetType);

        // Blacklist setup
        if (config.useBlackList) {

            Set<BlockType> blackList = pitfallSpongeWorker.getBlackList();

            for (String type : config.blackListedBlocks) {
                blackList.add(Sponge.getRegistry().getType(BlockType.class, type).get());
            }

            if (config.ignorePassable) {
                for (BlockType blockType : Sponge.getRegistry().getAllOf(BlockType.class)) {
                    Optional<PassableProperty> optProperty = blockType.getProperty(PassableProperty.class);
                    if (optProperty.isPresent() && optProperty.get().getValue()) {
                        blackList.add(blockType);
                    }
                }
            }
        }

        // Start the watcher
        Task.builder().execute(pitfallSpongeWorker).intervalTicks(5).submit(this);
    }

    @Listener
    public void onStop(GameStoppingServerEvent event) {
        pitfallSpongeWorker.revertAll();
        getLogger().info(container.getName() + " disabled.");
    }

    private boolean initConfig(File file) {
        if (file.exists()) {
            return false;
        }

        try {
            file.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * You should not use this to access Pitfall from your own plugins.
     *
     * @return the PitfallPlugin instance
     */
    public static PitfallPlugin inst() {

        return inst;
    }

    public static PluginContainer container() {
        return inst.container;
    }

    public LocalConfiguration getLocalConfiguration() {

        return config;
    }

    public PitfallSpongeWorker getPitfallWorker() {

        return pitfallSpongeWorker;
    }
}
