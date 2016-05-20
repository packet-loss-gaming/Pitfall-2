package com.skelril.Pitfall.sponge;

import com.google.common.reflect.TypeToken;
import com.skelril.Pitfall.config.ConfigurateConfiguration;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SpongeConfiguration extends ConfigurateConfiguration {
    public SpongeConfiguration(ConfigurationLoader<CommentedConfigurationNode> config) {
        super(config);
    }

    // Target Block
    public BlockType targetType;

    // Black List
    public List<String> blackListedBlocks;

    public List<String> ignoredGameModes;

    @Override
    public void load() {
        super.load();

        // Target Block
        targetType = Sponge.getRegistry().getType(BlockType.class, node.getNode("target-block", "name").getString(BlockTypes.CLAY.getId())).get();

        try {
            blackListedBlocks = node.getNode("blacklist", "blacklisted-blocks").getList(TypeToken.of(String.class), Arrays.asList(
                    BlockTypes.CHEST.getId(), BlockTypes.TRAPPED_CHEST.getId(), BlockTypes.STANDING_SIGN.getId()
            ));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        try {
            ignoredGameModes = node.getNode("ignored-gamemodes").getList(TypeToken.of(String.class), Arrays.asList(
                    GameModes.SPECTATOR.getId()
            ));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        try {
            config.save(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}