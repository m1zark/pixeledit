package com.m1zark.pixeledit;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.Inventories;
import com.m1zark.pixeledit.listeners.CraftingListener;
import com.m1zark.pixeledit.util.Database.DataSource;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayerMP;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.m1zark.pixeledit.util.PokeUtils;
import com.m1zark.pixeledit.commands.CommandManager;
import com.m1zark.pixeledit.util.configuration.Config;

@Getter
@Plugin(id= PEInfo.ID, name= PEInfo.NAME, version= PEInfo.VERSION, description= PEInfo.DESCRIPTION, authors = "m1zark")
public class PixelEdit {
    @Inject private Logger logger;
    private static PixelEdit instance;
    private DataSource sql;
    @Inject @ConfigDir(sharedRoot = false) private Path configDir;
    private Config config;
    private boolean enabled = true;
    private Cause pixelEditCause;

    private List<UUID> devs = Lists.newArrayList(UUID.fromString("ff3877da-6b9c-468b-b10a-2982bce70a63"));

    @Listener
    public void onInitialization(GameInitializationEvent e){
        instance = this;
        pixelEditCause = Cause.builder().append(this).build(EventContext.builder().build());

        PEInfo.startup();
        this.enabled = PEInfo.dependencyCheck();

        if (this.enabled) {
            this.config = new Config();

            Sponge.getEventManager().registerListeners(this, new CraftingListener());
            new CommandManager().registerCommands(this);

            // Initialize data source and creates tables
            this.sql = new DataSource("PB_LOGS");
            this.sql.createTables();

            getConsole().ifPresent(console -> console.sendMessages(Text.of(PEInfo.PREFIX, "Initialization complete!")));
        }
    }

    @Listener
    public void onReload(GameReloadEvent e) {
        if (this.enabled) {
            this.config = new Config();
            getConsole().ifPresent(console -> console.sendMessages(Text.of(PEInfo.PREFIX, "Configurations have been reloaded")));
        }
    }

    @Listener
    public void onServerStop(GameStoppingEvent e) {
        try {
            this.sql.shutdown();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    @Listener
    public void pokeInteract(InteractEntityEvent.Secondary.MainHand event, @First Player player) {
        Optional<ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (event.getTargetEntity() instanceof EntityPixelmon && optHeldItem.isPresent() && optHeldItem.get().getType() == PokeUtils.scrollType) {
            EntityPixelmon pokemon = (EntityPixelmon) event.getTargetEntity();
            PlayerPartyStorage storage = PokeUtils.getPlayerStorage((EntityPlayerMP)player);

            if (Inventories.doesHaveNBT(optHeldItem.get(), "typeID")) {
                String type = optHeldItem.get().toContainer().get(DataQuery.of("UnsafeData","typeID")).get().toString();

                if (pokemon.hasOwner()) {
                    if (storage != null && storage.getTeam().contains(pokemon.getStoragePokemonData())) {
                        if (PokeUtils.updatePokemon(type, pokemon.getPokemonData(), player)) {
                            Inventories.removeItem(player, optHeldItem.get(), 1);

                            Chat.sendMessage(player, "&aYour &b" + pokemon.getPokemonName() + " &ahas been updated.");
                            player.playSound(SoundTypes.BLOCK_ENCHANTMENT_TABLE_USE, player.getLocation().getPosition(), 1.0);

                            pokemon.update(EnumUpdateType.Stats, EnumUpdateType.Nickname, EnumUpdateType.Ability, EnumUpdateType.HeldItem, EnumUpdateType.HP, EnumUpdateType.Moveset, EnumUpdateType.Friendship);
                            pokemon.getPokemonData().getStats().setLevelStats(pokemon.getPokemonData().getNature(), pokemon.getBaseStats(), pokemon.getPokemonData().getLevel());
                        }
                    } else {
                        Chat.sendMessage(player, "&cYou can only use this on your own Pok\u00E9mon.");
                    }
                } else {
                    Chat.sendMessage(player, "&cYou cannot use this on a wild Pok\u00E9mon.");
                }
            }
        }
    }

    public static PixelEdit getInstance() {
        return instance;
    }

    public Optional<ConsoleSource> getConsole() {
        return Optional.ofNullable(Sponge.isServerAvailable() ? Sponge.getServer().getConsole() : null);
    }
}
