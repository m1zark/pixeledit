package com.m1zark.pixeledit.commands;

import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.PixelEdit;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class Reload implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        PixelEdit.getInstance().getConfig().reload();

        Chat.sendMessage(src, "&7PokeBuilder config successfully reloaded.");

        return CommandResult.success();
    }
}