package com.m1zark.pixeledit.pokebuilder;

import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.Inventories;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.pixeledit.util.PokeUtils;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.LearnMoveController;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import com.pixelmonmod.pixelmon.comm.packetHandlers.OpenReplaceMoveScreen;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
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

public class TutorGUI extends InventoryManager {
    private Player player;
    private int page;
    private int maxPage;
    private Pokemon pokemon;
    private boolean command;

    public TutorGUI(Player player, int page, Pokemon pokemon, boolean command) {
        super(player, 6, Text.of(Chat.embedColours("&4&lPok\u00E9Tutor &l&0\u27A5&r &8Moves")));
        this.page = page;
        this.player = player;
        this.pokemon = pokemon;
        this.command = command;

        int size = getAllAttacks(pokemon).size();
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

        Icon close = SharedIcons.closeIcon(49);
        close.addListener(clickable -> {
            this.player.closeInventory();
        });
        this.addIcon(close);

        Icon nextPage = pageIcon(50, true);
        nextPage.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.updatePage(true);
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(nextPage);

        this.addIcon(infoIcon(53, Config.getInfo("tutorInfo")));
    }

    private void setupTMs() {
        int index = (this.page - 1) * 44;
        List<String> attacks = getAllAttacks(this.pokemon);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9; x++, index++) {
                if (index >= attacks.size()) break;

                final int pos = index;
                Icon item = tmIcon(x + (9 * y), attacks.get(index));
                item.addListener(clickable -> {
                    Optional<AttackBase> attack = AttackBase.getAttackBase(attacks.get(pos).replaceAll("_", " "));
                    if(attack.isPresent()) {
                        if(this.pokemon.getBaseStats().canLearn(attack.get().getLocalizedName()) || this.canLearnOtherMove(this.pokemon, attack.get().getLocalizedName())) {
                            if (!this.pokemon.getMoveset().hasAttack(attack.get().getLocalizedName())) {
                                if(!this.command) Inventories.removeItem(this.player, PokeUtils.getScroll("moves"), 1);

                                if (this.pokemon.getMoveset().size() >= 4) {
                                    this.player.closeInventory();
                                    LearnMoveController.sendLearnMove(this.pokemon.getOwnerPlayer(), pokemon.getUUID(), attack.get());
                                    Chat.sendMessage(this.player, "&aSuccessfully sent the move to " + this.player.getName() + "'s " + this.pokemon.getDisplayName());
                                } else {
                                    this.pokemon.getMoveset().add(new Attack(attack.get()));
                                    Chat.sendMessage(this.player, String.format("&a%s just learned %s!", this.pokemon.getDisplayName(), attack.get().getLocalizedName()));
                                    this.player.closeInventory();
                                }
                            } else {
                                Chat.sendMessage(this.player, "&c" + this.pokemon.getDisplayName() + " has already learned this move!");
                                this.player.closeInventory();
                            }
                        } else {
                            Chat.sendMessage(this.player, "&c" + this.pokemon.getDisplayName() + " can not learn " + attack.get().getLocalizedName() + "!");
                            this.player.closeInventory();
                        }
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


    private Icon tmIcon(int slot, String name) {
        String tm = "tm1";
        String color = "";
        Optional<AttackBase> a = AttackBase.getAttackBase(name.replaceAll("_", " "));

        AttackBase attackBase = a.orElse(null);
        if (attackBase != null) {
            switch (attackBase.getAttackType().getName()) {
                case "Dragon":
                    tm = "tm2";
                    color = "&1";
                    break;
                case "Fire":
                    tm = "tm11";
                    color = "&c";
                    break;
                case "Fairy":
                    tm = "tm169";
                    color = "&d";
                    break;
                case "Dark":
                    tm = "tm164";
                    color = "&8";
                    break;
                case "Ghost":
                    tm = "tm30";
                    color = "&5";
                    break;
                case "Water":
                    tm = "tm18";
                    color = "&9";
                    break;
                case "Electric":
                    tm = "tm24";
                    color = "&e";
                    break;
                case "Ground":
                    tm = "tm26";
                    color = "&6";
                    break;
                case "Rock":
                    tm = "tm39";
                    color = "&6";
                    break;
                case "Steal":
                    tm = "tm144";
                    color = "&7";
                    break;
                case "Poison":
                    tm = "tm6";
                    color = "&5";
                    break;
                case "Normal":
                    tm = "tm5";
                    color = "&f";
                    break;
                case "Fighting":
                    tm = "tm8";
                    color = "&4";
                    break;
                case "Flying":
                    tm = "tm62";
                    color = "&b";
                    break;
                case "Grass":
                    tm = "tm22";
                    color = "&a";
                    break;
                case "Psychic":
                    tm = "tm3";
                    color = "&d";
                    break;
                case "Ice":
                    tm = "tm7";
                    color = "&3";
                    break;
                case "Bug":
                    tm = "tm76";
                    color = "&2";
                    break;
            }

            String[] newInfo = PokeUtils.insertLinebreaks(PokeUtils.format(TextFormatting.AQUA, "attack." + name.replaceAll("_", " ").toLowerCase() + ".description", new Object[0]).getFormattedText(), 40).split("\n");

            ArrayList<Text> itemLore = new ArrayList<>();
            for (String s : newInfo) itemLore.add(Text.of(Chat.embedColours("&b" + s)));
            itemLore.add(Text.of(Chat.embedColours("")));
            itemLore.add(Text.of(Chat.embedColours("&7Category: &a" + attackBase.getAttackCategory().getLocalizedName())));
            itemLore.add(Text.of(Chat.embedColours("&7Type: " + color + attackBase.getAttackType().getName())));
            itemLore.add(Text.of(Chat.embedColours("&7Attack Power: &a" + attackBase.getBasePower())));
            itemLore.add(Text.of(Chat.embedColours("&7Accuracy: &a" + attackBase.getAccuracy() + "%")));
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

    private Icon infoIcon(int slot, String info) {
        String[] newInfo = PokeUtils.insertLinebreaks(info,30).split("\n");
        ArrayList<Text> itemLore = new ArrayList<>();
        for(String s:newInfo) itemLore.add(Text.of(Chat.embedColours("&7" + s)));

        return new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.PAPER)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&3&lPok\u00E9Tutor Information")))
                .add(Keys.ITEM_LORE, itemLore)
                .build());
    }


    private static List<String> getAllAttacks(Pokemon pokemon) {
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

    private boolean canLearnOtherMove(Pokemon p, String move) {
        ArrayList<Attack> otherMoves = p.getBaseStats().getTutorMoves();
        otherMoves.addAll(p.getBaseStats().getEggMoves());
        for (Attack a : otherMoves) {
            if (!a.getActualMove().getLocalizedName().equals(move)) continue;
            return true;
        }
        return false;
    }
}
