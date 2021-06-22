package com.m1zark.pixeledit.util.configuration;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.m1zark.pixeledit.PEInfo;
import com.m1zark.pixeledit.PixelEdit;
import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.text.Text;

public class Config {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode main;

    public Config() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(PixelEdit.getInstance().getConfigDir() + "/pokebuilder.conf");
        loader = HoconConfigurationLoader.builder().setPath(configFile).build();

        try {
            if (!Files.exists(PixelEdit.getInstance().getConfigDir())) {
                Files.createDirectory(PixelEdit.getInstance().getConfigDir());
            }

            if (!Files.exists(configFile)) {
                Files.createFile(configFile);
            }

            if (main == null) {
                main = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
            }

            CommentedConfigurationNode general = main.getNode("General");
            CommentedConfigurationNode info = main.getNode("Info");
            CommentedConfigurationNode prices = main.getNode("Prices");

            general.getNode("currency","itemType").setComment("Set the item used as currency for the Pok\u00E9Builder");
            general.getNode("currency","itemType").getString("pixelmon:crystal");
            general.getNode("currency","itemName").getString("&6Mirage&aCrystal");
            general.getNode("currency", "currencyName").setComment("The name to display in mouseover lore in the builder. Will update for multiple as needed.");
            general.getNode("currency", "currencyName").getString("Crystal");
            general.getNode("currency","itemLore").setComment("Sets the lore for the item. Can use color codes, and \\n to add linebreaks for multiple lines.");
            general.getNode("currency","itemLore").getString("Can be used as currency to edit a\nPok\u00E9mons stats via the Pok\u00E9Builder.\n\n&c&lNonrefundable");

            general.getNode("howToGet").setComment("Set the mouseover lore to show how to get crystals.");
            general.getNode("howToGet").getString("&7You can earn &aCrystals &7by winning listeners.\n\n&7You can purchase &aCrystals &7at\n&9http://miragecraft.xyz");

            general.getNode("expireLogs").setComment("Number of days to keep logs in database");
            general.getNode("expireLogs").getInt(30);

            general.getNode("formBlacklist").getList(TypeToken.of(String.class), Lists.newArrayList("Arceus", "Genesect", "Castform", "Silvally"));

            info.getNode("movesInfo").getString("To pick a new move for your Pok\u00E9mon simply click on the TM to cycle through available moves (Excluding TMs and HMs).");
            info.getNode("abilityInfo").getString("To pick an ability for your Pok\u00E9mon simply select one from the list on the right.");
            info.getNode("pokeballInfo").getString("To pick a new PokéBall for your Pok\u00E9mon simply select one from the list on the right.");
            info.getNode("happinessInfo").getString("To pick boost your Pok\u00E9mon 's happiness to max simply select the option on the right.");
            info.getNode("genderInfo").getString("To pick a new gender for your Pok\u00E9mon simply select one from the list on the right.");
            info.getNode("growthInfo").getString("To pick a size/growth for your Pok\u00E9mon simply select one from the list on the right.");
            info.getNode("ivsevsInfo").getString("To change a stat for your Pok\u00E9mon simply select one from the list on the right.");
            info.getNode("levelInfo").getString("To boost your Pok\u00E9mon to level 100 simply select the option on the right.");
            info.getNode("natureInfo").getString("To pick a nature for your Pok\u00E9mon simply select one from the list on the right.");
            info.getNode("shinyInfo").getString("To choose a shininess for your Pok\u00E9mon simply select an option from the list on the right.");
            info.getNode("tutorInfo").getString("SImply click on one of the above tms to teach your Pok\u00E9mon the corresponding move. If you have 4 moves already, you will be asked to replace one.");

            prices.getNode("SpecialTexture","remove").getInt(0);
            prices.getNode("Aura","remove").getInt(0);
            prices.getNode("Alolan", "update").getInt(5);
            prices.getNode("Moves", "allMoves").getInt(5);
            prices.getNode("Form", "change").getInt(5);
            prices.getNode("Abilities", "normal").getInt(3);
            prices.getNode("Abilities", "hidden").getInt(5);
            prices.getNode("Abilities", "greninja").getInt(5);
            prices.getNode("Abilities", "zygarde").getInt(5);
            prices.getNode("Gender", "Male").getInt(2);
            prices.getNode("Gender", "Female").getInt(2);
            prices.getNode("Shiny", "Shiny").getInt(5);
            prices.getNode("Shiny", "Non-Shiny").getInt(2);
            prices.getNode("Levels", "lvl100").getInt(5);
            prices.getNode("Happiness", "maxHappiness").getInt(1);
            prices.getNode("Happiness", "zeroHappiness").getInt(1);
            prices.getNode("IVs", "0ivs").getInt(5);
            prices.getNode("IVs", "30ivs").getInt(5);
            prices.getNode("IVs", "31ivs").getInt(5);
            prices.getNode("IVs", "dittoIncrease").getInt(2);
            prices.getNode("EVs", "reset").getInt(1);

            for(int i = 0; i < EnumGrowth.class.getEnumConstants().length; i++){
                prices.getNode("Sizes", EnumGrowth.getGrowthFromIndex(i).toString()).getInt(RandomHelper.getRandomNumberBetween(1,5));
            }

            for(int i = 5; i < EnumNature.class.getEnumConstants().length; i++){
                prices.getNode("Natures", EnumNature.getNatureFromIndex(i).toString()).getInt(RandomHelper.getRandomNumberBetween(1,5));
            }

            for(int i = 0; i < EnumPokeballs.class.getEnumConstants().length; i++){
                prices.getNode("Pokeballs", EnumPokeballs.getFromIndex(i).toString()).getInt(RandomHelper.getRandomNumberBetween(1,5));
            }

            loader.save(main);
        } catch (IOException | ObjectMappingException e) {
			PixelEdit.getInstance().getConsole().ifPresent(console -> console.sendMessages(Text.of(PEInfo.ERROR_PREFIX, "There was an issue loading the config...")));
            e.printStackTrace();
			return;
        }
		
		PixelEdit.getInstance().getConsole().ifPresent(console -> console.sendMessages(Text.of(PEInfo.PREFIX, "Loading configuration...")));
    }

    public static void saveConfig() {
        try {
            loader.save(main);
        } catch (IOException var1) {
            var1.printStackTrace();
        }
    }

    public void reload() {
        try {
            main = loader.load();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public static int getPrices(String type, String name){ return main.getNode("Prices", type, name).getInt(); }

    public static String getItem(String name){ return main.getNode("General", "currency", name).getString(); }

    public static String getHowTo(){ return main.getNode("General", "howToGet").getString(); }

    public static String getInfo(String name){ return main.getNode("Info", name).getString(); }

    public static int getexpireLogs() { return main.getNode("General","expireLogs").getInt(); }

    public static List<String> getBlacklist() {
        try {
            return main.getNode("General", "formBlacklist").getList(TypeToken.of(String.class));
        }
        catch (ObjectMappingException e) {
            return Lists.newArrayList();
        }
    }
}
