package com.m1zark.pixeledit.pokebuilder.Stats;

import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.pixeledit.pokebuilder.BuilderGUI;
import com.m1zark.pixeledit.pokebuilder.SharedIcons;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumNature;
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

public class NatureGUI extends InventoryManager {
    private Player player;
    private BuilderGUI base;
    private String nature;
    private DyeColor color;
    private int slot = -1;

    public NatureGUI(Player player, BuilderGUI base) {
        super(player, 6, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Natures")));
        this.color = DyeColors.WHITE;
        this.player = player;
        this.base = base;
        this.nature = base.nature;

        setupDesign();
    }

    private void setupDesign() {
        int x = 0;
        int index = 5;
        for(int y = 0; y < 6; ++x) {
            if (x > 8) {
                x = 0;
                ++y;
            }
            if (y >= 6) { break; }

            this.addIcon(SharedIcons.BorderIcon(x + (9 * y),  DyeColors.GRAY, ""));
        }

        this.addIcon(this.selectedIcon(0));
        this.addIcon(SharedIcons.infoIcon(45, "Nature Info", Config.getInfo("natureInfo"), -1, this.base));

        for(x = 2; x < 7 && index < EnumNature.class.getEnumConstants().length;){
            for(int y = 1; y <= 4; y++) {
                boolean selected = false;
                if (this.nature.equals(EnumNature.getNatureFromIndex(index).toString())) {
                    selected = true;
                    this.slot = x + (9 * y);
                    this.color = this.getColor(EnumNature.getNatureFromIndex(index));
                    this.addIcon(this.selectedIcon(0));
                }

                this.addIcon(this.natureIcon(x + (9 * y), EnumNature.getNatureFromIndex(index), selected));
                index++;
            }
            x++;
        }

        Icon reset = SharedIcons.resetIcon(8);
        reset.addListener(clickable -> {
            if(!this.nature.equalsIgnoreCase("N/A")) {
                this.base.totalCost = this.base.totalCost - Config.getPrices("Natures", this.nature);
                this.addIcon(this.natureIcon(this.slot, EnumNature.natureFromString(this.nature), false));
                this.nature = "N/A";
                this.slot = -1;
                this.color = DyeColors.WHITE;

                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.selectedIcon(0));
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        this.addIcon(reset);

        Icon back = SharedIcons.backIcon(53);
        back.addListener(clickable -> {
            this.base.nature = this.nature;
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                //this.player.closeInventory();
                this.base.addIcon(this.base.natureIcon(!this.nature.equalsIgnoreCase("N/A")));
                this.base.addIcon(this.base.confirmIcon());
                this.base.updateContents();
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);
    }

    private Icon natureIcon(int slot, EnumNature nature, boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Increased: &a" + nature.increasedStat.name())));
        itemLore.add(Text.of(Chat.embedColours("&7Lowered: &c" + nature.decreasedStat.name())));
        itemLore.add(Text.of(Chat.embedColours("")));
        if(this.base.pokemon.getNature().equals(nature)) {
            itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00E9mon already has this nature.")));
        }else{
            itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + Config.getPrices("Natures", nature.name()) + " " + Config.getItem("currencyName") + (Config.getPrices("Natures", nature.name())==1 ? "" : "(s)"))));
        }

        Icon icon = new Icon(slot, ItemStack.builder().
                itemType(Sponge.getRegistry().getType(ItemType.class, this.getItem(nature)).orElse(ItemTypes.CONCRETE))
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&6&l" + nature.name())))
                .add(Keys.ITEM_LORE, itemLore)
                .add(Keys.DYE_COLOR, this.getColor(nature))
                .build());

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        icon.addListener(clickable -> {
            if(!this.base.pokemon.getNature().equals(nature)) {
                if (!this.nature.equalsIgnoreCase("N/A")) {
                    this.addIcon(this.natureIcon(this.slot, EnumNature.natureFromString(this.nature), false));
                } else {
                    this.base.totalCost = this.base.totalCost + Config.getPrices("Natures", nature.name());
                    this.nature = nature.name();
                    this.slot = slot;
                    this.color = this.getColor(nature);

                    Sponge.getScheduler().createTaskBuilder().execute(() -> {
                        this.addIcon(this.natureIcon(this.slot, nature, true));
                        this.addIcon(this.selectedIcon(0));
                        this.updateContents();
                    }).delayTicks(1L).submit(PixelEdit.getInstance());
                }
            }
        });

        return icon;
    }

    private Icon selectedIcon(int slot) {
        return new Icon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, this.getItem(EnumNature.natureFromString(this.nature))).orElse(ItemTypes.CONCRETE))
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lSelected Nature")))
                .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(Chat.embedColours("&7Current: &e" + this.nature))))
                .add(Keys.DYE_COLOR, this.color)
                .build());
    }

    private String getItem(EnumNature nature) {
        if(nature == null) { return "minecraft:concrete"; }

        return nature.increasedStat.equals(StatsType.HP) ? "" :
        (nature.increasedStat.equals(StatsType.Attack) ? "pixelmon:fire_gem" :
        (nature.increasedStat.equals(StatsType.Defence) ? "pixelmon:electric_gem" :
        (nature.increasedStat.equals(StatsType.SpecialAttack) ? "pixelmon:fairy_gem" :
        (nature.increasedStat.equals(StatsType.SpecialDefence) ? "pixelmon:water_gem" :
        (nature.increasedStat.equals(StatsType.Speed) ? "pixelmon:grass_gem" :
        "minecraft:concrete")))));
    }

    private DyeColor getColor(EnumNature nature) {
        return nature.increasedStat.equals(StatsType.HP) ? DyeColors.GREEN :
        (nature.increasedStat.equals(StatsType.Attack) ? DyeColors.ORANGE :
        (nature.increasedStat.equals(StatsType.Defence) ? DyeColors.YELLOW :
        (nature.increasedStat.equals(StatsType.SpecialAttack) ? DyeColors.LIGHT_BLUE :
        (nature.increasedStat.equals(StatsType.SpecialDefence) ? DyeColors.LIME :
        (nature.increasedStat.equals(StatsType.Speed) ? DyeColors.PINK :
        DyeColors.WHITE)))));
    }
}
