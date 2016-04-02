package com.skelril.Pitfall.sponge;

import com.skelril.Pitfall.config.YAMLConfiguration;
import com.skelril.Pitfall.util.yaml.YAMLProcessor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.ItemType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class SpongeConfiguration extends YAMLConfiguration {
    public SpongeConfiguration(YAMLProcessor config) {
        super(config);
    }

    // Target Block
    public BlockType targetType;

    // Black List
    public List<String> blackListedBlocks;

    public List<String> ignoredGameModes;

    @Override
    public void load() {
        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Target Block
        targetType = Sponge.getRegistry().getType(BlockType.class, config.getString("target-block.name", BlockTypes.CLAY.getId())).get();

        blackListedBlocks = config.getStringList("blacklist.blacklisted-blocks", Arrays.asList(
                BlockTypes.CHEST.getId(), BlockTypes.TRAPPED_CHEST.getId(), BlockTypes.STANDING_SIGN.getId()
        ));

        ignoredGameModes = config.getStringList("ignored-gamemodes", Arrays.asList(
                GameModes.SPECTATOR.getId()
        ));

        super.load();
    }

    @Override
    public File getWorkingDirectory() {
        return PitfallPlugin.inst().getDataFolder();
    }
}