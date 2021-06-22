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
import com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.basic.HiddenPower;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.EnumType;
import org.apache.commons.lang3.StringUtils;
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
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IVsEVsGUI extends InventoryManager {
    private Player player;
    private BuilderGUI base;
    private int hiddenPowerIndex = 0;
    private List<String> HPTypes = Arrays.asList("Bug","Dark","Dragon","Electric","Fighting","Fire","Flying","Ghost","Grass","Ground","Ice","Poison","Psychic","Rock","Steel","Water");
    private boolean evs;
    private int[] ivs;
    private boolean selected;
    private int dittoIncrease;

    public IVsEVsGUI(Player player, BuilderGUI base) {
        super(player, 5, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8IVs/EVs")));
        this.player = player;
        this.base = base;
        this.evs = base.evReset;
        this.ivs = base.ivs;
        this.dittoIncrease = this.base.pokemon.getSpecies().equals(EnumSpecies.Ditto) ? Config.getPrices("IVs", "dittoIncrease") : 0;

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

            this.addIcon(SharedIcons.BorderIcon(x + 9 * y,  DyeColors.GRAY, ""));
        }

        this.addIcon(this.selectedIcon(0));
        this.addIcon(SharedIcons.infoIcon(36, "IVs/EVs Info", Config.getInfo("ivsevsInfo"), 0, this.base));


        this.addModifiers();


        this.addIcon(this.evsIcon(32));
        this.addIcon(this.hiddenPowerIcon(14, this.hiddenPowerIndex));

        Icon reset = SharedIcons.resetIcon(8);
        reset.addListener(clickable -> {
            if(this.evs) {
                this.base.totalCost = this.base.totalCost - Config.getPrices("EVs", "reset");
                this.evs = false;
            }

            for(int i = 0; i < this.ivs.length; i++) {
                int iv = this.ivs[i];

                if(iv != -1) {
                    this.base.totalCost -= Config.getPrices("IVs", iv + "ivs") + this.dittoIncrease;
                    this.ivs[i] = -1;
                }
            }

            this.base.ivsSelected = false;

            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.addIcon(this.selectedIcon(0));
                this.addIcon(this.evsIcon(32));
                this.addModifiers();
                this.updateContents();
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(reset);

        Icon back = SharedIcons.backIcon(44);
        back.addListener(clickable -> {
            this.base.evReset = this.evs;
            this.base.ivs = this.ivs;

            for(int iv: this.ivs) if(iv != -1) this.selected = true;

            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.base.addIcon(this.base.statsIcon((this.selected || this.evs)));
                this.base.addIcon(this.base.confirmIcon());
                this.base.updateContents();
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);
    }

    private Icon selectedIcon(int slot) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Current:")));
        itemLore.add(Text.of(Chat.embedColours("")));
        if(this.evs){
            itemLore.add(Text.of(Chat.embedColours("&eReset EVs")));
            itemLore.add(Text.of(Chat.embedColours("")));
        }
        for(int i = 0; i < this.ivs.length; i++) {
            if(this.ivs[i] != -1) {
                String stat = i==0 ? "HP" : (i==1 ? "Attack" : (i==2 ? "Defense" : (i==3 ? "Special Attack" : (i==4 ? "Special Defense" : (i==5 ? "Speed" : "")))));

                itemLore.add(Text.of(Chat.embedColours("&e" + stat + ": " + this.ivs[i])));
            }
        }

        return new Icon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:destiny_knot").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lSelected IVs/EVs")))
                .add(Keys.ITEM_LORE, itemLore)
                .build());
    }

    private Icon incrementIcon(StatsType st) {
        Icon icon = new Icon(this.getSlot(st), ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, this.getItem(st)).orElse(ItemTypes.STAINED_HARDENED_CLAY))
                .add(Keys.DYE_COLOR, this.getColor(st))
                .add(Keys.DISPLAY_NAME, Text.of(this.getTextColor(st), TextStyles.BOLD, this.updateName(st.name()) + " IVs"))
                .add(Keys.ITEM_LORE, this.getLore(st)).build());

        if (this.ivs[this.getIndex(st)] != -1) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        icon.addListener(clickable -> {
            switch(this.ivs[this.getIndex(st)]){
                case 31:
                    this.ivs[this.getIndex(st)] = 30;
                    this.base.ivsSelected = true;
                    this.base.totalCost -= Config.getPrices("IVs", "31ivs") + this.dittoIncrease;
                    this.base.totalCost += Config.getPrices("IVs", "30ivs") + this.dittoIncrease;
                    break;
                case 30:
                    this.ivs[this.getIndex(st)] = 0;
                    this.base.ivsSelected = true;
                    this.base.totalCost -= Config.getPrices("IVs", "30ivs") + this.dittoIncrease;
                    this.base.totalCost += Config.getPrices("IVs", "0ivs") + this.dittoIncrease;
                    break;
                case 0:
                    this.ivs[this.getIndex(st)] = -1;
                    this.base.ivsSelected = false;
                    this.base.totalCost -= Config.getPrices("IVs", "0ivs") + this.dittoIncrease;
                    break;
                case -1:
                    this.ivs[this.getIndex(st)] = 31;
                    this.base.ivsSelected = true;
                    this.base.totalCost += Config.getPrices("IVs", "31ivs") + this.dittoIncrease;
                    break;
            }

            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.addModifiers();
                this.addIcon(this.selectedIcon(0));
                this.updateContents();
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        return icon;
    }

    private Icon evsIcon(int slot) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to reset your Pok\u00E9mon's EVs to 0.")));
        itemLore.add(Text.of(Chat.embedColours("")));

        EVStore eVsStore;
        int evSum = 0;
        if (this.base.pokemon.getStats() != null) {
            eVsStore = this.base.pokemon.getEVs();
            evSum = eVsStore.hp + eVsStore.attack + eVsStore.defence + eVsStore.specialAttack + eVsStore.specialDefence + eVsStore.speed;
        }

        if (evSum == 0) {
            itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00E9mon already has 0 EVs.")));
        } else {
            itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + Config.getPrices("EVs", "reset") + " " + Config.getItem("currencyName") + (Config.getPrices("EVs", "reset") == 1 ? "" : "(s)"))));
        }

        Icon icon = new Icon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:heal_powder").orElse(ItemTypes.STAINED_HARDENED_CLAY))
                .add(Keys.DYE_COLOR, DyeColors.MAGENTA)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&a&lReset EVs")))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        if (this.evs) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        if (evSum != 0 && !selected) {
            icon.addListener(clickable -> {
                this.evs = true;
                this.base.totalCost = this.base.totalCost + Config.getPrices("EVs", "reset");

                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.addIcon(this.evsIcon(slot));
                    this.addIcon(this.selectedIcon(0));
                    this.updateContents();
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            });
        }

        return icon;
    }

    private Icon hiddenPowerIcon(int slot, int index) {
        String typeName = this.HPTypes.get(index);
        IVStore ivs = HiddenPower.getOptimalIVs(EnumType.parseType(typeName));
        int[] iv = Arrays.stream(Arrays.toString(ivs.getArray()).substring(1, Arrays.toString(ivs.getArray()).length()-1).split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();

        String info = "Click here to cycle through the different Hidden Power types to view the IV stats needed for that type and max power.";
        String[] newInfo = PokeUtils.insertLinebreaks(info,30).split("\n");

        ArrayList<Text> itemLore = new ArrayList<>();
        for(String s:newInfo) itemLore.add(Text.of(Chat.embedColours("&7" + s)));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Type: &e" + typeName)));

        String color = "";
        List<String> stat = Arrays.asList("HP","Attack","Defence","SpecialAttack","SpecialDefence","Speed");
        for(int i = 0; i < iv.length; i++) {
            if(i==0) color="&c";
            if(i==1) color="&6";
            if(i==2) color="&e";
            if(i==3) color="&9";
            if(i==4) color="&a";
            if(i==5) color="&d";

            itemLore.add(Text.of(Chat.embedColours(color + stat.get(i) + "&7: "+ color + iv[i])));
        }

        Icon icon = new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.BOOK)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lHidden Power Types")))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        icon.addListener(clickable -> {
            this.hiddenPowerIndex = (this.hiddenPowerIndex == 15 ? 0 : this.hiddenPowerIndex + 1);
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.addIcon(this.hiddenPowerIcon(slot, this.hiddenPowerIndex));
                this.updateContents();
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });

        return icon;
    }




    private void addModifiers() {
        for(int ordinal = 1; ordinal < 7; ++ordinal) {
            this.addIcon(this.incrementIcon(StatsType.values()[ordinal]));
        }
    }

    private List<Text> getLore(StatsType st) {
        DecimalFormat df = new DecimalFormat("#0.##");

        IVStore ivStore;
        int ivSum = 0;
        if (this.base.pokemon.getStats() != null) {
            ivStore = this.base.pokemon.getIVs();
            ivSum = ivStore.hp + ivStore.attack + ivStore.defence + ivStore.specialAttack + ivStore.specialDefence + ivStore.speed;
        }

        String color = st.equals(StatsType.HP) ? "&a&l" : (st.equals(StatsType.Attack) ? "&c&l" : (st.equals(StatsType.Defence) ? "&6&l" : (st.equals(StatsType.SpecialAttack) ? "&d&l" : (st.equals(StatsType.SpecialDefence) ? "&e&l" : (st.equals(StatsType.Speed) ? "&b&l" : "")))));
        String info = "&7Click here to edit your Pok\u00E9mon's " + color + this.updateName(st.name()) + " &7stat.&r";
        String noteIVs = "\n&a&lAvailable Options:\n&b31 IVs &7- &a"
                + (Config.getPrices("IVs", "31ivs") + this.dittoIncrease) + " " + Config.getItem("currencyName") + (Config.getPrices("IVs", "31ivs") + this.dittoIncrease == 1 ? "" : "(s)")
                + "\n&b30 IVs &7- &a" + (Config.getPrices("IVs", "30ivs") + this.dittoIncrease) + " " + Config.getItem("currencyName") + (Config.getPrices("IVs", "30ivs") + this.dittoIncrease == 1 ? "" : "(s)")
                + "\n&b0 IVS &7- &a" + (Config.getPrices("IVs", "0ivs") + this.dittoIncrease) + " " + Config.getItem("currencyName") + (Config.getPrices("IVs", "0ivs") + this.dittoIncrease == 1 ? "" : "(s)")
                + "\n\n&7Right click the button to select a\n&7different option.";

        ArrayList<Text> itemLore = new ArrayList<>();
        for(String s : PokeUtils.insertLinebreaks(info,30).split("\n")) itemLore.add(Text.of(Chat.embedColours(s)));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Current: &e" + this.base.pokemon.getIVs().get(st) + "&7/&e31")));
        itemLore.add(Text.of(Chat.embedColours("&7Current Total: &e" + ivSum + "&7/&e186 &7(&a" + df.format((double)ivSum / 186.0D * 100.0D) + "&7%)")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Updated: " + this.updatedStat(st))));
        itemLore.add(Text.of(Chat.embedColours("&7Updated Total: &e" + this.updatedTotal() + "&7/&e186 &7(&a" + df.format((double)this.updatedTotal() / 186.0D * 100.0D) + "&7%)")));
        for(String s : noteIVs.split("\n")) itemLore.add(Text.of(Chat.embedColours(s)));

        return itemLore;
    }

    private int getIndex(StatsType st) {
        if (st.equals(StatsType.HP)) { return 0; }
        else if (st.equals(StatsType.Attack)) { return 1; }
        else if (st.equals(StatsType.Defence)) { return 2; }
        else if (st.equals(StatsType.SpecialAttack)) { return 3; }
        else if (st.equals(StatsType.SpecialDefence)) { return 4; }
        else { return st.equals(StatsType.Speed) ? 5 : -1; }
    }

    private String getItem(StatsType st) {
        return st.equals(StatsType.HP) ? "pixelmon:power_weight"
                : (st.equals(StatsType.Attack) ? "pixelmon:power_bracer"
                : (st.equals(StatsType.Defence) ? "pixelmon:power_belt"
                : (st.equals(StatsType.SpecialAttack) ? "pixelmon:power_lens"
                : (st.equals(StatsType.SpecialDefence) ? "pixelmon:power_band"
                : (st.equals(StatsType.Speed) ? "pixelmon:power_anklet"
                : "")))));
    }

    private DyeColor getColor(StatsType st) {
        return st.equals(StatsType.HP) ? DyeColors.LIME
                : (st.equals(StatsType.Attack) ? DyeColors.RED
                : (st.equals(StatsType.Defence) ? DyeColors.ORANGE
                : (st.equals(StatsType.SpecialAttack) ? DyeColors.PURPLE
                : (st.equals(StatsType.SpecialDefence) ? DyeColors.YELLOW
                : (st.equals(StatsType.Speed) ? DyeColors.LIGHT_BLUE
                : DyeColors.WHITE)))));
    }

    private TextColor getTextColor(StatsType st) {
        return st.equals(StatsType.HP) ? TextColors.GREEN
                : (st.equals(StatsType.Attack) ? TextColors.RED
                : (st.equals(StatsType.Defence) ? TextColors.GOLD
                : (st.equals(StatsType.SpecialAttack) ? TextColors.LIGHT_PURPLE
                : (st.equals(StatsType.SpecialDefence) ? TextColors.YELLOW
                : (st.equals(StatsType.Speed) ? TextColors.AQUA
                : TextColors.WHITE)))));
    }

    private int getSlot(StatsType st) {
        if (st.equals(StatsType.HP)) { return 11; }
        else if (st.equals(StatsType.Attack)) { return 12; }
        else if (st.equals(StatsType.Defence)) { return 20; }
        else if (st.equals(StatsType.SpecialAttack)) { return 21; }
        else if (st.equals(StatsType.SpecialDefence)) { return 29; }
        else if (st.equals(StatsType.Speed)) { return 30; }
        else { return -1; }
    }

    private String updatedStat(StatsType st) {
        String updater = "&eN/A";
        int stat = this.ivs[this.getIndex(st)];

        if(stat != -1) { updater = "&e" + stat + "&7/&e31"; }

        return updater;
    }

    private String updateName(String name) {
        String[] r = name.split("(?=[A-Z])");
        //String[] r = name.split("(?=\\p{Lu})");
        return name.length() == 2 ? name : StringUtils.join(r," ");
    }

    private int updatedTotal() {
        int hp = this.ivs[0] != -1 ? this.ivs[0] : this.base.pokemon.getIVs().hp;
        int att = this.ivs[1] != -1 ? this.ivs[1] : this.base.pokemon.getIVs().attack;
        int def = this.ivs[2] != -1 ? this.ivs[2] : this.base.pokemon.getIVs().defence;
        int spatt = this.ivs[3] != -1 ? this.ivs[3] : this.base.pokemon.getIVs().specialAttack;
        int spdef = this.ivs[4] != -1 ? this.ivs[4] : this.base.pokemon.getIVs().specialDefence;
        int speed = this.ivs[5] != -1 ? this.ivs[5] : this.base.pokemon.getIVs().speed;

        return hp + att + def + spatt + spdef + speed;
    }
}
