package com.m1zark.pixeledit.pokebuilder;

import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.pixeledit.pokebuilder.Stats.*;
import com.m1zark.pixeledit.util.PokeUtils;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EnumSpecialTexture;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.EnumType;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;

public class BuilderGUI extends InventoryManager {
    private Player player;
    public Pokemon pokemon;
    public int totalCost = 0;
    public int level = -1;
    public boolean evReset = false;
    public int[] ivs = new int[]{-1, -1, -1, -1, -1, -1};
    public String[] moves = new String[]{"n/a","n/a","n/a","n/a"};
    public boolean ivsSelected = false;
    public boolean moveLearner = false;
    public String ability = "N/A";
    public String growth = "N/A";
    public String nature = "N/A";
    public String gender = "N/A";
    public String pokeball = "N/A";
    public int happiness = -1;
    public int shiny = -1;
    public boolean aura = false;
    public int form = -1;

    public BuilderGUI(Player player, Pokemon pokemon) {
        super(player, 5, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Editor")));
        this.player = player;
        if(this.totalCost < 0) this.totalCost = 0;
        this.pokemon = pokemon;

        this.setupDesign();
    }

    private void setupDesign() {
        for(int y = 0, x = 0, index = 0; y < 6 && index < 45; ++index) {
            if (x == 9) {
                x = 0;
                ++y;
            }

            this.addIcon(SharedIcons.BorderIcon(x + (9 * y),  DyeColors.GRAY, ""));
            ++x;
        }

        for(int y = 0, x = 3, index = 0; y < 3 && index < 9; ++index) {
            if (x == 6) {
                x = 3;
                ++y;
            }

            this.addIcon(this.BorderIcon(x + (9 * y), this.pokemon.getBaseStats().getType1()));
            ++x;
        }

        PokemonData pkmn = new PokemonData(this.pokemon, this);
        this.addIcon(new Icon(13, pkmn.getSprite("Current Pok\u00E9mon", false, this.player)));

        this.addIcon(SharedIcons.BorderIcon(27, DyeColors.LIGHT_BLUE, ""));
        this.addIcon(SharedIcons.totalCurrencyIcon(36, this.player));
        this.addIcon(SharedIcons.BorderIcon(37, DyeColors.LIGHT_BLUE, ""));

        this.addIcon(SharedIcons.BorderIcon(35, DyeColors.LIME, ""));
        this.addIcon(this.confirmIcon());
        this.addIcon(SharedIcons.BorderIcon(43, DyeColors.LIME, ""));

        this.addIcon(this.levelIcon(this.level != -1));

        for(String name: this.moves) if(!name.equals("n/a")) this.moveLearner = true;
        this.addIcon(this.moveIcon(this.moveLearner));

        this.addIcon(this.abilityIcon(!this.ability.equalsIgnoreCase("N/A")));
        this.addIcon(this.natureIcon(!this.nature.equalsIgnoreCase("N/A")));

        for(int iv: this.ivs) if(iv != -1) this.ivsSelected = true;
        this.addIcon(this.statsIcon(this.evReset || this.ivsSelected));

        this.addIcon(this.genderIcon(!this.gender.equalsIgnoreCase("N/A")));
        this.addIcon(this.growthIcon(!this.growth.equalsIgnoreCase("N/A")));
        this.addIcon(this.shinyIcon(this.shiny != -1));
        this.addIcon(this.pokeballIcon(!this.pokeball.equalsIgnoreCase("N/A")));
        this.addIcon(this.happinessIcon(this.happiness != -1));
        this.addIcon(this.formIcon(this.form != -1));
    }

    public Icon confirmIcon() {
        Icon confirm = SharedIcons.confirmIcon(44, this);
        confirm.addListener(clickable -> {
            if(hasSelections()) {
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.player.openInventory((new ConfirmGUI(this.player, this)).getInventory());
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            }
        });

        return confirm;
    }

    public Icon BorderIcon(int slot, EnumType type) {
        DyeColor color = DyeColors.WHITE;
        switch(type) {
            case Psychic:
                color = DyeColors.MAGENTA; break;
            case Bug:
                color = DyeColors.GREEN; break;
            case Ice:
                color = DyeColors.LIGHT_BLUE; break;
            case Dark:
                color = DyeColors.BLACK; break;
            case Fire:
                color = DyeColors.ORANGE; break;
            case Rock:
                color = DyeColors.SILVER; break;
            case Fairy:
                color = DyeColors.PINK; break;
            case Ghost:
                color = DyeColors.PURPLE; break;
            case Grass:
                color = DyeColors.LIME; break;
            case Steel:
                color = DyeColors.SILVER; break;
            case Water:
                color = DyeColors.CYAN; break;
            case Dragon:
                color = DyeColors.BLUE; break;
            case Flying:
                color = DyeColors.BLUE; break;
            case Ground:
                color = DyeColors.BROWN; break;
            case Normal:
                color = DyeColors.WHITE; break;
            case Poison:
                color = DyeColors.PURPLE; break;
            case Electric:
                color = DyeColors.YELLOW; break;
            case Fighting:
                color = DyeColors.RED; break;
        }

        return new Icon(slot, ItemStack.builder()
                .itemType(ItemTypes.STAINED_GLASS_PANE)
                .quantity(1)
                .add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("")))
                .add(Keys.DYE_COLOR, color)
                .build());
    }


    public Icon levelIcon(boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to modify your Pok\u00E9mons &blevel.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours(this.pokemon.getLevel() == 100 ? "&cYour Pok\u00E9mon is already lvl 100." : "&7Current Selection: &e" + (selected ? "Level 100 Boost" : ""))));

        Icon icon = new Icon(1, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:rare_candy").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Level Editor"))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        if(this.pokemon.getLevel() != 100) {
            icon.addListener(clickable -> {
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.player.openInventory((new LevelGUI(this.player, this)).getInventory());
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            });
        }

        return icon;
    }

    public Icon shinyIcon(boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to modify your Pok\u00E9mons &bshininess.")));
        if (PokeUtils.hasSpecialTexture(this.pokemon)) itemLore.add(Text.of(Chat.embedColours("&cYour Pok\u00E9mon has a special texture. Shininess won't apply.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Current Selection: &e" + (selected ? (this.shiny == 1 ? "Shiny" : this.shiny == 0 ? "Non-Shiny" : "") : ""))));

        Icon icon = new Icon(7, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:shiny_charm").orElse(ItemTypes.NETHER_STAR))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Shininess Editor"))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        if (!PokeUtils.hasSpecialTexture(this.pokemon)) {
            icon.addListener(clickable -> {
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.player.openInventory((new ShinyGUI(this.player, this)).getInventory());
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            });
        }

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        return icon;
    }

    public Icon abilityIcon(boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to modify your Pok\u00E9mons &bability.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Current Selection: &e" + (selected ? this.ability : ""))));

        Icon icon = new Icon(10, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:ability_patch").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Ability Editor"))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        icon.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.player.openInventory((new AbilityGUI(this.player, this)).getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        return icon;
    }

    public Icon moveIcon(boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to edit your Pok\u00E9mons &bmoves.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Current Selection:")));
        for(String name: this.moves) {
            if(!name.equals("n/a")) itemLore.add(Text.of(Chat.embedColours("&e" + name)));
        }

        Icon icon = new Icon(16, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:hm1").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Move Tutor"))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        icon.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.player.openInventory((new MovesGUI(this.player, this)).getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });

        return icon;
    }

    public Icon natureIcon(boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to modify your Pok\u00E9mons &bnature.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Current Selection: &e" + (selected ? this.nature : ""))));

        Icon icon = new Icon(19, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:ever_stone").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Nature Editor"))
                .add(Keys.ITEM_LORE, itemLore)
                .build());
        icon.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                //this.player.closeInventory(Cause.of(NamedCause.source(PixelEdit.getInstance())));
                this.player.openInventory((new NatureGUI(this.player, this)).getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        return icon;
    }

    public Icon statsIcon(boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to modify your Pok\u00E9mons &bIVs/EVs.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Current Selection:")));
        itemLore.add(Text.of(Chat.embedColours("")));
        if(this.evReset){
            itemLore.add(Text.of(Chat.embedColours("&eReset EVs")));
            itemLore.add(Text.of(Chat.embedColours("")));
        }
        for(int i = 0; i < this.ivs.length; i++) {
            if(this.ivs[i] != -1) {
                String stat = i==0 ? "HP" : (i==1 ? "Attack" : (i==2 ? "Defence" : (i==3 ? "Special Attack" : (i==4 ? "Special Defence" : (i==5 ? "Speed" : "")))));
                itemLore.add(Text.of(Chat.embedColours("&e" + stat + ": " + this.ivs[i])));
            }
        }

        Icon icon = new Icon(25, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:destiny_knot").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "IVs/EVs Editor"))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        icon.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.player.openInventory((new IVsEVsGUI(this.player, this)).getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        return icon;
    }

    public Icon genderIcon(boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to modify your Pok\u00E9mons &bgender.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        if(this.pokemon.getBaseStats().getMalePercent() == -1) itemLore.add(Text.of(Chat.embedColours("&cSelected Pok\u00E9mon is genderless.")));
        else if(this.pokemon.getBaseStats().getMalePercent() == 0) itemLore.add(Text.of(Chat.embedColours("&cSelected Pok\u00E9mon can only be female.")));
        else if(this.pokemon.getBaseStats().getMalePercent() == 100) itemLore.add(Text.of(Chat.embedColours("&cSelected Pok\u00E9mon can only be male.")));
        else itemLore.add(Text.of(Chat.embedColours("&7Current Selection: &e" + (selected ? this.gender : ""))));

        Icon icon = new Icon(29, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:rose_incense").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Gender Editor"))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        if (this.pokemon.getGender() != Gender.None && PokeUtils.between((int) this.pokemon.getBaseStats().getMalePercent(), 1, 99)) {
            icon.addListener(clickable -> {
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    this.player.openInventory((new GenderGUI(this.player, this)).getInventory());
                }).delayTicks(1L).submit(PixelEdit.getInstance());
            });
        }

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        return icon;
    }

    public Icon pokeballIcon(boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to modify your Pok\u00E9mons &bcaught ball.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Current Selection: &e" + (selected ? this.pokeball.substring(0, this.pokeball.indexOf("Ball")) + " Ball" : ""))));

        Icon icon = new Icon(33, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:poke_ball").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Pok\u00E9Ball Editor"))
                .add(Keys.ITEM_LORE, itemLore)
                .build());
        icon.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.player.openInventory((new BallGUI(this.player, this, 1)).getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        return icon;
    }

    public Icon growthIcon(boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to modify your Pok\u00E9mons &bgrowth.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Current Selection: &e" + (selected ? this.growth : ""))));

        Icon icon = new Icon(39, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:moomoo_milk").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Growth Editor"))
                .add(Keys.ITEM_LORE, itemLore)
                .build());

        icon.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.player.openInventory((new GrowthGUI(this.player, this)).getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        return icon;
    }

    public Icon formIcon(boolean selected) {
        boolean canClick = true;
        if (Config.getBlacklist().contains(this.pokemon.getSpecies().name)) {
            canClick = false;
        }
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to modify your Pok\u00e9mons &bform.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        if (!canClick) {
            itemLore.add(Text.of(Chat.embedColours("&cSelected Pok\u00e9mon cannot change forms.")));
        } else {
            itemLore.add(Text.of(Chat.embedColours(("&7Current Selection: &e" + (selected ? (this.pokemon.getSpecies().getPossibleForms(false).get(this.form)).getLocalizedName() : "")))));
        }
        Icon icon = new Icon(40, ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:n_lunarizer").orElse(ItemTypes.BARRIER)).add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Form Editor")).add(Keys.ITEM_LORE, itemLore).build());
        if (canClick) {
            icon.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> this.player.openInventory(new FormGUI(this.player, this, 1).getInventory())).delayTicks(1L).submit((Object)PixelEdit.getInstance()));
        }
        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }
        return icon;
    }

    private Icon auraIcon(boolean aura) {
        int auraCost = Config.getPrices("Aura", "remove");
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of(Chat.embedColours("&cThis is a permanent change and cannot be undone.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Left click to remove a Pok\u00e9mons aura effect.")));
        itemLore.add(Text.of(Chat.embedColours(("&7Cost: &a" + auraCost + " " + Config.getItem("currencyName") + (auraCost == 1 ? "" : "(s)")))));
        itemLore.add(Text.of(Chat.embedColours((!this.pokemon.getPersistentData().hasKey("HasAura") ? "&cYour Pok\u00e9mon doesn't have an aura effect." : "&7Current Selection: &e" + (aura ? "Purchased" : "")))));
        itemLore.add(Text.of(Chat.embedColours("")));
        Icon icon = new Icon(40, ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:sachet").orElse(ItemTypes.BARRIER)).add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Aura Cleanser")).add(Keys.ITEM_LORE, itemLore).build());
        if (aura) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }
        icon.addListener(clickable -> {
            if (clickable.getEvent() instanceof ClickInventoryEvent.Primary) {
                if (this.pokemon.getPersistentData().hasKey("HasAura")) {
                    if (aura) {
                        this.totalCost -= auraCost;
                        this.aura = false;
                    } else {
                        this.totalCost += auraCost;
                        this.aura = true;
                    }
                }
            } else if (clickable.getEvent() instanceof ClickInventoryEvent.Secondary) {
                // empty if block
            }
            this.addIcon(this.auraIcon(this.aura));
            this.addIcon(this.confirmIcon());
            this.updateContents();
        });
        return icon;
    }

    public Icon happinessIcon(boolean selected) {
        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(Chat.embedColours("&7Click here to modify your Pok\u00E9mons &bhappiness.")));
        itemLore.add(Text.of(Chat.embedColours("")));
        itemLore.add(Text.of(Chat.embedColours("&7Current Selection: &e" + (selected ? this.happiness : ""))));

        Icon icon = new Icon(41, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:soothe_bell").orElse(ItemTypes.BARRIER))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Happiness Editor"))
                .add(Keys.ITEM_LORE, itemLore)
                .build());
        icon.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.player.openInventory((new HappinessGUI(this.player, this)).getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });

        if (selected) {
            icon.getDisplay().offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.UNBREAKING, 0)));
            icon.getDisplay().offer(Keys.HIDE_ENCHANTMENTS, true);
        }

        return icon;
    }

    public boolean hasSelections() {
        return this.level != -1 || this.shiny != -1 || this.happiness != -1 ||
                this.form != -1 || this.aura || this.evReset || this.ivsSelected || this.moveLearner ||
                !this.pokeball.equalsIgnoreCase("N/A") ||
                !this.growth.equalsIgnoreCase("N/A") ||
                !this.gender.equalsIgnoreCase("N/A") ||
                !this.nature.equalsIgnoreCase("N/A") ||
                !this.ability.equalsIgnoreCase("N/A");
    }
}
