package com.m1zark.pixeledit.commands;

import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.pokebuilder.TutorGUI;
import com.m1zark.pixeledit.util.PokeUtils;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class Tutor implements CommandExecutor {
    @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<Player> player = args.getOne(Text.of("player"));
        Optional<Integer> slot = args.getOne(Text.of("slot"));

        player.ifPresent(player1 -> {
            if(slot.isPresent()) {
                if(slot.get() < 1 || slot.get() > 6){
                    Chat.sendMessage(src, Chat.embedColours("&cSlot number must be between 1 and 6."));
                    return;
                }

                PlayerPartyStorage storage = PokeUtils.getPlayerStorage((EntityPlayerMP) player1);
                if(storage == null) return;

                Pokemon pokemon = storage.get(slot.get() - 1);
                if(pokemon == null) {
                    Chat.sendMessage(src, "&cNothing exists in that slot.");
                    return;
                }

                player1.openInventory((new TutorGUI(player1, 1, pokemon, true)).getInventory());
            }
        });

        return CommandResult.success();
    }
}
