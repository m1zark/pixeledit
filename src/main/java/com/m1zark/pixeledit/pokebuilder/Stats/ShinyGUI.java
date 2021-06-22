package com.m1zark.pixeledit.pokebuilder.Stats;

import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.pixeledit.pokebuilder.BuilderGUI;
import com.m1zark.pixeledit.pokebuilder.SharedIcons;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.client.gui.GuiResources;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;

public class ShinyGUI extends InventoryManager {
    private Player player;
    private BuilderGUI base;
    private int shiny;

    public ShinyGUI(Player player, BuilderGUI base) {
        super(player, 3, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Shiny")));
        this.player = player;
        this.base = base;
        this.shiny = base.shiny;

        setupDesign();
    }

    private void setupDesign() {
        int x = 0;
        for(int y = 0; y < 3; ++x) {
            if (x > 8) {
                x = 0;
                ++y;
            }
            if (y >= 3) { break; }

            this.addIcon(SharedIcons.BorderIcon(x + 9 * y,  DyeColors.GRAY, ""));
        }

        this.addIcon(this.selectedIcon(0));
        this.addIcon(SharedIcons.infoIcon(18, "Shiny Info", Config.getInfo("shinyInfo"), -1, this.base));

        this.addIcon(this.shinyIcon(12, true, this.shiny==1));
        this.addIcon(this.shinyIcon(14, false, this.shiny==0));

        Icon reset = SharedIcons.resetIcon(8);
        reset.addListener(clickable -> {
            if(this.shiny != -1) {
                this.base.totalCost = this.base.totalCost - Config.getPrices("Shiny", this.shiny==1 ? "Shiny" : "Non-Shiny");
                this.shiny = -1;
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.shinyIcon(12, true, false));
                    this.addIcon(this.shinyIcon(14, false, false));
                    this.addIcon(this.selectedIcon(0));
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        this.addIcon(reset);

        Icon back = SharedIcons.backIcon(26);
        back.addListener(clickable -> {
            this.base.shiny = this.shiny;
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                //this.player.closeInventory();
                this.base.addIcon(this.base.shinyIcon(this.shiny != -1));
                this.base.addIcon(this.base.confirmIcon());
                this.base.updateContents();
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);
    }

    private Icon shinyIcon(int slot, boolean shiny, boolean selected) {
        boolean canClick = true;

        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours(shiny ? "&7Click here to select the &6Shiny &7option." : "&7Click here to select the &8Non-Shiny &7option.")));
        itemLore.add(Text.of(Chat.embedColours("")));

        if(shiny) {
            if(this.base.pokemon.isShiny()) {
                itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00E9mon is already shiny.")));
                canClick = false;
            } else {
                itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + Config.getPrices("Shiny", "Shiny") + " " + Config.getItem("currencyName") + (Config.getPrices("Shiny", "Shiny")==1 ? "" : "(s)"))));
            }
        } else {
            if(!this.base.pokemon.isShiny()) {
                itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00E9mon is already not shiny.")));
                canClick = false;
            } else {
                itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + Config.getPrices("Shiny", "Non-Shiny") + " " + Config.getItem("currencyName") + (Config.getPrices("Shiny", "Non-Shiny")==1 ? "" : "(s)"))));
            }
        }

        ItemStack item = ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:pixelmon_sprite").orElse(ItemTypes.STAINED_HARDENED_CLAY))
                .add(Keys.DYE_COLOR, shiny ? DyeColors.YELLOW : DyeColors.BLACK)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&a&l" + (shiny ? "Shiny" : "Non-Shiny"))))
                .add(Keys.ITEM_LORE, itemLore)
                .build();

        Icon icon = new Icon(slot, ItemStack.builder().fromContainer(item.toContainer().set(DataQuery.of("UnsafeData","SpriteName"), this.getSprite(shiny))).build());

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        if(canClick) {
            icon.addListener(clickable -> {
                if(this.shiny == -1) {
                    this.shiny = shiny ? 1 : 0;
                    this.base.totalCost = this.base.totalCost + Config.getPrices("Shiny", shiny ? "Shiny" : "Non-Shiny");

                    Sponge.getScheduler().createTaskBuilder().execute(() -> {
                        this.addIcon(this.selectedIcon(0));
                        this.addIcon(this.shinyIcon(slot, shiny, true));
                        this.updateContents();
                    }).delayTicks(1L).submit(PixelEdit.getInstance());

                }
            });
        }

        return icon;
    }

    private Icon selectedIcon(int slot) {
        ItemStack item = ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, getItemName()).orElse(ItemTypes.CONCRETE))
                .add(Keys.DYE_COLOR, this.shiny == 1 ? DyeColors.YELLOW : (this.shiny == 0 ? DyeColors.BLACK : DyeColors.WHITE))
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lSelected Shininess")))
                .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(Chat.embedColours("&7Current: &e" + (this.shiny == 1 ? "Shiny" : (this.shiny == 0 ? "Non-Shiny" : ""))))))
                .build();

        return new Icon(slot, ItemStack.builder().fromContainer(item.toContainer().set(DataQuery.of("UnsafeData","SpriteName"), this.getSprite(this.shiny == 1))).build());
    }

    private String getItemName() {
        return this.shiny == -1 ? "minecraft:concrete" : "pixelmon:pixelmon_sprite";
    }

    private String getSprite(boolean shiny) {
        return "pixelmon:" + GuiResources.getSpritePath(this.base.pokemon.getSpecies(), this.base.pokemon.getForm(), this.base.pokemon.getGender(), this.base.pokemon.getCustomTexture(), shiny);
    }
}
