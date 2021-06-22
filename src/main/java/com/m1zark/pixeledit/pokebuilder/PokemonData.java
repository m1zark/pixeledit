package com.m1zark.pixeledit.pokebuilder;

import com.google.common.base.Strings;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.util.PokeUtils;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.basic.HiddenPower;
import com.pixelmonmod.pixelmon.client.gui.GuiResources;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.EnumSpecial;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import de.waterdu.aquaauras.auras.AuraStorage;
import de.waterdu.aquaauras.helper.FileHelper;
import de.waterdu.aquaauras.structures.AuraDefinition;
import de.waterdu.aquaauras.structures.EffectDefinition;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PokemonData {
    private Pokemon pokemon;
    private BuilderGUI base;

    private int id;
    private IEnumForm form;
    private String name;
    private String ability;
    private String m1;
    private String m2;
    private String m3;
    private String m4;
    private int lvl;
    private String growth;
    private String gender;
    private boolean shiny;
    private String nature;
    private String mintNature;
    private int happiness;
    private String caughtBall;
    private boolean isEgg;
    private boolean isHiddenAbility;
    private String heldItem = "Nothing";
    private String pokerus;
    private String hiddenPower;
    private List<String> PokemonAuras = new ArrayList<>();
    private int AurasEnabled = 0;

    private int totalIVs;
    private String IVPercent;
    private int[] ivs = new int[]{0, 0, 0, 0, 0, 0};

    private int totalEVs;
    private String EVPercent;
    private int evs[] = new int[]{0, 0, 0, 0, 0, 0};

    public PokemonData(Pokemon pokemon, BuilderGUI base) {
        DecimalFormat df = new DecimalFormat("##0");

        if(base != null) this.base = base;

        if(pokemon != null) {
            this.pokemon = pokemon;
            this.id = pokemon.getSpecies().getNationalPokedexInteger();
            this.form = pokemon.getFormEnum();
            this.name = pokemon.getSpecies().name();
            this.ability = pokemon.getAbility().getName();
            this.pokerus = pokemon.getPokerus() != null ? (pokemon.getPokerus().canInfect() ? "&d[PKRS] " : "&7&m[PKRS] ") : "";
            this.isHiddenAbility = isHiddenAbility(pokemon, this.ability);
            this.heldItem = pokemon.getHeldItem().getDisplayName().equalsIgnoreCase("Air") ? pokemon.getHeldItem().getDisplayName() : "Nothing";
            this.m1 = pokemon.getMoveset().get(0) != null ? pokemon.getMoveset().get(0).getActualMove().getLocalizedName() : "Empty";
            this.m2 = pokemon.getMoveset().get(1) != null ? pokemon.getMoveset().get(1).getActualMove().getLocalizedName() : "Empty";
            this.m3 = pokemon.getMoveset().get(2) != null ? pokemon.getMoveset().get(2).getActualMove().getLocalizedName() : "Empty";
            this.m4 = pokemon.getMoveset().get(3) != null ? pokemon.getMoveset().get(3).getActualMove().getLocalizedName() : "Empty";
            this.isEgg = pokemon.isEgg();
            this.lvl = pokemon.getLevel();
            this.nature = pokemon.getNature().name();
            this.gender = pokemon.getGender().name();
            this.shiny = pokemon.isShiny();
            this.growth = pokemon.getGrowth().name();
            this.happiness = pokemon.getFriendship();
            this.caughtBall = pokemon.getCaughtBall().name();
            this.hiddenPower = HiddenPower.getHiddenPowerType(pokemon.getIVs()).getLocalizedName();

            this.ivs[0] = pokemon.getIVs().getStat(StatsType.HP);
            this.ivs[1] = pokemon.getIVs().getStat(StatsType.Attack);
            this.ivs[2] = pokemon.getIVs().getStat(StatsType.Defence);
            this.ivs[3] = pokemon.getIVs().getStat(StatsType.SpecialAttack);
            this.ivs[4] = pokemon.getIVs().getStat(StatsType.SpecialDefence);
            this.ivs[5] = pokemon.getIVs().getStat(StatsType.Speed);
            this.totalIVs = this.ivs[0] + this.ivs[1] + this.ivs[2] + this.ivs[3] + this.ivs[4] + this.ivs[5];
            this.IVPercent = df.format((double) totalIVs / 186.0D * 100.0D) + "%";

            this.evs[0] = pokemon.getEVs().getStat(StatsType.HP);
            this.evs[1] = pokemon.getEVs().getStat(StatsType.Attack);
            this.evs[2] = pokemon.getEVs().getStat(StatsType.Defence);
            this.evs[3] = pokemon.getEVs().getStat(StatsType.SpecialAttack);
            this.evs[4] = pokemon.getEVs().getStat(StatsType.SpecialDefence);
            this.evs[5] = pokemon.getEVs().getStat(StatsType.Speed);
            this.totalEVs = this.evs[0] + this.evs[1] + this.evs[2] + this.evs[3] + this.evs[4] + this.evs[5];
            this.EVPercent = df.format((double) totalEVs / 510.0D * 100.0D) + "%";

            AuraStorage auras = new AuraStorage(pokemon.getPersistentData());
            if(auras.hasAuras()) {
                auras.getAuras().forEach(aura -> {
                    if(aura.isEnabled()) this.PokemonAuras.add(aura.getAuraDefinition().getDisplayName() + " " + aura.getEffectDefinition().getDisplayName());
                });
            }
            this.AurasEnabled = auras.aurasEnabled();
        }else{
            //PixelEdit.getInstance().getConsole().ifPresent(console -> console.sendMessages(Text.of(PEInfo.DEBUG_PREFIX, "Missing Pixelmon Data!")));
        }
    }

    private static boolean isHiddenAbility(Pokemon p, String ability) {
        return p.getAbilitySlot() == 2;
    }

    private static String updatePokemonName(String name){
        if(name.equalsIgnoreCase("MrMime")) return "Mr. Mime";
        else if(name.equalsIgnoreCase("MimeJr")) return "Mime Jr.";
        else if(name.equalsIgnoreCase("Nidoranfemale")) return "Nidoran&d\u2640&r";
        else if(name.equalsIgnoreCase("Nidoranmale")) return "Nidoran&b\u2642&r";
        else if(name.equalsIgnoreCase("Farfetchd")) return "Farfetch'd";
        else if(name.contains("Alolan")){
            return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name.replaceAll("\\d+", "")), " ");
        }

        return name;
    }

    public ItemStack getSprite(String title, boolean updated, Player p) {
        Optional<ItemType> sprite = Sponge.getRegistry().getType(ItemType.class, "pixelmon:pixelmon_sprite");
        ItemStack Item = ItemStack.builder().itemType(sprite.get()).build();

        if(updated) setUpdatedData();

        ItemStack item = this.setPicture(Item);
        this.setItemData(item, title);
        return item;
    }

    public boolean isEgg() { return this.isEgg; }

    private ItemStack setPicture(ItemStack item) {
        String spriteData;

        if(this.isEgg){
            if (this.pokemon.getSpecies() == EnumSpecies.Manaphy || this.pokemon.getSpecies() == EnumSpecies.Togepi) {
                spriteData = String.format("pixelmon:sprites/eggs/%s1", pokemon.getSpecies().name.toLowerCase());
            } else {
                spriteData = "pixelmon:sprites/eggs/egg1";
            }
        }else{
            spriteData = "pixelmon:" + GuiResources.getSpritePath(this.pokemon.getSpecies(), this.form.getForm(), (this.gender.equalsIgnoreCase("Male") ? Gender.Male : Gender.Female), this.pokemon.getCustomTexture(), this.shiny);
        }

        return ItemStack.builder().fromContainer(item.toContainer().set(DataQuery.of("UnsafeData","SpriteName"), spriteData)).build();
    }

    private void setUpdatedData(){
        DecimalFormat df = new DecimalFormat("##0");

        this.lvl = this.base.level != -1 ? this.base.level : this.lvl;

        if(this.base.shiny != -1){
            if(this.base.shiny == 1) this.shiny = true;
            if(this.base.shiny == 0) this.shiny = false;
        }

        if(!this.base.gender.equalsIgnoreCase("n/a")) { this.gender = this.base.gender; }
        if(!this.base.ability.equalsIgnoreCase("n/a")) { this.ability = this.base.ability; }

        this.isHiddenAbility = isHiddenAbility(this.pokemon, this.ability);

        if(!this.base.nature.equalsIgnoreCase("n/a")) { this.nature = this.base.nature; }
        if(!this.base.growth.equalsIgnoreCase("n/a")) { this.growth = this.base.growth; }
        if(!this.base.pokeball.equalsIgnoreCase("n/a")) { this.caughtBall = this.base.pokeball; }
        if(this.base.happiness != -1){ this.happiness = this.base.happiness; }

        if(this.base.ivsSelected) {
            for(int i = 0; i < this.ivs.length; i++) {
                if(this.base.ivs[i] != -1) this.ivs[i] = this.base.ivs[i];
            }
        }
        this.totalIVs = this.ivs[0] + this.ivs[1] + this.ivs[2] + this.ivs[3] + this.ivs[4] + this.ivs[5];
        this.IVPercent = df.format((double) totalIVs / 186.0D * 100.0D) + "%";

        if(this.base.evReset) {
            for(int e = 0; e < this.evs.length; e++) {
                this.evs[e] = 0;
            }
        }
        this.totalEVs = this.evs[0] + this.evs[1] + this.evs[2] + this.evs[3] + this.evs[4] + this.evs[5];
        this.EVPercent = df.format((double) totalEVs / 510.0D * 100.0D) + "%";

        if (this.base.aura) {
            this.PokemonAuras.clear();
        }
        if (this.base.form != -1) {
            try {
                this.form = PokeUtils.getAllForms(this.base.pokemon).get(this.base.form);
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                // empty catch block
            }
        }
        if(this.base.moveLearner) {
            if(!this.base.moves[0].equals("n/a")) this.m1 = this.base.moves[0];
            if(!this.base.moves[1].equals("n/a")) this.m2 = this.base.moves[1];
            if(!this.base.moves[2].equals("n/a")) this.m3 = this.base.moves[2];
            if(!this.base.moves[3].equals("n/a")) this.m4 = this.base.moves[3];
        }
    }

    private void setItemData(ItemStack item, String title) {
        String displayName = pokerus + "&b"+updatePokemonName(this.name)+" &7| &eLvl "+this.lvl+" "+((this.shiny) ? "&7(&6Shiny&7)&r" : "");
        String pokeGender = (this.gender.equals("Male")) ? "&b" + this.gender : (this.gender.equals("Female")) ? "&d" + this.gender : "Genderless";

        if(this.isEgg){
            item.offer(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&7Pok\u00E9mon Egg")));
        }else{
            if(title.equals("")) {
                item.offer(Keys.DISPLAY_NAME, Text.of(Chat.embedColours(displayName)));
            }else{
                item.offer(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&l" + title)));
            }
        }

        ArrayList<Text> itemLore = new ArrayList<>();
        if(!this.isEgg) {
            if (!title.equals("")) itemLore.add(Text.of(Chat.embedColours(displayName)));
            if (!PokemonAuras.isEmpty()) {
                itemLore.add(Text.of(Chat.embedColours("&7Aura(s): " + this.PokemonAuras.get(0) + (this.AurasEnabled > 1 ? " + " + this.PokemonAuras.get(1) : ""))));
            }
            if (!(this.form.equals(EnumSpecial.Base) || this.form.getLocalizedName().equals("None") || this.form.getLocalizedName().equals("Standard") || this.form.getLocalizedName().equals("Normal"))) {
                itemLore.add(Text.of(Chat.embedColours(("&7Form: &e" + this.form.getLocalizedName()))));
            }
            itemLore.add(Text.of(Chat.embedColours("&7Ability: &e" + this.ability + (this.isHiddenAbility ? " &7(&6HA&7)&r" : ""))));
            itemLore.add(Text.of(Chat.embedColours("&7Nature: &e" + this.nature)));
            itemLore.add(Text.of(Chat.embedColours("&7Gender: " + pokeGender)));
            itemLore.add(Text.of(Chat.embedColours("&7Size: &e" + this.growth)));
            if (!title.equals("")) itemLore.add(Text.of(Chat.embedColours("&7Happiness: &e" + this.happiness)));
            itemLore.add(Text.of(Chat.embedColours(("&7Hidden Power: &e" + this.hiddenPower))));
            itemLore.add(Text.of(Chat.embedColours("&7CaughtBall: &e" + this.caughtBall)));
            if (!title.equals("")) itemLore.add(Text.of(Chat.embedColours("")));
            itemLore.add(Text.of(Chat.embedColours("&7IVs: &e" + this.totalIVs + "&7/&e186 &7(&a" + this.IVPercent + "&7)")));
            if (!title.equals("")) itemLore.add(Text.of(Chat.embedColours("&cHP: " + this.ivs[0] + "&7/&6Atk: " + this.ivs[1] + "&7/&eDef: " + this.ivs[2] + "&7/&9SpA: " + this.ivs[3] + "&7/&aSpD: " + this.ivs[4] + "&7/&dSpe: " + this.ivs[5])));
            if (!title.equals("")) itemLore.add(Text.of(Chat.embedColours("")));
            itemLore.add(Text.of(Chat.embedColours("&7EVs: &e" + this.totalEVs + "&7/&e510 &7(&a" + this.EVPercent + "&7)")));
            if (!title.equals("")) itemLore.add(Text.of(Chat.embedColours("&cHP: " + this.evs[0] + "&7/&6Atk: " + this.evs[1] + "&7/&eDef: " + this.evs[2] + "&7/&9SpA: " + this.evs[3] + "&7/&aSpD: " + this.evs[4] + "&7/&dSpe: " + this.evs[5])));
            if (!title.equals("")) itemLore.add(Text.of(Chat.embedColours("")));
            if (!title.equals("")) itemLore.add(Text.of(Chat.embedColours("&7Moves: &b" + m1 + " &7- &b" + m2 + " &7- &b" + m3 + " &7- &b" + m4)));
        }else{
            itemLore.add(Text.of(Chat.embedColours("&7Wait til it hatches first...")));
        }

        item.offer(Keys.ITEM_LORE, itemLore);
    }
}
