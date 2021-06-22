package com.m1zark.pixeledit.pokebuilder.Stats;

import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.pixeledit.pokebuilder.BuilderGUI;
import com.m1zark.pixeledit.pokebuilder.SharedIcons;
import com.m1zark.pixeledit.util.PokeUtils;
import com.m1zark.pixeledit.util.configuration.Config;
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
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;

public class LevelGUI extends InventoryManager {
    private Player player;
    private BuilderGUI base;
    private int currLevel;

    public LevelGUI(Player player, BuilderGUI base) {
        super(player, 3, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Level")));
        this.player = player;
        this.base = base;
        this.currLevel = base.level;

        this.setupDesign();
    }

    private void setupDesign() {
        int x = 0;
        for(int y = 0; y < 3; ++x) {
            if (x > 8) {
                x = 0;
                ++y;
            }
            if (y >= 3) { break; }

            this.addIcon(SharedIcons.BorderIcon(x + (9 * y),  DyeColors.GRAY, ""));
        }

        this.addIcon(this.selectedIcon());
        this.addIcon(SharedIcons.infoIcon(18, "Level Info", Config.getInfo("levelInfo"), -1, this.base));

        this.addIcon(this.levelIcon());

        Icon reset = SharedIcons.resetIcon(8);
        reset.addListener(clickable -> {
            if(this.currLevel == 100) {
                this.base.totalCost = this.base.totalCost - Config.getPrices("Levels", "lvl100");
                this.currLevel = -1;
            }
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.addIcon(this.levelIcon());
                this.addIcon(this.selectedIcon());
                this.updateContents();
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(reset);

        Icon back = SharedIcons.backIcon(26);
        back.addListener(clickable -> {
            this.base.level = this.currLevel;
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                //this.player.closeInventory(Cause.of(NamedCause.source(PixelEdit.getInstance())));
                this.base.addIcon(this.base.levelIcon(this.currLevel != -1));
                this.base.addIcon(this.base.confirmIcon());
                this.base.updateContents();
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);
    }

    private Icon levelIcon() {
        String info = "Caution: We are not responsible for you not being able to evolve your Pok\u00E9mon further, or for losing possible level-up moves.";
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to instantly get level 100.")));
        itemLore.add(Text.of(Chat.embedColours("")));

        String[] newInfo = PokeUtils.insertLinebreaks(info,30).split("\n");
        for(String s:newInfo) itemLore.add(Text.of(Chat.embedColours("&c" + s)));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + Config.getPrices("Levels", "lvl100") + " " + Config.getItem("currencyName") + (Config.getPrices("Levels", "lvl100") == 1 ? "" : "(s)"))));

        Icon icon = new Icon(13, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:rare_candy").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "Boost to level 100"))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        if (this.currLevel != -1) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        icon.addListener(clickable -> {
            if(this.currLevel == -1) {
                this.currLevel = 100;
                this.base.totalCost = this.base.totalCost + Config.getPrices("Levels", "lvl100");

                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.levelIcon());
                    this.addIcon(this.selectedIcon());
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        return icon;
    }

    private Icon selectedIcon(){
        return new Icon(0, ItemStack.builder()
                .itemType(this.currLevel != -1 ? Sponge.getRegistry().getType(ItemType.class, "pixelmon:rare_candy").get() : ItemTypes.CONCRETE)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lSelected Level")))
                .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(Chat.embedColours("&7Current: &e" + (this.currLevel != -1 ? "Level 100 Boost" : "N/A")))))
                .build());
    }
}
