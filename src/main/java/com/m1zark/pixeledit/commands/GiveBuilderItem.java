package com.m1zark.pixeledit.commands;

import com.m1zark.m1utilities.api.Inventories;
import com.m1zark.pixeledit.util.PokeUtils;
import com.m1zark.pixeledit.util.configuration.Config;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;

public class GiveBuilderItem implements CommandExecutor {
    @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<Player> player = args.getOne(Text.of("player"));
        player.ifPresent(player1 -> {
            Optional<Integer> quantity = args.getOne(Text.of("quantity"));
            int q = quantity.orElse(1);

            ItemStack ItemStack = PokeUtils.currencyItem();
            if(!Inventories.giveItem(player1, ItemStack, q)) {
                src.sendMessage(Text.of("Couldn't give " + TextSerializers.FORMATTING_CODE.stripCodes(Config.getItem("itemName")) + " to " + player1.getName() + " because of a full inventory."));
            }
        });

        return CommandResult.success();
    }
}
