package com.m1zark.pixeledit.commands;

import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.pixeledit.PEInfo;
import com.m1zark.pixeledit.commands.elements.ScrollElement;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {
    public void registerCommands(PixelEdit plugin) {
        Sponge.getCommandManager().register(plugin, scrollSpec, "scrolls");
        Sponge.getCommandManager().register(plugin, tutorSpec, "tutor");
        Sponge.getCommandManager().register(plugin, builderSpec, "pokebuilder", "pb");

        PixelEdit.getInstance().getConsole().ifPresent(console -> console.sendMessages(Text.of(PEInfo.PREFIX, "Registering commands...")));
    }

    CommandSpec giveScroll = CommandSpec.builder()
            .permission("pixeledit.command.scrolls.give")
            .description(Text.of("Gives players a scroll to edit various pokemon stats."))
            .arguments(
                    GenericArguments.playerOrSource(Text.of("player")),
                    new ScrollElement(Text.of("type")),
                    GenericArguments.optional(GenericArguments.integer(Text.of("quantity")))
            )
            .executor(new GiveItem())
            .build();

    CommandSpec scrollSpec = CommandSpec.builder()
            .description(Text.of("PokeScrolls base command."))
            .child(giveScroll, "give")
            .build();

    CommandSpec tutorSpec = CommandSpec.builder()
            .permission("pixeledit.command.tutor")
            .description(Text.of("Brings up the move tutor GUI for the specified player and pokemon."))
            .arguments(
                    GenericArguments.playerOrSource(Text.of("player")),
                    GenericArguments.integer(Text.of("slot"))
            )
            .executor(new Tutor())
            .build();

    CommandSpec giveCrystal = CommandSpec.builder()
            .permission("pokebuilder.command.gui.give")
            .description(Text.of("Gives player currency to use on the pokebuilder."))
            .arguments(
                    GenericArguments.playerOrSource(Text.of("player")),
                    GenericArguments.optional(GenericArguments.integer(Text.of("quantity")))
            )
            .executor(new GiveBuilderItem())
            .build();

    CommandSpec reload = CommandSpec.builder()
            .permission("pokebuilder.command.gui.reload")
            .description(Text.of("Reload the pokebuilder config file"))
            .executor(new Reload())
            .build();

    CommandSpec log = CommandSpec.builder()
            .permission("pokebuilder.command.gui.logs")
            .arguments(GenericArguments.optional(GenericArguments.user(Text.of("player"))))
            .description(Text.of("View transaction logs"))
            .executor(new Logs())
            .build();

    CommandSpec builderSpec = CommandSpec.builder()
            .permission("pokebuilder.command.gui.use")
            .description(Text.of("Brings up the pokebuilder GUI interface"))
            .child(giveCrystal, "give")
            .child(reload, "reload")
            .child(log, "viewlogs")
            .executor(new PokeBuilder())
            .build();
}
