package com.m1zark.pixeledit.commands;

import com.m1zark.pixeledit.pokebuilder.MainGUI;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class PokeBuilder implements CommandExecutor {
    @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        //MainGUI.showGUI((Player) src);
        ((Player)src).openInventory((new MainGUI((Player)src)).getInventory());

        return CommandResult.success();
    }
}