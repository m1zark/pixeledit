package com.m1zark.pixeledit.pokebuilder.Stats;

import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.pixeledit.pokebuilder.BuilderGUI;
import com.m1zark.pixeledit.pokebuilder.SharedIcons;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;

public class GrowthGUI extends InventoryManager {
    private Player player;
    private BuilderGUI base;
    private String growth;

    public GrowthGUI(Player player, BuilderGUI base) {
        super(player, 5, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Growth")));
        this.player = player;
        this.base = base;
        this.growth = base.growth;

        setupDesign();
    }

    private void setupDesign() {
        int x = 0;
        for(int y = 0; y < 5; ++x) {
            if (x > 8) {
                x = 0;
                ++y;
            }
            if (y >= 5) { break; }

            this.addIcon(SharedIcons.BorderIcon(x + (9 * y),  DyeColors.GRAY, ""));
        }

        this.addIcon(this.selectedIcon(0));
        this.addIcon(SharedIcons.infoIcon(36, "Growth Info", Config.getInfo("growthInfo"), -1, this.base));

        int i = 11;
        int in = 8;
        for(int ord = 0; ord < 9; ++ord) {
            this.addIcon(this.growthIcon(i, EnumGrowth.getGrowthFromIndex(in), this.growth.equalsIgnoreCase(EnumGrowth.getGrowthFromIndex(in).name())));
            if (in == 8) { in = 0; }
            else { ++in; }

            if (i > 13 && i < 20) { i = 18; }
            if (i > 22 && i < 29) { i = 27; }

            i += 2;
        }

        Icon reset = SharedIcons.resetIcon(8);
        reset.addListener(clickable -> {
            if (!this.growth.equals("N/A")) {
                this.base.totalCost = this.base.totalCost - Config.getPrices("Sizes", this.growth);

                int a = 11;
                int b = 8;
                for(int ord = 0; ord < 9; ++ord) {
                    this.addIcon(this.growthIcon(a, EnumGrowth.getGrowthFromIndex(b), false));
                    if (b == 8) { b = 0; }
                    else { ++b; }

                    if (a > 13 && a < 20) { a = 18; }
                    if (a > 22 && a < 29) { a = 27; }

                    a += 2;
                }

                this.growth = "N/A";
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.selectedIcon(0));
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        this.addIcon(reset);

        Icon back = SharedIcons.backIcon(44);
        back.addListener(clickable -> {
            this.base.growth = this.growth;
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                //this.player.closeInventory(Cause.of(NamedCause.source(PixelEdit.getInstance())));
                this.base.addIcon(this.base.growthIcon(!this.growth.equalsIgnoreCase("N/A")));
                this.base.addIcon(this.base.confirmIcon());
                this.base.updateContents();
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);
    }

    private Icon growthIcon(int slot, EnumGrowth growth, boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to select the &a"+ growth.name() +" &7size.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        if(this.base.pokemon.getGrowth().toString().equalsIgnoreCase(growth.name())) {
            itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00E9mon is already this size.")));
        }else{
            itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + Config.getPrices("Sizes", growth.name()) + " " + Config.getItem("currencyName") + (Config.getPrices("Sizes", growth.name())==1 ? "" : "(s)"))));
        }

        Icon icon = new Icon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:moomoo_milk").orElse(ItemTypes.BARRIER))
                .quantity(this.getQuantity(growth.name()))
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&6&l" + growth.name())))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        if(!this.base.pokemon.getGrowth().toString().equalsIgnoreCase(growth.name())) {
            icon.addListener(clickable -> {
                if (this.growth.equalsIgnoreCase("N/A")) {
                    this.growth = growth.name();
                    this.base.totalCost = this.base.totalCost + Config.getPrices("Sizes", growth.name());
                    Sponge.getScheduler().createTaskBuilder().execute(() -> {
                        this.addIcon(this.selectedIcon(0));
                        int i = 11;
                        int in = 8;

                        for (int ord = 0; ord < 9; ++ord) {
                            this.addIcon(this.growthIcon(i, EnumGrowth.getGrowthFromIndex(in), this.growth.equalsIgnoreCase(EnumGrowth.getGrowthFromIndex(in).name())));
                            if (in == 8) {
                                in = 0;
                            } else {
                                ++in;
                            }

                            if (i > 13 && i < 20) {
                                i = 18;
                            }
                            if (i > 22 && i < 29) {
                                i = 27;
                            }

                            i += 2;
                        }

                        this.updateContents();
                    }).delayTicks(1L).submit(PixelEdit.getInstance());
                }
            });
        }

        return icon;
    }

    private Icon selectedIcon(int slot) {
        return new Icon(slot, ItemStack.builder()
                .add(Keys.DYE_COLOR, this.getColor(this.growth))
                .itemType(Sponge.getRegistry().getType(ItemType.class, this.growth.equalsIgnoreCase("n/a") ? "minecraft:concrete" : "pixelmon:moomoo_milk").orElse(ItemTypes.BARRIER))
                .quantity(this.getQuantity(this.growth))
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lSelected Growth")))
                .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(Chat.embedColours("&7Current: &e" + this.growth))))
                .build());
    }

    private DyeColor getColor(String growth) {
        return growth.equals("Microscopic") ? DyeColors.RED
                : (growth.equals("Pygmy") ? DyeColors.LIME
                : (growth.equals("Runt") ? DyeColors.LIGHT_BLUE
                : (growth.equals("Small") ? DyeColors.ORANGE
                : (growth.equals("Ordinary") ? DyeColors.PURPLE
                : (growth.equals("Huge") ? DyeColors.PINK
                : (growth.equals("Giant") ? DyeColors.YELLOW
                : (growth.equals("Enormous") ? DyeColors.MAGENTA
                : (growth.equals("Ginormous") ? DyeColors.BROWN
                : DyeColors.WHITE))))))));
    }

    private int getQuantity(String growth) {
        return growth.equals("Microscopic") ? 1
                : (growth.equals("Pygmy") ? 2
                : (growth.equals("Runt") ? 3
                : (growth.equals("Small") ? 4
                : (growth.equals("Ordinary") ? 5
                : (growth.equals("Huge") ? 6
                : (growth.equals("Giant") ? 7
                : (growth.equals("Enormous") ? 8
                : (growth.equals("Ginormous") ? 9
                : 1))))))));
    }
}
