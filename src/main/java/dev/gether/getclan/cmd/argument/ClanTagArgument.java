package dev.gether.getclan.cmd.argument;

import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getconfig.utils.ColorFixer;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class ClanTagArgument extends ArgumentResolver<CommandSender, Clan> {

    private final ClanManager clansManager;
    private final FileManager fileManager;

    public ClanTagArgument(ClanManager clansManager, FileManager fileManager) {
        this.clansManager = clansManager;
        this.fileManager = fileManager;
    }



    @Override
    protected ParseResult<Clan> parse(Invocation<CommandSender> invocation, Argument<Clan> context, String argument) {
        Clan clan = clansManager.getClan(argument);
        if(clan == null) {
            return ParseResult.failure(ColorFixer.addColors(fileManager.getLangConfig().getMessage("clan-does-not-exist")));
        }
        return ParseResult.success(clan);
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Clan> argument, SuggestionContext context) {
        List<String> sortedTags = this.clansManager.getClansData().keySet().stream()
                .sorted()
                .limit(5)
                .toList();

        return SuggestionResult.of(sortedTags);
    }



}

