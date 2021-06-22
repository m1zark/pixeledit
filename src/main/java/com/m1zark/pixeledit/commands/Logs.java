package com.m1zark.pixeledit.commands;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.pixeledit.PixelEdit;
import com.m1zark.pixeledit.util.Log.Log;
import com.m1zark.pixeledit.util.configuration.Config;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import java.sql.SQLException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Logs implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) throw new CommandException(Text.of(Chat.embedColours("You need to be ingame to run this command.")));

        removeExpired();

        List<Text> texts = Lists.newArrayList();
        List<Log> list;
        Optional<User> player =  args.getOne("player");

        if(player.isPresent()) {
            list = PixelEdit.getInstance().getSql().getAllLogs().stream()
                    .filter(name -> name.getOwner().equals(player.get().getUniqueId()))
                    .collect(Collectors.toList());
        } else {
            list = PixelEdit.getInstance().getSql().getAllLogs();
        }

        if (list.isEmpty()) {
            src.sendMessage(Text.of(Chat.embedColours("&cThere are no current transaction logs.")));
            return CommandResult.empty();
        }

        for (Log log : list) {
            Text text = Text.of(
                    "[", TextColors.GREEN, log.getOwnerName(), TextColors.WHITE, " @ ", TextColors.AQUA, log.getTimeStamp(), TextColors.WHITE, "]", " ",
                    Text.builder(btn("View")).color(TextColors.GREEN).onHover(TextActions.showText(Text.of(Chat.embedColours(viewData(log))))).onClick(TextActions.executeCallback(s -> showLog(log, s))), " ",
                    Text.builder(btn("Delete")).color(TextColors.RED).onHover(TextActions.showText(Text.of(TextColors.RED,"Click here to delete this log."))).onClick(TextActions.executeCallback(s -> deleteLog(log, s)))
            );
            texts.add(text);
        }

        PaginationList.builder().contents(texts).linesPerPage(10).title(Text.of("PokeBuilder Logs")).build().sendTo(src);

        return CommandResult.builder().successCount(list.size()).build();
    }

    private void showLog(Log log, CommandSource src) {
        src.sendMessages(
                Text.of(Chat.embedColours(getWithArgs(ImmutableMap.of("player", log.getOwnerName(), "timestamp", log.getTimeStamp())))),
                Text.of(Chat.embedColours("&7" + log.getPurchaseInfo()))
        );
    }

    private void deleteLog(Log log, CommandSource src) {
        PixelEdit.getInstance().getSql().deleteLog(log);
        src.sendMessage(Text.of(TextColors.RED, "Log deleted."));
    }

    private String btn(String button) {
        return "[" + button + "]";
    }

    private String viewData(Log log) {
        return "&bPlayer: &e" + log.getOwnerName() + "\n" +
                "&bTime: &e" + log.getTimeStamp() + "\n" +
                "&bInfo: &e" + "\n" +
                log.getPurchaseInfo();
    }

    private String getWithArgs(Map<String, String> replace) {
        String lang = "&a<player> &f@ &b<timestamp>:";
        for (Map.Entry<String, String> entry : replace.entrySet()) {
            lang = lang.replace("<"+entry.getKey()+">", entry.getValue());
        }
        return lang;
    }

    private void removeExpired() {
        List<Log> list = PixelEdit.getInstance().getSql().getAllLogs();
        list.forEach(log -> {
            SimpleDateFormat ft = new SimpleDateFormat("MMMMM d yyyy h:mm a z");
            try {
                Date date = new Date(ft.parse(log.getTimeStamp()).getTime());
                long daysElapsed = ChronoUnit.DAYS.between(date.toInstant() , Instant.now());

                if(daysElapsed >= Config.getexpireLogs()) {
                    PixelEdit.getInstance().getSql().deleteLog(log);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }
}
