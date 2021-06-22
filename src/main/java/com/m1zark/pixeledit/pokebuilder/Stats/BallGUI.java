package com.m1zark.pixeledit.pokebuilder.Stats;

import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.pixeledit.pokebuilder.BuilderGUI;
import com.m1zark.pixeledit.pokebuilder.SharedIcons;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
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
import java.util.Collections;
import java.util.List;

public class BallGUI extends InventoryManager {
    private Player player;
    private BuilderGUI base;
    private String pokeball;
    private int slot = -1;
    private int page;
    private int maxPage;

    public BallGUI(Player player, BuilderGUI base, int page) {
        super(player, 5, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Pok\u00E9Balls")));
        this.player = player;
        this.base = base;
        this.pokeball = base.pokeball;
        this.page = page;
        int size = this.getPokeballs().size();
        this.maxPage = size % 25 == 0 && size / 25 != 0 ? size / 25 : size / 25 + 1;

        setupDesign();
        setupPokeballs();
    }

    private void setupDesign() {
        int index;
        int x = 0;
        int y = 0;

        for (index = 0; y < 6 && index < 45; ++index) {
            if (x == 9) {
                x = 0;
                ++y;
            }
            if (y == 2) {
                this.addIcon(SharedIcons.BorderIcon(x + 9 * y, DyeColors.GRAY, ""));
                ++x;
                continue;
            }
            this.addIcon(SharedIcons.BorderIcon(x + 9 * y, y < 3 ? DyeColors.RED : DyeColors.WHITE, ""));
            ++x;
        }
        for (y = 0; y <= 4; ++y) {
            for (x = 2; x < 7 && index != 25; ++x, ++index) {
                this.addIcon(SharedIcons.BorderIcon(x + 9 * y, DyeColors.GRAY, ""));
            }
        }

        this.addIcon(this.selectedIcon(0));
        this.addIcon(SharedIcons.infoIcon(36, "Pok\u00E9Ball Info", Config.getInfo("pokeballInfo"), -1, this.base));

        Icon reset = SharedIcons.resetIcon(8);
        reset.addListener(clickable -> {
            if (!this.pokeball.equals("N/A")) {
                this.base.totalCost = this.base.totalCost - Config.getPrices("Pokeballs", this.pokeball);
                this.addIcon(this.pokeballIcon(this.slot, EnumPokeballs.valueOf(this.pokeball), false));
                this.pokeball = "N/A";
                this.slot = -1;
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.selectedIcon(9));
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        this.addIcon(reset);

        Icon back = SharedIcons.backIcon(44);
        back.addListener(clickable -> {
            this.base.pokeball = this.pokeball;
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                //this.player.closeInventory(Cause.of(NamedCause.source(PixelEdit.getInstance())));
                this.base.addIcon(this.base.pokeballIcon(!this.pokeball.equals("N/A")));
                this.base.addIcon(this.base.confirmIcon());
                this.base.updateContents();
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);

        Icon previousPage = this.pageIcon(19, false);
        previousPage.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> this.updatePage(false)).delayTicks(1L).submit((Object)PixelEdit.getInstance()));
        this.addIcon(previousPage);

        Icon nextPage = this.pageIcon(25, true);
        nextPage.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> this.updatePage(true)).delayTicks(1L).submit((Object)PixelEdit.getInstance()));
        this.addIcon(nextPage);
    }

    private void setupPokeballs() {
        int index = (this.page - 1) * 25;
        List<EnumPokeballs> balls = this.getPokeballs();
        for (int y = 0; y <= 5; ++y) {
            for (int x = 2; x < 7 && index < balls.size() && (this.page != 1 || index != 25); ++x, ++index) {
                boolean selected = false;
                if (this.pokeball.equalsIgnoreCase(balls.get(index).name())) {
                    selected = true;
                    this.slot = x + 9 * y;
                }
                this.addIcon(this.pokeballIcon(x + 9 * y, balls.get(index), selected));
            }
        }
    }

    private Icon selectedIcon(int slot) {
        return new Icon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, this.getItemName(this.pokeball)).orElse(ItemTypes.CONCRETE))
                .add(Keys.DYE_COLOR, DyeColors.WHITE)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lSelected Pok\u00E9Ball")))
                .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(Chat.embedColours("&7Current: &e" + (this.pokeball.equals("N/A") ? this.pokeball : this.pokeball.substring(0, this.pokeball.indexOf("Ball")) + " Ball")))))
                .build());
    }

    private Icon pokeballIcon(int slot, EnumPokeballs pokeball, boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to select this type of Pok\u00E9ball.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        if(this.base.pokemon.getCaughtBall().name().equalsIgnoreCase(pokeball.name())) {
            itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00E9mon is already in this ball.")));
        }else{
            itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + Config.getPrices("Pokeballs", pokeball.name()) + " " +  Config.getItem("currencyName") + (Config.getPrices("Pokeballs", pokeball.name())==1 ? "" : "(s)"))));
        }

        Icon icon = new Icon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, this.getItemName(pokeball.name())).orElse(Sponge.getRegistry().getType(ItemType.class, "pixelmon:poke_ball").orElse(ItemTypes.BARRIER)))
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&6&l" + pokeball.name().substring(0, pokeball.name().indexOf("Ball")) + " Ball")))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 1)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        icon.addListener(clickable -> {
            if (!this.pokeball.equals("N/A")) {
                this.addIcon(this.pokeballIcon(this.slot, EnumPokeballs.valueOf(this.pokeball), false));
            } else {
                this.base.totalCost = this.base.totalCost + Config.getPrices("Pokeballs", pokeball.name());
                this.pokeball = pokeball.name();
                this.slot = slot;

                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.pokeballIcon(this.slot, pokeball, true));
                    this.addIcon(this.selectedIcon(9));
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        return icon;
    }

    private Icon pageIcon(int slot, boolean nextOrLast) {
        return new Icon(slot, ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, nextOrLast ? "pixelmon:trade_holder_right" : "pixelmon:trade_holder_left").get()).quantity(1).add(Keys.DISPLAY_NAME, (nextOrLast ? Text.of(TextColors.GREEN, "\u2192 ", "Next Page", TextColors.GREEN, " \u2192") : Text.of(TextColors.RED, "\u2190 ", "Previous Page", TextColors.RED, " \u2190"))).build());
    }

    private void updatePage(boolean upOrDown) {
        this.page = upOrDown ? (this.page < this.maxPage ? ++this.page : 1) : (this.page > 1 ? --this.page : this.maxPage);
        Icon previousPage = this.pageIcon(19, false);
        previousPage.addListener(clickable -> this.updatePage(false));
        this.addIcon(previousPage);
        Icon nextPage = this.pageIcon(25, true);
        nextPage.addListener(clickable -> this.updatePage(true));
        this.addIcon(nextPage);
        this.clearIcons(new int[]{2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42});
        this.setupPokeballs();
        this.updateContents(0, 44);
    }

    private String getItemName(String name) {
        return name.equals("N/A") ? "minecraft:concrete" : "pixelmon:" + (name.substring(0, name.indexOf("Ball")) + "_ball").toLowerCase();
    }

    private List<EnumPokeballs> getPokeballs() {
        ArrayList<EnumPokeballs> pokeballs = new ArrayList<EnumPokeballs>();
        Collections.addAll(pokeballs, EnumPokeballs.values());
        pokeballs.removeIf(name -> name.name().equalsIgnoreCase("CherishBall"));
        pokeballs.removeIf(name -> name.name().equalsIgnoreCase(this.base.pokemon.getCaughtBall().name()));
        return pokeballs;
    }
}
