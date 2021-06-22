package com.m1zark.pixeledit.commands;

import com.m1zark.m1utilities.api.Inventories;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.Text;

import com.m1zark.pixeledit.util.PokeUtils;

import java.util.Optional;

public class GiveItem implements CommandExecutor {
    @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> type = args.getOne(Text.of("type"));
        Optional<Player> player = args.getOne(Text.of("player"));
        Optional<Integer> quantity = args.getOne(Text.of("quantity"));

        player.ifPresent(p -> {
            int q = quantity.orElse(1);

            if(type.isPresent()) {
                ItemStack ItemStack = PokeUtils.getScroll(type.get());
                if (!Inventories.giveItem(p, ItemStack, q)) {
                    src.sendMessage(Text.of("Couldn't give item to " + p.getName() + " because of a full inventory and enderchest"));
                }
            }
        });

        return CommandResult.success();
    }
}
