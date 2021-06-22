package com.m1zark.pixeledit.pokebuilder.Stats;

import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.pixeledit.pokebuilder.BuilderGUI;
import com.m1zark.pixeledit.pokebuilder.SharedIcons;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;

public class GenderGUI extends InventoryManager {
    private Player player;
    private BuilderGUI base;
    private String gender;

    public GenderGUI(Player player, BuilderGUI base) {
        super(player, 3, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Gender")));
        this.player = player;
        this.base = base;
        this.gender = base.gender;

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

        this.addIcon(this.selectedIcon());
        this.addIcon(SharedIcons.infoIcon(18, "Gender Info", Config.getInfo("genderInfo"), -1, this.base));

        this.addIcon(this.genderIcon(true, this.gender.equalsIgnoreCase("male")));
        this.addIcon(this.genderIcon(false, this.gender.equalsIgnoreCase("female")));

        Icon reset = SharedIcons.resetIcon(8);
        reset.addListener(clickable -> {
            if (!this.gender.equalsIgnoreCase("N/A")) {
                this.base.totalCost = this.base.totalCost - Config.getPrices("Gender", this.gender);
                this.gender = "N/A";
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.genderIcon(true, false));
                    this.addIcon(this.genderIcon(false, false));
                    this.addIcon(this.selectedIcon());
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        this.addIcon(reset);

        Icon back = SharedIcons.backIcon(26);
        back.addListener(clickable -> {
            this.base.gender = this.gender;
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                //this.player.closeInventory(Cause.of(NamedCause.source(PixelEdit.getInstance())));
                this.base.addIcon(this.base.genderIcon(!this.gender.equalsIgnoreCase("N/A")));
                this.base.addIcon(this.base.confirmIcon());
                this.base.updateContents();
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);
    }

    private Icon genderIcon(boolean male, boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to select the "+ (male ? "&bMale" : "&dFemale") +" &7gender.")));
        itemLore.add(Text.of(Chat.embedColours("")));

        Icon icon;
        boolean canClick = true;

        if (male) {
            if(this.base.pokemon.getGender().equals(Gender.Male)) {
                canClick = false;
                itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00E9mon is already &bMale.")));
            }
            else {
                itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + Config.getPrices("Gender", "Male") + " " + Config.getItem("currencyName") + (Config.getPrices("Gender", "Male")==1 ? "" : "(s)"))));
            }

            icon = new Icon(12, ItemStack.builder()
                    .itemType(Sponge.getRegistry().getType(ItemType.class, this.getItemName("male")).orElse(ItemTypes.CONCRETE))
                    .add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE)
                    .add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "\u2642 Male \u2642"))
                    .add(Keys.ITEM_LORE, itemLore).build());
        } else {
            if(this.base.pokemon.getGender().equals(Gender.Female)) {
                canClick = false;
                itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00E9mon is already &dFemale.")));
            }
            else {
                itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + Config.getPrices("Gender", "Female") + " " + Config.getItem("currencyName") + (Config.getPrices("Gender", "Female")==1 ? "" : "(s)"))));
            }

            icon = new Icon(14, ItemStack.builder()
                    .itemType(Sponge.getRegistry().getType(ItemType.class, this.getItemName("female")).orElse(ItemTypes.STAINED_HARDENED_CLAY))
                    .add(Keys.DYE_COLOR, DyeColors.PINK)
                    .add(Keys.DISPLAY_NAME, Text.of(TextColors.LIGHT_PURPLE, "\u2640 Female \u2640"))
                    .add(Keys.ITEM_LORE, itemLore).build());
        }

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        if(canClick) {
            icon.addListener(clickable -> {
                if(this.gender.equalsIgnoreCase("N/A")) {
                    this.gender = male ? "Male" : "Female";
                    this.base.totalCost = this.base.totalCost + Config.getPrices("Gender", this.gender);

                    Sponge.getScheduler().createTaskBuilder().execute(() -> {
                        this.addIcon(this.genderIcon(true, male));
                        this.addIcon(this.genderIcon(false, !male));
                        this.addIcon(this.selectedIcon());
                        this.updateContents();
                    }).delayTicks(1L).submit(PixelEdit.getInstance());
                }
            });
        }

        return icon;
    }

    private Icon selectedIcon() {
        return new Icon(0, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, this.getItemName(this.gender)).orElse(ItemTypes.CONCRETE))
                .add(Keys.DYE_COLOR, this.gender.equals("Male") ? DyeColors.LIGHT_BLUE : (this.gender.equals("Female") ? DyeColors.PINK : DyeColors.WHITE))
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lSelected Gender")))
                .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(Chat.embedColours("&7Current: &e" + this.gender))))
                .build());
    }

    private String getItemName(String name) {
        return name.equals("N/A") ? "minecraft:concrete" : "pixelmon:" + (name.equalsIgnoreCase("male") ? "sea_incense" : "odd_incense");
    }
}
