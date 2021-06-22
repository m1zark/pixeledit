package com.m1zark.pixeledit.pokebuilder;

import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.Inventories;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.pixeledit.util.PokeUtils;
import com.m1zark.pixeledit.util.configuration.Config;
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
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;

public class SharedIcons {
    public static Icon confettiBorderIcon(int slot){
        DyeColor[] colors = {DyeColors.BLUE,DyeColors.CYAN,DyeColors.LIGHT_BLUE,DyeColors.LIME,DyeColors.MAGENTA,DyeColors.ORANGE,DyeColors.PINK,DyeColors.PURPLE,DyeColors.RED,DyeColors.YELLOW};

        return new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.STAINED_GLASS_PANE)
                .add(Keys.DYE_COLOR, colors[(int)Math.floor(Math.random() * colors.length)])
                .add(Keys.DISPLAY_NAME, Text.of(TextStyles.RESET,""))
                .build());
    }

    public static Icon BorderIcon(int slot, DyeColor color, String name) {
        return new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.STAINED_GLASS_PANE)
                .quantity(1)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours(name)))
                .add(Keys.DYE_COLOR, color)
                .build());
    }

    public static Icon closeIcon(int slot) {
        return new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.CONCRETE)
                .quantity(1)
                .add(Keys.DYE_COLOR, DyeColors.RED)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&c&lExit")))
                .build());
    }

    public static Icon confirmIcon(int slot, BuilderGUI base) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Your total cost is &c" + base.totalCost + " " + Config.getItem("currencyName") + (base.totalCost==1 ? "" : "(s)"))));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Clicking this button will take you to a final confirmation page.")));

        return new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.CONCRETE)
                .quantity(1)
                .add(Keys.DYE_COLOR, base.hasSelections() ? DyeColors.LIME: DyeColors.GRAY)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&a&lConfirm")))
                .add(Keys.ITEM_LORE, itemLore)
                .build());
    }

    public static Icon confirmFinalIcon(int slot, Player p, BuilderGUI base) {
        int balance = Inventories.getItemCount(p, PokeUtils.currencyItem());

        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Your total cost is &c" + base.totalCost + " " + Config.getItem("currencyName") + (base.totalCost==1 ? "" : "(s)"))));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Clicking this button will confirm your purchase.")));
        itemLore.add(Text.of(Chat.embedColours("&7Once clicked these changes cannot be reversed.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        if(balance - base.totalCost >= 0) {
            itemLore.add(Text.of(Chat.embedColours("&7Your updated balance will be &a" + (balance-base.totalCost) + " " + Config.getItem("currencyName") + (balance-base.totalCost==1 ? "" : "(s)"))));
        } else {
            itemLore.add(Text.of(Chat.embedColours("&cYou do not have enough " + Config.getItem("currencyName") + (base.totalCost==1 ? "" : "(s)") + " to complete this purchase.")));
        }
        return new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.CONCRETE)
                .quantity(1)
                .add(Keys.DYE_COLOR, balance - base.totalCost >= 0 ? DyeColors.LIME : DyeColors.GRAY)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&a&lConfirm")))
                .add(Keys.ITEM_LORE, itemLore)
                .build());
    }

    public static Icon totalCurrencyIcon(int slot, Player p) {
        int balance = Inventories.getItemCount(p, PokeUtils.currencyItem());

        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&a"+ balance + " " + Config.getItem("itemName") + (balance==1 ? "" : "(s)"))));
        itemLore.add(Text.of(Chat.embedColours("")));

        String[] newInfo = Config.getHowTo().split("\n");
        for(String s:newInfo) itemLore.add(Text.of(Chat.embedColours(s)));

        return new Icon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, Config.getItem("itemType")).get())
                .quantity(1)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&2&lCurrent Balance")))
                .add(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)))
                .add(Keys.HIDE_ENCHANTMENTS, true)
                .add(Keys.ITEM_LORE, itemLore)
                .build());
    }

    public static Icon infoIcon(int slot, String name, String info, int display, BuilderGUI base) {
        String[] newInfo = PokeUtils.insertLinebreaks(info,40).split("\n");
        ArrayList<Text> itemLore = new ArrayList<>();
        for(String s:newInfo) itemLore.add(Text.of(Chat.embedColours("&7" + s)));

        if(display == 0){
            itemLore.add(Text.of(Chat.embedColours("")));
            itemLore.add(Text.of(Chat.embedColours("&cPlease note that the cost is &l&nper stat.")));
        }
        if(display == 1){
            String[] abilities = base.pokemon.getBaseStats().abilities;

            itemLore.add(Text.of(Chat.embedColours("")));
            itemLore.add(Text.of(Chat.embedColours("&b" + PokeUtils.updatePokemonName(base.pokemon.getDisplayName()) + "'s Abilities:")));
            itemLore.add(Text.of(Chat.embedColours("&71: &a" + abilities[0])));
            itemLore.add(Text.of(Chat.embedColours("&72: &a" + (abilities[1] != null ? abilities[1] : "N/A"))));
            itemLore.add(Text.of(Chat.embedColours("&7HA: &a" + (abilities[2] != null ? abilities[2] : "N/A"))));
        }
        if(display == 3){
            itemLore.add(Text.of(Chat.embedColours("")));
            itemLore.add(Text.of(Chat.embedColours("&cPlease note that the cost is &l&nper move.")));
        }

        return new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.PAPER)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&l" + name)))
                .add(Keys.ITEM_LORE, itemLore)
                .build());
    }

    public static Icon resetIcon(int slot) {
        return new Icon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trash_can").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&c&lReset Options")))
                .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(Chat.embedColours("&7Click here to reset the current option back to default"))))
                .build());
    }

    public static Icon backIcon(int slot) {
        return new Icon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:eject_button").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&c&l\u21FD Return to Pok\u00E9Builder Menu \u21FD")))
                .build());
    }
}