package com.m1zark.pixeledit.util;

import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.pokebuilder.TutorGUI;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.config.PixelmonItemsTMs;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.forms.EnumSpecial;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import com.pixelmonmod.pixelmon.enums.technicalmoves.*;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class PokeUtils {
    public static ItemType scrollType = ItemTypes.PAPER;
    public static ArrayList<Item> tms = new ArrayList();
    public static ArrayList<Item> galartms = new ArrayList();

    @Nullable
    public static PlayerPartyStorage getPlayerStorage(EntityPlayerMP player) {
        return Pixelmon.storageManager.getParty(player);
    }

    public static List<IEnumForm> getAllForms(Pokemon pokemon) {
        List forms = pokemon.getSpecies().getPossibleForms(false);
        forms.removeIf(form -> form.equals(EnumSpecial.Online));
        forms.removeIf(form -> form.equals(pokemon.getFormEnum()));
        return forms;
    }

    public static boolean hasSpecialTexture(Pokemon pokemon) {
        ArrayList<EnumSpecial> specials = new ArrayList<>();
        Collections.addAll(specials, EnumSpecial.values());
        specials.removeIf(form -> form.equals(EnumSpecial.Rainbow));
        specials.removeIf(form -> form.equals(EnumSpecial.Valentine));
        specials.removeIf(form -> form.equals(EnumSpecial.Base));
        for (IEnumForm iEnumForm : specials) {
            if (!pokemon.getFormEnum().equals(iEnumForm)) continue;
            return true;
        }

        return false;
    }

    public static boolean isHiddenAbility(Pokemon p, String ability) {
        return p.getAbilitySlot() == 2;
    }

    private static ArrayList<String> scrollInfo(String type) {
        ArrayList<String> info = new ArrayList<>();

        if (type.equalsIgnoreCase("ivs")) {
            info.add("Perfect IVs Scroll");
            info.add("Right click on a Pok\u00E9mon to give it 100% IVs!");
        }
        if(type.equalsIgnoreCase("gender")) {
            info.add("Swap Gender Scroll");
            info.add("Right click on a Pok\u00E9mon to toggle its gender!");
        }
        if(type.equalsIgnoreCase("sizeG")) {
            info.add("Ginormous Scroll");
            info.add("Right click on a Pok\u00E9mon to enlarge it to the largest possible size!");
        }
        if(type.equalsIgnoreCase("sizeM")) {
            info.add("Microscopic Scroll");
            info.add("Right click on a Pok\u00E9mon to shrink it to the smallest possible size!");
        }
        if(type.equalsIgnoreCase("shiny")) {
            info.add("Shiny Turner Into Thing");
            info.add("Right click on a Pok\u00E9mon to turn it into its shiny version!");
        }
        if(type.equalsIgnoreCase("ha")) {
            info.add("Hidden Ability Scroll");
            info.add("Right click on a Pok\u00E9mon to get its hidden ability!");
        }
        if(type.equalsIgnoreCase("lvl")) {
            info.add("Max Level Scroll");
            info.add("Right click on a Pok\u00E9mon to instantly make it level 100!");
        }
        if(type.equalsIgnoreCase("moves")) {
            info.add("Move Tutor Scroll");
            info.add("Right click on a Pok\u00E9mon to teach it a new move.");
        }
        if(type.equalsIgnoreCase("happiness")) {
            info.add("Max Happiness Scroll");
            info.add("Right click on a Pok\u00E9mon to instantly gain max happiness.");
        }
        if(type.equalsIgnoreCase("evs")) {
            info.add("Reset EVs Scroll");
            info.add("Right click on a Pok\u00E9mon to reset all EVs to 0.");
        }

        return info;
    }

    public static boolean updatePokemon(String type, Pokemon pokemon, Player player){
        if (type.equalsIgnoreCase("ivs")) {
            int ivSum = 0;
            for (int i : pokemon.getIVs().getArray()) ivSum += i;

            if ((int) (((double) ivSum / 186) * 100) == 100) {
                Chat.sendMessage(player, "&b"+pokemon.getSpecies().name()+"&a already has max IVs.");
                return false;
            }else {
                pokemon.getIVs().maximizeIVs();
            }
        }
        if(type.equalsIgnoreCase("evs")){
            int evSum = 0;
            for(int i : pokemon.getEVs().getArray()) evSum += i;

            if(evSum == 0) {
                Chat.sendMessage(player, "&b"+pokemon.getSpecies().name()+"&a already has 0 EVs.");
                return false;
            } else {
                pokemon.getEVs().setStat(StatsType.Attack, 0);
                pokemon.getEVs().setStat(StatsType.Defence, 0);
                pokemon.getEVs().setStat(StatsType.SpecialAttack, 0);
                pokemon.getEVs().setStat(StatsType.SpecialDefence, 0);
                pokemon.getEVs().setStat(StatsType.HP, 0);
                pokemon.getEVs().setStat(StatsType.Speed, 0);
            }
        }
        if (type.equalsIgnoreCase("gender")) {
            if(between((int) pokemon.getBaseStats().getMalePercent(), 0, 99)) {
                pokemon.setGender(pokemon.getGender().equals(Gender.Male) ? Gender.Female : Gender.Male);
            } else {
                Chat.sendMessage(player, "&b"+pokemon.getSpecies().name()+"'s &agender cannot be changed.");
                return false;
            }
        }
        if (type.equalsIgnoreCase("sizeG")) {
            if(pokemon.getGrowth().getLocalizedName().equals("Ginormous")) {
                Chat.sendMessage(player, "&b"+pokemon.getSpecies().name()+"&a is already Ginormous.");
                return false;
            }else{
                pokemon.setGrowth(EnumGrowth.Ginormous);
            }
        }
        if (type.equalsIgnoreCase("sizeM")) {
            if(pokemon.getGrowth().getLocalizedName().equals("Microscopic")) {
                Chat.sendMessage(player, "&b"+pokemon.getSpecies().name()+"&a is already Microscopic.");
                return false;
            }else {
                pokemon.setGrowth(EnumGrowth.Microscopic);
            }
        }
        if (type.equalsIgnoreCase("shiny")) {
            if(pokemon.isShiny()) {
                Chat.sendMessage(player, "&b"+pokemon.getSpecies().name()+"&a is already shiny.");
                return false;
            }else {
                pokemon.setShiny(true);
            }
        }
        if (type.equalsIgnoreCase("ha")) {
            if (pokemon.getAbilitySlot() == 2) {
                Chat.sendMessage(player, "&b"+pokemon.getSpecies().name()+"&a already has its Hidden Ability.");
                return false;
            }else {
                if (pokemon.getBaseStats().getAbilitiesArray().length == 3 && pokemon.getBaseStats().getAbilitiesArray()[2] != null) {
                    pokemon.setAbility(pokemon.getBaseStats().getAbilitiesArray()[2]);
                    pokemon.setAbilitySlot(2);
                }else {
                    Chat.sendMessage(player, "&b"+pokemon.getSpecies().name()+"&a doesn't have a HA to learn.");
                    return false;
                }
            }
        }
        if (type.equalsIgnoreCase("lvl")) {
            if(pokemon.getLevel() == 100) {
                Chat.sendMessage(player, "&b"+pokemon.getSpecies().name()+"&a is already at max level.");
                return false;
            }else {
                pokemon.setLevel(100);
            }
        }
        if(type.equalsIgnoreCase("happiness")){
            if(pokemon.getFriendship() == 255) {
                Chat.sendMessage(player, "&b"+pokemon.getSpecies().name()+"&a already has max happiness.");
                return false;
            }else {
                pokemon.setFriendship(255);
            }
        }
        if(type.equalsIgnoreCase("moves")) {
            player.openInventory((new TutorGUI(player, 1, pokemon, false)).getInventory());
            return false;
        }

        return true;
    }

    public static String updatePokemonName(String name){
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

    public static String insertLinebreaks(String s, int charsPerLine) {
        char[] chars = s.toCharArray();
        int lastLinebreak = 0;
        boolean wantLinebreak = false;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            if (wantLinebreak && chars[i] == ' ') {
                sb.append('\n');
                lastLinebreak = i;
                wantLinebreak = false;
            } else {
                sb.append(chars[i]);
            }
            if (i - lastLinebreak + 1 == charsPerLine)
                wantLinebreak = true;
        }
        return sb.toString();
    }

    public static boolean between(int i, int minValueInclusive, int maxValueInclusive) {
        return (i >= minValueInclusive && i <= maxValueInclusive);
    }

    public static String asReadableList(List<String> data) {
        String separator = ", ";
        String list = "";
        for (Object s : data) {
            if (s != null) {
                if (s.equals(data.get(0))) {
                    list = s.toString();
                } else {
                    list = list.concat(separator + s);
                }
            }
        }
        return list.isEmpty() ? "None" : list;
    }

    public static ItemStack currencyItem(){
        ItemStack crystal = ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, Config.getItem("itemType")).get()).add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours(Config.getItem("itemName")))).build();

        String lore = Config.getItem("itemLore");
        ArrayList<Text> itemLore = new ArrayList<>();
        String[] newInfo = lore.split("\n");
        for(String s:newInfo) itemLore.add(Text.of(Chat.embedColours(s)));
        crystal.offer(Keys.ITEM_LORE, itemLore);

        crystal.offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 1)));
        crystal.offer(Keys.HIDE_ENCHANTMENTS, true);

        return ItemStack.builder().fromContainer(crystal.toContainer().set(DataQuery.of("UnsafeData","PokeBuilder"), 1)).build();
    }

    public static ItemStack getScroll(String type){
        ItemStack key = ItemStack.builder().itemType(scrollType).add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, scrollInfo(type).get(0))).build();
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(TextColors.DARK_GREEN, scrollInfo(type).get(1)));
        key.offer(Keys.ITEM_LORE, itemLore);

        key.offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 1)));
        key.offer(Keys.HIDE_ENCHANTMENTS, true);

        return ItemStack.builder().fromContainer(key.toContainer().set(DataQuery.of("UnsafeData","typeID"),type)).build();
    }

    public static List<String> getAllAttacks(Pokemon pokemon) {
        List<Attack> attacks = pokemon.getBaseStats().getAllMoves();
        attacks.addAll(pokemon.getBaseStats().getTutorMoves());
        attacks.addAll(pokemon.getBaseStats().getEggMoves());
        attacks.addAll(pokemon.getBaseStats().getMovesUpToLevel(100));
        attacks.removeAll(pokemon.getBaseStats().getTMMoves());

        List<String> attackNames = new ArrayList<>();
        attacks.forEach(attack -> {
            if(!pokemon.getMoveset().hasAttack(attack)) attackNames.add(attack.getActualMove().getLocalizedName());
        });

        return attackNames.stream().distinct().collect(Collectors.toList());
    }

    public static ITextComponent format(TextFormatting color, String lang, Object ... args) {
        TextComponentTranslation message = new TextComponentTranslation(lang, args);
        message.getStyle().setColor(color);
        return message;
    }

    public static net.minecraft.item.ItemStack getTMs(String name) {
        if(Gen1TechnicalMachines.getTm(name) != null) return PixelmonItemsTMs.createStackFor(Gen1TechnicalMachines.getTm(name));
        if(Gen2TechnicalMachines.getTm(name) != null) return PixelmonItemsTMs.createStackFor(Gen2TechnicalMachines.getTm(name));
        if(Gen3TechnicalMachines.getTm(name) != null) return PixelmonItemsTMs.createStackFor(Gen3TechnicalMachines.getTm(name));
        if(Gen4TechnicalMachines.getTm(name) != null) return PixelmonItemsTMs.createStackFor(Gen4TechnicalMachines.getTm(name));
        if(Gen5TechnicalMachines.getTm(name) != null) return PixelmonItemsTMs.createStackFor(Gen5TechnicalMachines.getTm(name));
        if(Gen6TechnicalMachines.getTm(name) != null) return PixelmonItemsTMs.createStackFor(Gen6TechnicalMachines.getTm(name));
        if(Gen7TechnicalMachines.getTm(name) != null) return PixelmonItemsTMs.createStackFor(Gen7TechnicalMachines.getTm(name));
        if(Gen8TechnicalMachines.getTm(name) != null) return PixelmonItemsTMs.createStackFor(Gen8TechnicalMachines.getTm(name));

        return PixelmonItemsTMs.createStackFor(Gen1TechnicalMachines.Bide);
    }
}
