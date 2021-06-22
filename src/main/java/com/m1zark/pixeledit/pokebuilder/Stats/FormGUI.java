package com.m1zark.pixeledit.pokebuilder.Stats;

import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.pixeledit.pokebuilder.BuilderGUI;
import com.m1zark.pixeledit.pokebuilder.SharedIcons;
import com.m1zark.pixeledit.util.PokeUtils;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.client.gui.GuiResources;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class FormGUI extends InventoryManager {
    private Player player;
    private BuilderGUI base;
    private int form;
    private int slot = -1;
    private int page;
    private int maxPage;

    public FormGUI(Player player, BuilderGUI base, int page) {
        super(player, 5, Text.of((Object[])new Object[]{Chat.embedColours((String)"&4&lPok\u00e9Builder &l&0\u27a5&r &8Forms")}));
        this.player = player;
        this.base = base;
        this.form = base.form;
        this.page = page;
        int size = PokeUtils.getAllForms(this.base.pokemon).size();
        this.maxPage = size % 25 == 0 && size / 25 != 0 ? size / 25 : size / 25 + 1;
        this.setupDesign();
        this.setupForms();
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
        Icon reset = SharedIcons.resetIcon(8);
        reset.addListener(clickable -> {
            if (this.form != -1) {
                this.base.totalCost -= Config.getPrices("Form", "change");
                this.addIcon(this.formIcon(this.slot, this.form, false));
                this.form = -1;
                this.slot = -1;
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.selectedIcon(0));
                    this.setupForms();
                    this.updateContents(0, 44);
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        this.addIcon(reset);
        this.addIcon(SharedIcons.infoIcon(36, "Form Change Info", Config.getInfo("formInfo"), -1, this.base));
        Icon back = SharedIcons.backIcon(44);
        back.addListener(clickable -> {
            this.base.form = this.form;
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.base.addIcon(this.base.formIcon(this.form != -1));
                this.base.addIcon(this.base.confirmIcon());
                this.base.updateContents();
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);
        if (this.base.pokemon.getSpecies().getPossibleForms(false).size() > 25) {
            Icon previousPage = this.pageIcon(19, false);
            previousPage.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> this.updatePage(false)).delayTicks(1L).submit(PixelEdit.getInstance()));
            this.addIcon(previousPage);
            Icon nextPage = this.pageIcon(25, true);
            nextPage.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> this.updatePage(true)).delayTicks(1L).submit(PixelEdit.getInstance()));
            this.addIcon(nextPage);
        }
    }

    private void setupForms() {
        int index = (this.page - 1) * 25;
        List<IEnumForm> forms = PokeUtils.getAllForms(this.base.pokemon);
        for (int y = 0; y <= 5; ++y) {
            for (int x = 2; x < 7 && index < forms.size() && (this.page != 1 || index != 25); ++x, ++index) {
                boolean selected = false;
                if (this.form == index) {
                    selected = true;
                    this.slot = x + 9 * y;
                }
                this.addIcon(this.formIcon(x + 9 * y, index, selected));
            }
        }
    }

    private Icon formIcon(int slot, int form, boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to select this &6Form &7option.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours(("&7Cost: &a" + Config.getPrices("Form", "change") + " " + Config.getItem("currencyName") + (Config.getPrices("Form", "change") == 1 ? "" : "(s)")))));
        ItemStack item = ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:pixelmon_sprite").orElse(ItemTypes.STAINED_HARDENED_CLAY)).add(Keys.DYE_COLOR, (form != -1 ? DyeColors.YELLOW : DyeColors.BLACK)).add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours(("&a&lForm " + (form + 1) + ": " + PokeUtils.getAllForms(this.base.pokemon).get(form).getLocalizedName())))).add(Keys.ITEM_LORE, itemLore).build();
        Icon icon = new Icon(slot, ItemStack.builder().fromContainer(item.toContainer().set(DataQuery.of("UnsafeData", "SpriteName"), this.getSprite(PokeUtils.getAllForms(this.base.pokemon).get(form).getForm()))).build());
        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }
        icon.addListener(clickable -> {
            if (this.form == -1) {
                this.form = form;
                this.base.totalCost += Config.getPrices("Form", "change");
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.selectedIcon(0));
                    this.addIcon(this.formIcon(slot, form, true));
                    this.updateContents(0, 44);
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        return icon;
    }

    private Icon selectedIcon(int slot) {
        ItemStack item = ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, this.getItemName()).orElse(ItemTypes.CONCRETE)).add(Keys.DYE_COLOR, (this.form != -1 ? DyeColors.LIME : DyeColors.WHITE)).add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lSelected Form"))).add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(Chat.embedColours(("&7Current: &e" + (this.form != -1 ? (this.base.pokemon.getSpecies().getPossibleForms(false).get(this.form)).getLocalizedName() : "")))))).build();
        return new Icon(slot, ItemStack.builder().fromContainer(item.toContainer().set(DataQuery.of("UnsafeData", "SpriteName"), this.getSprite(this.form))).build());
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
        this.clearIcons(2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42);
        this.setupForms();
        this.updateContents(0, 44);
    }

    private String getItemName() {
        return this.form == -1 ? "minecraft:concrete" : "pixelmon:pixelmon_sprite";
    }

    private String getSprite(int form) {
        return "pixelmon:" + GuiResources.getSpritePath(this.base.pokemon.getSpecies(), form, this.base.pokemon.getGender(), this.base.pokemon.getCustomTexture(), (this.base.pokemon.isShiny() || this.base.shiny == 1 ? 1 : 0) != 0);
    }
}
