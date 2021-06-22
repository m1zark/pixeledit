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
import com.pixelmonmod.pixelmon.client.gui.GuiResources;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.util.text.TextFormatting;
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

import java.util.ArrayList;

public class AbilityGUI extends InventoryManager {
    private Player player;
    private BuilderGUI base;
    private String ability;
    private String[] abilities;

    public AbilityGUI(Player player, BuilderGUI base) {
        super(player, 3, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Ability")));
        this.player = player;
        this.base = base;
        this.ability = base.ability;
        this.abilities = base.pokemon.getBaseStats().getAbilitiesArray();

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

            this.addIcon(SharedIcons.BorderIcon(x + (9 * y),  DyeColors.GRAY, ""));
        }

        this.addIcon(this.selectedIcon());
        this.addIcon(SharedIcons.infoIcon(18, "Ability Info", Config.getInfo("abilityInfo"), 1, this.base));

        this.addIcon(this.abilityIcon(11, this.ability.equalsIgnoreCase(this.abilities[0])));
        if (this.abilities[1] != null) {
            if (this.base.pokemon.getSpecies().equals(EnumSpecies.Zygarde)) {
                this.addIcon(this.zygardeIcon(13, this.ability.equalsIgnoreCase("Power Construct")));
            } else {
                this.addIcon(this.abilityIcon(13, this.ability.equalsIgnoreCase(this.abilities[1])));
            }
        } else {
            if(this.base.pokemon.getSpecies().equals(EnumSpecies.Greninja) && this.base.pokemon.getForm() != 1) {
                this.addIcon(this.greninjaIcon(13, this.ability.equalsIgnoreCase("Battle Bond")));
            } else {
                this.addIcon(this.invalidIcon(13, 1));
            }
        }
        if (this.abilities[2] != null) {
            this.addIcon(this.abilityIcon(15, this.ability.equalsIgnoreCase(this.abilities[2])));
        } else {
            this.addIcon(this.invalidIcon(15,2));
        }

        Icon reset = SharedIcons.resetIcon(8);
        reset.addListener(clickable -> {
            if (!this.ability.equalsIgnoreCase("N/A")) {
                this.base.totalCost = this.ability.equalsIgnoreCase("Battle Bond") ? (this.base.totalCost -= Config.getPrices("Abilities", "greninja")) : (this.ability.equalsIgnoreCase("Power Construct") ? (this.base.totalCost -= Config.getPrices("Abilities", "zygarde")) : (this.abilities[0].equalsIgnoreCase(this.ability) || this.abilities[1] != null && this.abilities[1].equalsIgnoreCase(this.ability) ? (this.base.totalCost -= Config.getPrices("Abilities", "normal")) : (this.base.totalCost -= Config.getPrices("Abilities", "hidden"))));
                this.ability = "N/A";
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.selectedIcon());
                    this.addIcon(this.abilityIcon(11, false));
                    if (this.abilities[1] != null) {
                        if (this.base.pokemon.getSpecies().equals(EnumSpecies.Zygarde)) {
                            this.addIcon(this.zygardeIcon(13, false));
                        } else {
                            this.addIcon(this.abilityIcon(13, false));
                        }
                    } else if (this.base.pokemon.getSpecies().equals(EnumSpecies.Greninja)) {
                        this.addIcon(this.greninjaIcon(13, false));
                    } else {
                        this.addIcon(this.invalidIcon(13, 1));
                    }
                    if (this.abilities[2] != null) {
                        this.addIcon(this.abilityIcon(15, false));
                    } else {
                        this.addIcon(this.invalidIcon(15, 2));
                    }
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        this.addIcon(reset);

        Icon back = SharedIcons.backIcon(26);
        back.addListener(clickable -> {
            this.base.ability = this.ability;
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.base.addIcon(this.base.abilityIcon(!this.ability.equalsIgnoreCase("N/A")));
                this.base.addIcon(this.base.confirmIcon());
                this.base.updateContents();
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);
    }

    private Icon abilityIcon(int slot, boolean selected) {
        int cost;
        int n = cost = slot == 11 || slot == 13 ? Config.getPrices("Abilities", "normal") : Config.getPrices("Abilities", "hidden");
        String ability = slot == 11 ? this.abilities[0] : (slot == 13 ? this.abilities[1] : this.abilities[2]);
        boolean ha = false;
        if (this.abilities[2] != null && this.abilities[2].equals(ability)) {
            ha = true;
        }
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of(Chat.embedColours(("&b" + PokeUtils.format(TextFormatting.AQUA, "ability." + ability + ".description", new Object[0]).getFormattedText()))));
        itemLore.add(Text.of(Chat.embedColours("&7Click here to select this ability.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        if (this.hasAbility(slot)) {
            itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00e9mon already has this ability.")));
        } else {
            itemLore.add(Text.of(Chat.embedColours(("&7Cost: &a" + cost + " " + Config.getItem("currencyName") + (cost == 1 ? "" : "(s)")))));
        }
        Icon icon = new Icon(slot, ItemStack.builder().itemType(slot == 11 || slot == 13 ? ItemTypes.POTION : ItemTypes.DRAGON_BREATH).add(Keys.HIDE_MISCELLANEOUS, true).add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours(("&a&l" + PokeUtils.format(TextFormatting.GREEN, "ability." + ability + ".name", new Object[0]).getFormattedText() + (ha ? " &7(&cHA&7)" : ""))))).add(Keys.ITEM_LORE, itemLore).build());
        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 1)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }
        icon.addListener(clickable -> {
            if (this.ability.equalsIgnoreCase("N/A") && !this.hasAbility(slot)) {
                if (slot == 11) {
                    this.ability = this.abilities[0];
                    if (this.base.pokemon.getSpecies().equals(EnumSpecies.Greninja)) {
                        this.addIcon(this.greninjaIcon(13, false));
                    } else if (this.base.pokemon.getSpecies().equals(EnumSpecies.Zygarde)) {
                        this.addIcon(this.zygardeIcon(13, false));
                    } else {
                        this.addIcon(this.abilities[1] != null ? this.abilityIcon(13, false) : this.invalidIcon(13, 1));
                    }
                    this.addIcon(this.abilities[2] != null ? this.abilityIcon(15, false) : this.invalidIcon(15, 2));
                } else if (slot == 13) {
                    this.ability = this.abilities[1];
                    this.addIcon(this.abilityIcon(11, false));
                    this.addIcon(this.abilities[2] != null ? this.abilityIcon(15, false) : this.invalidIcon(15, 2));
                } else {
                    this.ability = this.abilities[2];
                    this.addIcon(this.abilityIcon(11, false));
                    if (this.base.pokemon.getSpecies().equals(EnumSpecies.Greninja)) {
                        this.addIcon(this.greninjaIcon(13, false));
                    } else if (this.base.pokemon.getSpecies().equals(EnumSpecies.Zygarde)) {
                        this.addIcon(this.zygardeIcon(13, false));
                    } else {
                        this.addIcon(this.abilities[1] != null ? this.abilityIcon(13, false) : this.invalidIcon(13, 1));
                    }
                }
                this.base.totalCost += cost;
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.selectedIcon());
                    this.addIcon(this.abilityIcon(slot, true));
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        return icon;
    }

    private Icon greninjaIcon(int slot, boolean selected) {
        int cost = Config.getPrices("Abilities", "greninja");
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of(Chat.embedColours("&bSelecting this will give your Greninja the Battle Bond ability, allowing it to turn into Ash-Greninja.")));
        itemLore.add(Text.of(Chat.embedColours("&7Click here to select this ability.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&cThis is a permanent change... there is no going back.")));
        if (this.base.pokemon.getForm() == 1) {
            itemLore.add(Text.of(Chat.embedColours("&cYour Greninja already has this ability.")));
        } else {
            itemLore.add(Text.of(Chat.embedColours(("&7Cost: &a" + cost + " " + Config.getItem("currencyName") + (cost == 1 ? "" : "(s)")))));
        }
        ItemStack item = ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:pixelmon_sprite").orElse(ItemTypes.STAINED_HARDENED_CLAY)).add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&a&lBattle Bond"))).add(Keys.ITEM_LORE, itemLore).build();
        Icon icon = new Icon(slot, ItemStack.builder().fromContainer(item.toContainer().set(DataQuery.of("UnsafeData", "SpriteName"), this.getSprite())).build());
        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 1)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }
        icon.addListener(clickable -> {
            if (this.ability.equalsIgnoreCase("N/A") || !this.ability.equalsIgnoreCase("Battle Bond") && this.base.pokemon.getForm() != 1) {
                this.addIcon(this.abilityIcon(11, false));
                this.addIcon(this.abilities[2] != null ? this.abilityIcon(15, false) : this.invalidIcon(15, 2));
                this.ability = "Battle Bond";
                this.base.totalCost += cost;
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.selectedIcon());
                    this.addIcon(this.greninjaIcon(slot, true));
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        return icon;
    }

    private Icon zygardeIcon(int slot, boolean selected) {
        int cost = Config.getPrices("Abilities", "zygarde");
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of(Chat.embedColours("&bSelecting this will give your Zygarde the Power Construct ability.")));
        itemLore.add(Text.of(Chat.embedColours("&7Click here to select this ability.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&cThis is a permanent change... there is no going back.")));
        if (this.base.pokemon.getForm() == 2) {
            itemLore.add(Text.of(Chat.embedColours("&cYour Zygarde already has this ability.")));
        } else {
            itemLore.add(Text.of(Chat.embedColours(("&7Cost: &a" + cost + " " + Config.getItem("currencyName") + (cost == 1 ? "" : "(s)")))));
        }
        ItemStack item = ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:pixelmon_sprite").orElse(ItemTypes.STAINED_HARDENED_CLAY)).add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&a&lPower Construct"))).add(Keys.ITEM_LORE, itemLore).build();
        Icon icon = new Icon(slot, ItemStack.builder().fromContainer(item.toContainer().set(DataQuery.of("UnsafeData", "SpriteName"), this.getSprite())).build());
        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 1)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }
        icon.addListener(clickable -> {
            if (this.ability.equalsIgnoreCase("N/A") || !this.ability.equalsIgnoreCase("Power Construct") && this.base.pokemon.getForm() <= 1) {
                this.addIcon(this.abilityIcon(11, false));
                this.addIcon(this.abilities[2] != null ? this.abilityIcon(15, false) : this.invalidIcon(15, 2));
                this.ability = "Power Construct";
                this.base.totalCost += cost;
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.selectedIcon());
                    this.addIcon(this.zygardeIcon(slot, true));
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });
        return icon;
    }

    private Icon invalidIcon(int slot, int ability) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7This Pok\u00E9mon doesn't have a " + (ability == 1 ? "second regular" : "hidden") + " ability.")));

        return new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&4&lNot Available")))
                .add(Keys.ITEM_LORE, itemLore)
                .build());
    }

    private Icon selectedIcon() {
        ItemType type = this.abilities[0].equalsIgnoreCase(this.ability) ? ItemTypes.POTION : (this.abilities[1] != null && this.abilities[1].equalsIgnoreCase(this.ability) ? ItemTypes.POTION : (this.abilities[2] != null && this.abilities[2].equalsIgnoreCase(this.ability) ? ItemTypes.DRAGON_BREATH : ItemTypes.CONCRETE));

        if(this.ability.equalsIgnoreCase("Battle Bond")) {
            type = Sponge.getRegistry().getType(ItemType.class, "pixelmon:pixelmon_sprite").orElse(ItemTypes.STAINED_HARDENED_CLAY);
        }

        ItemStack item = ItemStack.builder()
                .itemType(type)
                .add(Keys.HIDE_MISCELLANEOUS, true)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lSelected Ability")))
                .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(Chat.embedColours("&7Current: &e" + this.ability))))
                .build();

        if(this.ability.equalsIgnoreCase("Battle Bond"))
            return new Icon(0, ItemStack.builder().fromContainer(item.toContainer().set(DataQuery.of("UnsafeData","SpriteName"), this.getSprite())).build());
        else
            return new Icon(0, item);
    }

    private boolean hasAbility(int slot) {
        if(slot == 11) {
            return this.base.pokemon.getAbility().getName().equalsIgnoreCase(this.abilities[0]);
        } else if(slot == 13) {
            return this.base.pokemon.getAbility().getName().equalsIgnoreCase(this.abilities[1]);
        } else {
            return this.base.pokemon.getAbility().getName().equalsIgnoreCase(this.abilities[2]);
        }
    }

    private String getSprite() {
        return "pixelmon:" + GuiResources.getSpritePath(this.base.pokemon.getSpecies(), this.base.pokemon.getForm(), this.base.pokemon.getGender(), this.base.pokemon.getCustomTexture(), (this.base.pokemon.isShiny() || this.base.shiny == 1 ? 1 : 0) != 0);
    }
}
