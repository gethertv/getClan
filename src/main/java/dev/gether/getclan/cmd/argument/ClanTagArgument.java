package dev.gether.getclan.cmd.argument;

import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.manager.ClanManager;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.role.Owner;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

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
            return ParseResult.failure(fileManager.getLangConfig().getMessage("clan-does-not-exist"));
        }
        return ParseResult.success(clan);
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Clan> argument, SuggestionContext context) {
        return SuggestionResult.of(
                this.clansManager.getClansData().keySet()
                                .stream()
                                .filter(tag -> tag.toUpperCase().startsWith(argument.getName().toUpperCase()))
                                .sorted()
                                .limit(5)
                                .collect(Collectors.toList())
        );
    }

}

