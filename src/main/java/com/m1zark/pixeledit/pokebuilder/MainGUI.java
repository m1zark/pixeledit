package com.m1zark.pixeledit.pokebuilder;

import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.pixeledit.util.PokeUtils;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class MainGUI extends InventoryManager {
    private Player player;
    private PlayerPartyStorage storage;

    public MainGUI(Player p) {
        super(p, 3, Text.of(Chat.embedColours("&4&lPok\u00E9Builder")));
        this.player = p;
        this.storage = PokeUtils.getPlayerStorage((EntityPlayerMP) p);

        this.setupDesign();
    }

    private void setupDesign() {
        int s = 0;

        for(int x = 0; x < 9; x++) { this.addIcon(SharedIcons.BorderIcon(x, DyeColors.RED, "")); }

        this.addIcon(SharedIcons.BorderIcon(9, DyeColors.BLACK, ""));

        for(int slot = 10; slot <= 16; slot++){
            if(slot == 13) {
                this.addIcon(SharedIcons.totalCurrencyIcon(slot, this.player));
            } else {
                Pokemon pokemon = storage.get(s);
                if (pokemon != null) {
                    PokemonData pkmn = new PokemonData(pokemon, null);

                    Icon poke = new Icon(slot, pkmn.getSprite("", false, this.player));
                    poke.addListener(clickable -> {
                        if (!pkmn.isEgg()) {
                            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                                this.storage.retrieveAll();
                                this.player.openInventory((new BuilderGUI(this.player, pokemon)).getInventory());
                            }).delayTicks(1L).submit(PixelEdit.getInstance());
                        }
                    });
                    this.addIcon(poke);
                } else {
                    this.addIcon(SharedIcons.BorderIcon(slot, DyeColors.GRAY, "Slot " + (s + 1) + " is empty!"));
                }

                s++;
            }
        }

        this.addIcon(SharedIcons.BorderIcon(17, DyeColors.BLACK, ""));

        for(int x = 18; x < 27; x++) { this.addIcon(SharedIcons.BorderIcon(x, DyeColors.WHITE, "")); }
    }
}
