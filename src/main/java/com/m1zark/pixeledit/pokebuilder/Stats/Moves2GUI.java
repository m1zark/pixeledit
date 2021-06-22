package com.m1zark.pixeledit.pokebuilder.Stats;

import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.Inventories;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.pixeledit.pokebuilder.SharedIcons;
import com.m1zark.pixeledit.util.PokeUtils;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import com.pixelmonmod.pixelmon.comm.packetHandlers.OpenReplaceMoveScreen;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Moves2GUI extends InventoryManager {
    private Player player;
    private int page;
    private int maxPage;
    private MovesGUI moves;
    private int slot;

    public Moves2GUI(Player player, int page, MovesGUI moves, int slot) {
        super(player, 6, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Move Selection")));
        this.page = page;
        this.player = player;
        this.moves = moves;
        this.slot = slot;

        int size = PokeUtils.getAllAttacks(this.moves.base.pokemon).size();
        this.maxPage = size % 44 == 0 && size / 44 != 0 ? size / 44 : size / 44 + 1;

        this.setupDesign();
        this.setupTMs();
    }

    private void setupDesign() {
        for(int y = 5, x = 0; x < 9; x++) {
            this.addIcon(SharedIcons.BorderIcon(x + 9 * y, DyeColors.RED, ""));
        }

        Icon previousPage = pageIcon(48, false);
        previousPage.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.updatePage(false);
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(previousPage);

        Icon back = backIcon(49);
        back.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.player.openInventory(this.moves.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);

        Icon nextPage = pageIcon(50, true);
        nextPage.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.updatePage(true);
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(nextPage);
    }

    private void setupTMs() {
        int index = (this.page - 1) * 44;
        List<String> attacks = PokeUtils.getAllAttacks(this.moves.base.pokemon);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9; x++, index++) {
                if (index >= attacks.size()) break;

                final int pos = index;
                Icon item = tmIcon(x + (9 * y), attacks.get(index));
                item.addListener(clickable -> {
                    Optional<AttackBase> attack = AttackBase.getAttackBase(attacks.get(pos).replaceAll("_", " "));
                    if(attack.isPresent()) {
                        int cost = Config.getPrices("Moves", "allMoves");
                        int s = slot==11 ? 0 : slot==12 ? 1 : slot==14 ? 2 : 3;

                        if(this.moves.moves[s].equals("n/a")) this.moves.base.totalCost = this.moves.base.totalCost + cost;
                        this.moves.moves[s] = attack.get().getLocalizedName();

                        Sponge.getScheduler().createTaskBuilder().execute(() -> {
                            this.moves.addIcon(this.moves.selectedIcon());
                            this.moves.setupMoves();
                            this.moves.updateContents();
                            this.player.openInventory(this.moves.getInventory());
                        }).delayTicks(1L).submit(PixelEdit.getInstance());
                    }
                });
                this.addIcon(item);
            }
        }
    }

    private void updatePage(boolean upOrDown) {
        if (upOrDown) {
            if (this.page < this.maxPage) ++this.page;
            else this.page = 1;
        } else if (this.page > 1) --this.page;
        else this.page = this.maxPage;

        // Previous Page
        Icon previousPage = pageIcon(48, false);
        previousPage.addListener(clickable -> {
            this.updatePage(false);
        });
        this.addIcon(previousPage);

        // Next Page
        Icon nextPage = pageIcon(50, true);
        nextPage.addListener(clickable -> {
            this.updatePage(true);
        });
        this.addIcon(nextPage);

        this.clearIcons(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44);
        this.setupTMs();
        this.updateContents(0,44);
    }


    private static Icon backIcon(int slot) {
        return new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.CONCRETE)
                .quantity(1)
                .add(Keys.DYE_COLOR, DyeColors.RED)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&c&lGo Back")))
                .build());
    }

    private Icon tmIcon(int slot, String name) {
        Optional<AttackBase> a = AttackBase.getAttackBase(name.replaceAll("_", " "));

        AttackBase attackBase = a.orElse(null);
        if (attackBase != null) {
            String[] newInfo = PokeUtils.insertLinebreaks(PokeUtils.format(TextFormatting.AQUA, "attack." + name.replace(" ", "_").toLowerCase() + ".description").getFormattedText(), 40).split("\n");

            ArrayList<Text> itemLore = new ArrayList<>();
            for (String s : newInfo) itemLore.add(Text.of(Chat.embedColours("&b" + s)));
            itemLore.add(Text.of(Chat.embedColours("&7Cost: &a" + Config.getPrices("Moves", "allMoves") + " " +  Config.getItem("currencyName") + (Config.getPrices("Moves", "allMoves")==1 ? "" : "(s)"))));
            itemLore.add(Text.of(Chat.embedColours("")));
            itemLore.add(Text.of(Chat.embedColours("&7Category: &a" + attackBase.getAttackCategory().getLocalizedName())));
            itemLore.add(Text.of(Chat.embedColours("&7Type: " + getTM(attackBase.getAttackType().getName())[1] + attackBase.getAttackType().getName())));
            itemLore.add(Text.of(Chat.embedColours("&7Attack Power: &a" + (attackBase.getBasePower() != 0 ? attackBase.getBasePower() : "-"))));
            itemLore.add(Text.of(Chat.embedColours("&7Accuracy: &a" + (attackBase.getAccuracy() != -1 ? attackBase.getAccuracy() : "-"))));
            itemLore.add(Text.of(Chat.embedColours("&7PP: &a" + attackBase.getPPBase())));

            return new Icon(slot, ItemStack.builder()
                    .fromItemStack(ItemStackUtil.fromNative(PokeUtils.getTMs(attackBase.getAttackName())))
                    .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&6&l" + name)))
                    .add(Keys.ITEM_LORE, itemLore)
                    .build());
        } else {
            return new Icon(slot, ItemStack.builder()
                    .itemType(ItemTypes.BARRIER)
                    .add(Keys.DISPLAY_NAME,Text.of(TextColors.RED, "Error getting attack"))
                    .build());
        }
    }

    private Icon pageIcon(int slot, boolean nextOrLast) {
        return new Icon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, nextOrLast ? "pixelmon:trade_holder_right" : "pixelmon:trade_holder_left").get())
                .quantity(1)
                .add(Keys.DISPLAY_NAME, nextOrLast ? Text.of(TextColors.GREEN, "\u2192 ", "Next Page", TextColors.GREEN, " \u2192") : Text.of(TextColors.RED, "\u2190 ", "Previous Page", TextColors.RED, " \u2190"))
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
