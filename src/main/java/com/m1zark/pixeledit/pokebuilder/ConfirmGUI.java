package com.m1zark.pixeledit.pokebuilder;

import com.google.common.collect.Maps;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.Inventories;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.pixeledit.util.PokeUtils;
import com.m1zark.pixeledit.util.configuration.Config;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.EnumSpecialTexture;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.*;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import de.waterdu.aquaauras.auras.AuraStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.function.Consumer;

public class ConfirmGUI extends InventoryManager {
    private Player player;
    private BuilderGUI base;
    private int balance;

    public ConfirmGUI(Player player, BuilderGUI base) {
        super(player, 5, Text.of(Chat.embedColours("&4&lPok\u00E9Builder &l&0\u27A5&r &8Confirmation")));
        this.player = player;
        this.base = base;
        this.balance = Inventories.getItemCount(player, PokeUtils.currencyItem());

        setupDesign();
    }

    private void setupDesign() {
        int x = 0;
        int index = 0;

        for(int y = 0; y < 6 && index < 45; ++index) {
            if (x == 9) {
                x = 0;
                ++y;
            }

            this.addIcon(SharedIcons.BorderIcon(x + (9 * y),  DyeColors.GRAY, ""));
            ++x;
        }

        PokemonData pkmn = new PokemonData(this.base.pokemon, this.base);
        this.addIcon(new Icon(12, pkmn.getSprite("Current Pok\u00E9mon", false, this.player)));
        this.addIcon(new Icon(14, pkmn.getSprite("Updated Pok\u00E9mon", true, this.player)));

        Icon confirm = SharedIcons.confirmFinalIcon(29, this.player, this.base);
        confirm.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                if (this.balance - this.base.totalCost >= 0 || PixelEdit.getInstance().getDevs().contains(player.getUniqueId())) {
                    this.updatePokemon(this.player);
                    Inventories.removeItem(this.player, PokeUtils.currencyItem(), this.base.totalCost);
                    Chat.sendMessage(this.player, "&7Congratulations! Your &a" + this.base.pokemon.getDisplayName() + " &7was successfully updated!");

                    Task.builder().execute(new ConfettiTask()).intervalTicks(4L).name("ConfettiBorder- - " + this.player.getName()).submit((Object)PixelEdit.getInstance());
                } else {
                    Chat.sendMessage(this.player, "&cYou don't have enough " + Config.getItem("currencyName") + " to complete this purchase.");
                }
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(confirm);

        this.addIcon(SharedIcons.totalCurrencyIcon(31, this.player));

        Icon back = SharedIcons.backIcon(33);
        back.addListener(clickable -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.player.openInventory(this.base.getInventory());
            }).delayTicks(1L).submit(PixelEdit.getInstance());
        });
        this.addIcon(back);
    }

    @Override
    protected void processClose(InteractInventoryEvent.Close event) {
        Sponge.getScheduler().getTasksByName("ConfettiBorder-" + player.getName()).forEach(Task::cancel);
    }

    private class ConfettiTask implements Consumer<Task> {
        private int seconds = 20;
        @Override
        public void accept(Task task) {
            seconds--;
            clearIcons(2,3,4,5,6,11,13,15,20,21,22,23,24);
            setupConfettiBorder();
            updateContents(2,3,4,5,6,11,13,15,20,21,22,23,24);

            if (seconds < 1) {
                player.closeInventory();
                task.cancel();
            }
        }
    }

    private void setupConfettiBorder() {
        this.addIcon(SharedIcons.confettiBorderIcon(2));
        this.addIcon(SharedIcons.confettiBorderIcon(3));
        this.addIcon(SharedIcons.confettiBorderIcon(4));
        this.addIcon(SharedIcons.confettiBorderIcon(5));
        this.addIcon(SharedIcons.confettiBorderIcon(6));

        this.addIcon(SharedIcons.confettiBorderIcon(11));
        this.addIcon(SharedIcons.confettiBorderIcon(13));
        this.addIcon(SharedIcons.confettiBorderIcon(15));

        this.addIcon(SharedIcons.confettiBorderIcon(20));
        this.addIcon(SharedIcons.confettiBorderIcon(21));
        this.addIcon(SharedIcons.confettiBorderIcon(22));
        this.addIcon(SharedIcons.confettiBorderIcon(23));
        this.addIcon(SharedIcons.confettiBorderIcon(24));
    }


    private void updatePokemon(@First Player p)  {
        Pokemon pokemon = this.base.pokemon;

        HashMap<String, String> options = Maps.newHashMap();
        options.put("Cost", String.valueOf(this.base.totalCost));
        options.put("Pok\u00E9mon", "&e" + pokemon.getDisplayName());

        if(!this.base.pokeball.equalsIgnoreCase("n/a")) {
            options.put("Pokeball", "&e" + pokemon.getCaughtBall().name() + " &f-> " + "&b" + this.base.pokeball);
            pokemon.setCaughtBall(EnumPokeballs.valueOf(this.base.pokeball));
        } else {
            options.put("Pokeball", "&e" + pokemon.getCaughtBall().name());
        }

        if(this.base.level != -1) {
            options.put("Level", "&e" + pokemon.getLevel() + " &f-> " + "&b" + 100);
            pokemon.setLevel(this.base.level);
        } else {
            options.put("Level", "&e" + pokemon.getLevel());
        }

        if(this.base.shiny != -1) {
            options.put("Shiny", "&e" + pokemon.isShiny() + " &f-> " + "&b" + Boolean.getBoolean(String.valueOf(this.base.shiny)));
            if(this.base.shiny == 1) pokemon.setShiny(true);
            if(this.base.shiny == 0) pokemon.setShiny(false);
        } else {
            options.put("Shiny", "&e" + pokemon.isShiny());
        }

        if(!this.base.nature.equalsIgnoreCase("n/a")) {
            options.put("Nature", "&e" + pokemon.getNature().name() + " &f-> " + "&b" + this.base.nature);
            pokemon.setNature(EnumNature.natureFromString(this.base.nature));
        } else {
            options.put("Nature", "&e" + pokemon.getNature().name());
        }

        if(!this.base.growth.equalsIgnoreCase("n/a")) {
            options.put("Growth", "&e" + pokemon.getGrowth().name() + " &f-> " + "&b" + this.base.growth);
            pokemon.setGrowth(EnumGrowth.growthFromString(this.base.growth));
        } else {
            options.put("Growth", "&e" + pokemon.getGrowth().name());
        }

        if(!this.base.gender.equalsIgnoreCase("n/a")) {
            options.put("Gender", "&e" + pokemon.getGender().name() + " &f-> " + "&b" + this.base.gender);
            if(this.base.gender.equalsIgnoreCase("male")) pokemon.setGender(Gender.Male);
            if(this.base.gender.equalsIgnoreCase("female")) pokemon.setGender(Gender.Female);
        } else {
            options.put("Gender", "&e" + pokemon.getGender().name());
        }

        if(!this.base.ability.equalsIgnoreCase("n/a")) {
            options.put("Ability", "&e" + pokemon.getAbility().getName() + " &f-> " + "&b" + this.base.ability);

            if(this.base.ability.equalsIgnoreCase("Battle Bond")) {
                pokemon.setForm(1);
            } else {
                pokemon.setAbility(this.base.ability);
            }
        } else {
            options.put("Ability", "&e" + pokemon.getAbility().getName());
        }

        if(this.base.happiness != -1) {
            options.put("Happiness", "&e" + pokemon.getFriendship() + " &f-> " + "&b" + this.base.happiness);
            pokemon.setFriendship(this.base.happiness);
        } else {
            options.put("Happiness", "&e" + pokemon.getFriendship());
        }

        if(this.base.evReset) {
            pokemon.getEVs().setStat(StatsType.Attack, 0);
            pokemon.getEVs().setStat(StatsType.Defence, 0);
            pokemon.getEVs().setStat(StatsType.SpecialAttack, 0);
            pokemon.getEVs().setStat(StatsType.SpecialDefence, 0);
            pokemon.getEVs().setStat(StatsType.HP, 0);
            pokemon.getEVs().setStat(StatsType.Speed, 0);
            options.put("EVs", "&eReset");
        }

        if(this.base.ivsSelected) {
            IVStore ivs = pokemon.getIVs();
            StatsType[] stats = new StatsType[]{StatsType.HP, StatsType.Attack, StatsType.Defence, StatsType.SpecialAttack, StatsType.SpecialDefence, StatsType.Speed};
            StringBuilder iv = new StringBuilder();

            for(int i = 0; i < this.base.ivs.length; i++) {
                if (this.base.ivs[i] != -1) {
                    iv.append("&d" + stats[i].getLocalizedName() + "&f: " + "&e" + pokemon.getIVs().get(stats[i]) + "&f->" + "&b" + this.base.ivs[i] + " ");
                    pokemon.getIVs().CopyIVs(addToIvs(ivs, stats[i], this.base.ivs[i]));
                } else {
                    iv.append("&d" + stats[i].getLocalizedName() + "&f: " + "&e" + pokemon.getIVs().get(stats[i]) + " ");
                }
            }
            options.put("IVs", iv.toString());
        }

        if (this.base.form != -1) {
            options.put("Form", "&eChanged");
            pokemon.setForm(PokeUtils.getAllForms(pokemon).get(this.base.form));
        }

        if (this.base.aura) {
            AuraStorage auras = new AuraStorage(pokemon.getPersistentData());
            auras.clearAuras((EntityPlayerMP) player, pokemon.getPixelmonIfExists());
        }

        if(this.base.moveLearner) {
            StringBuilder moves = new StringBuilder();
            for(int i = 0; i < this.base.moves.length; i++) {
                if(!this.base.moves[i].equalsIgnoreCase("n/a")) {
                    moves.append("&dMove #" + i + "&f: " + "&e" + (pokemon.getMoveset().get(i) != null ? pokemon.getMoveset().get(i).getActualMove().getLocalizedName() : "Empty") + "&f->" + "&b" + this.base.moves[i] + " ");

                    AttackBase attack = AttackBase.getAttackBase(this.base.moves[i].replaceAll("_", " ")).orElse(null);
                    if(attack != null) pokemon.getMoveset().set(i, new Attack(attack));
                }
            }
            options.put("Moves", moves.toString());
        }

        pokemon.getStats().setLevelStats(pokemon.getNature(), pokemon.getBaseStats(), pokemon.getLevel());

        if(!PixelEdit.getInstance().getDevs().contains(player.getUniqueId())) this.createLog(options, p);
        displayEffects(p);
    }

    private IVStore addToIvs(IVStore ivS, StatsType stat, int i) {
        if (stat == StatsType.Attack) {
            ivS.attack = i;
        } else if (stat == StatsType.Defence) {
            ivS.defence = i;
        } else if (stat == StatsType.HP) {
            ivS.hp = i;
        } else if (stat == StatsType.SpecialAttack) {
            ivS.specialAttack = i;
        } else if (stat == StatsType.SpecialDefence) {
            ivS.specialDefence = i;
        } else if (stat == StatsType.Speed) {
            ivS.speed = i;
        }

        return ivS;
    }

    private void displayEffects(Player p) {
        p.playSound(SoundTypes.BLOCK_ENCHANTMENT_TABLE_USE, p.getLocation().getPosition(), 1);
    }

    private void createLog(HashMap log, Player player) {
        PixelEdit.getInstance().getSql().addLog(log, player);
    }
}
