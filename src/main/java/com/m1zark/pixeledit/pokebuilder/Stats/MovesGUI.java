package com.m1zark.pixeledit.pokebuilder.Stats;

import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.pixeledit.pokebuilder.BuilderGUI;
import com.m1zark.pixeledit.pokebuilder.SharedIcons;
import com.m1zark.pixeledit.util.PokeUtils;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.config.PixelmonItemsTMs;
import com.pixelmonmod.pixelmon.enums.technicalmoves.*;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.util.ArrayList;

public class MovesGUI extends InventoryManager {
    private Player player;
    public BuilderGUI base;
    public String[] moves;

    public MovesGUI(Player player, BuilderGUI base) {
        super(player, 3, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Moves")));
        this.player = player;
        this.base = base;
        this.moves = this.base.moves;

        setupDesign();
    }

    private void setupDesign() {
        for(int y = 0, x = 0, index = 0; y < 3 && index < 27; ++index) {
            if (x == 9) {
                x = 0;
                ++y;
            }

            this.addIcon(SharedIcons.BorderIcon(x + (9 * y),  DyeColors.GRAY, ""));
            ++x;
        }

        this.addIcon(this.infoIcon());
        setupMoves();

        this.addIcon(this.selectedIcon());
        this.addIcon(SharedIcons.infoIcon(18, "Moves Info", Config.getInfo("movesInfo"), 3, this.base));

        Icon reset = SharedIcons.resetIcon(8);
        reset.addListener(clickable -> {
            for(int i = 0; i < this.moves.length; i++) {
                if(!this.moves[i].equals("n/a")) {
                    this.base.totalCost = this.base.totalCost - Config.getPrices("Moves", "allMoves");
                    this.moves[i] = "n/a";
                }
            }
            this.base.moveLearner = false;

            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.setupMoves();
                this.addIcon(this.selectedIcon());
                this.updateContents();
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(reset);

        Icon back = SharedIcons.backIcon(26);
        back.addListener(clickable -> {
            this.base.moves = this.moves;

            for(String name: this.moves) if (!name.equals("n/a")) this.base.moveLearner = true;

            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.base.addIcon(this.base.moveIcon(this.base.moveLearner));
                this.base.addIcon(this.base.confirmIcon());
                this.base.updateContents();
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);
    }

    public void setupMoves() {
        this.addIcon(this.tmIcon(11, !this.moves[0].equals("n/a") ? this.moves[0] : this.base.pokemon.getMoveset().get(0) != null ? this.base.pokemon.getMoveset().get(0).getActualMove().getLocalizedName() : "Empty"));
        this.addIcon(this.tmIcon(12, !this.moves[1].equals("n/a") ? this.moves[1] : this.base.pokemon.getMoveset().get(1) != null ? this.base.pokemon.getMoveset().get(1).getActualMove().getLocalizedName() : "Empty"));
        this.addIcon(this.tmIcon(14, !this.moves[2].equals("n/a") ? this.moves[2] : this.base.pokemon.getMoveset().get(2) != null ? this.base.pokemon.getMoveset().get(2).getActualMove().getLocalizedName() : "Empty"));
        this.addIcon(this.tmIcon(15, !this.moves[3].equals("n/a") ? this.moves[3] : this.base.pokemon.getMoveset().get(3) != null ? this.base.pokemon.getMoveset().get(3).getActualMove().getLocalizedName() : "Empty"));
    }

    public Icon selectedIcon(){
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Current:")));
        if(!this.moves[0].equals("n/a")) itemLore.add(Text.of(Chat.embedColours("&7Move 1: &e" + this.moves[0])));
        if(!this.moves[1].equals("n/a")) itemLore.add(Text.of(Chat.embedColours("&7Move 2: &e" + this.moves[1])));
        if(!this.moves[2].equals("n/a")) itemLore.add(Text.of(Chat.embedColours("&7Move 3: &e" + this.moves[2])));
        if(!this.moves[3].equals("n/a")) itemLore.add(Text.of(Chat.embedColours("&7Move 4: &e" + this.moves[3])));

        //return new Icon(0, SharedIcons.createSkull(EnumTextures.MasterBall, Text.of(Chat.embedColours("&3&lSelected Moves")), itemLore));

        return new Icon(0, ItemStack.builder()
                .itemType(ItemTypes.CONCRETE)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lSelected Moves")))
                .add(Keys.ITEM_LORE, itemLore)
                .build());
    }

    private Icon tmIcon(int slot, String name) {
        AttackBase attack = AttackBase.getAttackBase(name.replaceAll("_", " ")).orElse(null);
        Icon icon;

        if (name.equals("Empty")) {
            icon = new Icon(slot, ItemStack.builder()
                    .itemType(ItemTypes.CONCRETE)
                    .add(Keys.DYE_COLOR, DyeColors.RED)
                    .add(Keys.DISPLAY_NAME,Text.of(TextColors.RED, "No Move Set"))
                    .build());
        } else {
            if (attack != null) {
                String[] newInfo = PokeUtils.insertLinebreaks(PokeUtils.format(TextFormatting.AQUA, "attack." + name.replace(" ", "_").toLowerCase() + ".description").getFormattedText(), 40).split("\n");

                ArrayList<Text> itemLore = new ArrayList<>();
                for (String s : newInfo) itemLore.add(Text.of(Chat.embedColours("&b" + s)));
                itemLore.add(Text.of(Chat.embedColours("")));
                itemLore.add(Text.of(Chat.embedColours("&7Category: &a" + attack.getAttackCategory().getLocalizedName())));
                itemLore.add(Text.of(Chat.embedColours("&7Type: " + getTM(attack.getAttackType().getName())[1] + attack.getAttackType().getName())));
                itemLore.add(Text.of(Chat.embedColours("&7Attack Power: &a" + (attack.getBasePower() != 0 ? attack.getBasePower() : "-"))));
                itemLore.add(Text.of(Chat.embedColours("&7Accuracy: &a" + (attack.getAccuracy() != -1 ? attack.getAccuracy() : "-"))));
                itemLore.add(Text.of(Chat.embedColours("&7PP: &a" + attack.getPPBase())));

                icon = new Icon(slot, ItemStack.builder()
                    .fromItemStack(ItemStackUtil.fromNative(PokeUtils.getTMs(attack.getAttackName())))
                    .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&6&l" + name)))
                    .add(Keys.ITEM_LORE, itemLore)
                    .build());
            } else {
                icon = new Icon(slot, ItemStack.builder()
                        .itemType(ItemTypes.BARRIER)
                        .add(Keys.DISPLAY_NAME,Text.of(TextColors.RED, "Error getting attack"))
                        .build());
            }
        }

        icon.addListener(clickable ->
            Sponge.getScheduler().createTaskBuilder().execute(() ->
                this.player.openInventory((new Moves2GUI(this.player, 1, this, slot)).getInventory())
            ).delayTicks(1L).submit(PixelEdit.getInstance())
        );

        return icon;
    }

    private Icon infoIcon() {
        String[] allMoves = PokeUtils.insertLinebreaks(PokeUtils.asReadableList(PokeUtils.getAllAttacks(this.base.pokemon)), 60).split("\n");

        ArrayList<Text> itemLore = new ArrayList<>();
        for(String s:allMoves) itemLore.add(Text.of(Chat.embedColours("&a" + s)));

        return new Icon(13, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:weakness_policy").get())
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lAvailable Moves")))
                .add(Keys.ITEM_LORE, itemLore)
                .build());
    }

    private String[] getTM(String name) {
        String tm = "tm_gen8";
        String color = "";

        switch (name) {
            case "Dragon":
                tm = "tm_gen8";
                color = "&1";
                break;
            case "Fire":
                tm = "tm_gen8";
                color = "&c";
                break;
            case "Fairy":
                tm = "tm_gen8";
                color = "&d";
                break;
            case "Dark":
                tm = "tm_gen8";
                color = "&8";
                break;
            case "Ghost":
                tm = "tm_gen8";
                color = "&5";
                break;
            case "Water":
                tm = "tm_gen8";
                color = "&9";
                break;
            case "Electric":
                tm = "tm_gen8";
                color = "&e";
                break;
            case "Ground":
                tm = "tm_gen8";
                color = "&6";
                break;
            case "Rock":
                tm = "tm_gen8";
                color = "&6";
                break;
            case "Steal":
                tm = "tm_gen8";
                color = "&7";
                break;
            case "Poison":
                tm = "tm_gen8";
                color = "&5";
                break;
            case "Normal":
                tm = "tm_gen8";
                color = "&f";
                break;
            case "Fighting":
                tm = "tm_gen8";
                color = "&4";
                break;
            case "Flying":
                tm = "tm_gen8";
                color = "&b";
                break;
            case "Grass":
                tm = "tm_gen8";
                color = "&a";
                break;
            case "Psychic":
                tm = "tm_gen8";
                color = "&d";
                break;
            case "Ice":
                tm = "tm_gen8";
                color = "&3";
                break;
            case "Bug":
                tm = "tm_gen8";
                color = "&2";
                break;
        }

        return new String[]{tm,color};
    }
}
