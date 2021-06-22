package com.m1zark.pixeledit.pokebuilder.Stats;

import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.pixeledit.pokebuilder.BuilderGUI;
import com.m1zark.pixeledit.pokebuilder.SharedIcons;
import com.m1zark.pixeledit.util.configuration.Config;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;

public class HappinessGUI extends InventoryManager {
    private Player player;
    private BuilderGUI base;
    private int happiness;

    public HappinessGUI(Player player, BuilderGUI base) {
        super(player, 3, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Happiness")));
        this.player = player;
        this.base = base;
        this.happiness = base.happiness;

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
        this.addIcon(SharedIcons.infoIcon(18, "Happiness Info", Config.getInfo("happinessInfo"), -1, this.base));

        this.addIcon(this.happinessIcon(12, 255, this.happiness == 255));
        this.addIcon(this.happinessIcon(14, 0, this.happiness == 0));

        Icon reset = SharedIcons.resetIcon(8);
        reset.addListener(clickable -> {
            if(this.happiness != -1) {
                this.base.totalCost = this.base.totalCost - (this.happiness == 255 ? Config.getPrices("Happiness","maxHappiness") : Config.getPrices("Happiness","zeroHappiness"));
                this.happiness = -1;
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.happinessIcon(12,255,false));
                    this.addIcon(this.happinessIcon(14,0,false));
                    this.addIcon(this.selectedIcon(0));
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        this.addIcon(reset);

        Icon back = SharedIcons.backIcon(26);
        back.addListener(clickable -> {
            this.base.happiness = this.happiness;
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                //this.player.closeInventory(Cause.of(NamedCause.source(PixelEdit.getInstance())));
                this.base.addIcon(this.base.happinessIcon(this.happiness != -1));
                this.base.addIcon(this.base.confirmIcon());
                this.base.updateContents();
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);
    }

    private Icon happinessIcon(int slot, int amount, boolean selected) {
        int cost = slot == 12 ? Config.getPrices("Happiness","maxHappiness") : Config.getPrices("Happiness","zeroHappiness");

        ArrayList<Text> itemLore = new ArrayList<>();
        if(slot == 12) {
            itemLore.add(Text.of(Chat.embedColours("&7Click here to instantly get max happiness.")));
            itemLore.add(Text.of(Chat.embedColours("")));
            if(this.base.pokemon.getFriendship() == 255) {
                itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00E9mon already has max happiness.")));
            }else{
                itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + cost + " " + Config.getItem("currencyName") + (cost == 1 ? "" : "(s)"))));
            }
        }
        else {
            itemLore.add(Text.of(Chat.embedColours("&7Click here to instantly get zero happiness.")));
            itemLore.add(Text.of(Chat.embedColours("")));
            if(this.base.pokemon.getFriendship() == 0) {
                itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00E9mon already has zero happiness.")));
            }else{
                itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + cost + " " + Config.getItem("currencyName") + (cost == 1 ? "" : "(s)"))));
            }
        }

        Icon icon = new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.CONCRETE)
                .add(Keys.DYE_COLOR, slot == 12 ? DyeColors.LIME : DyeColors.YELLOW)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&a&l" + (slot == 12 ? "Max Happiness" : "Zero Happiness"))))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        icon.addListener(clickable -> {
            if(this.base.pokemon.getFriendship() != amount) {
                if(this.happiness == -1) {
                    this.happiness = amount;
                    this.base.totalCost = this.base.totalCost + cost;

                    Icon update = this.happinessIcon(slot, amount, true);
                    Sponge.getScheduler().createTaskBuilder().execute(() -> {
                        this.addIcon(this.selectedIcon(0));
                        this.addIcon(update);
                        this.updateContents();
                    }).delayTicks(1L).submit(PixelEdit.getInstance());
                }
            }
        });
        return icon;
    }

    private Icon selectedIcon(int slot) {
        return new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.CONCRETE)
                .add(Keys.DYE_COLOR, this.happiness == 255 ? DyeColors.LIME : (this.happiness == 0 ? DyeColors.YELLOW : DyeColors.WHITE))
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lSelected Happiness")))
                .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(Chat.embedColours("&7Current: &e" + (this.happiness == 255 ? "Max Happiness" : (this.happiness == 0 ? "Zero Happiness" : ""))))))
                .build());
    }
}
