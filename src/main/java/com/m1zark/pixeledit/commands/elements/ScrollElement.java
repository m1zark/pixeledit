package com.m1zark.pixeledit.commands.elements;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScrollElement extends CommandElement {
    public ScrollElement(Text key) {
        super(key);
    }

    @Nullable @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        return commandArgs.next();
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        return new ArrayList<>(Arrays.asList("ivs","gender","sizeG","sizeM","shiny","ha","lvl","moves","happiness","evs"));
    }
}
