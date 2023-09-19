package dev.gether.getclan.cmd.argument;

import dev.gether.getclan.config.Config;
import dev.gether.getclan.manager.ClanManager;
import dev.gether.getclan.model.Clan;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import panda.std.Option;
import panda.std.Result;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ArgumentName("clan")
public class ClanTagArgument implements OneArgument<Clan> {

    private final ClanManager clansManager;


    private Config config;

    public ClanTagArgument(Config config, ClanManager clansManager) {
        this.config = config;
        this.clansManager = clansManager;
    }

    @Override
    public Result<Clan, Object> parse(LiteInvocation invocation, String argument) {
        return Option.of(this.clansManager.getClan(argument))
                .toResult(config.langClanNotExists);
    }
    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return invocation.lastArgument()
                .map(text -> this.clansManager.getClansData().keySet()
                        .stream()
                        .filter(tag -> tag.toUpperCase().startsWith(text.toUpperCase()))
                        .sorted()
                        .limit(5)
                        .map(Suggestion::of)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

}
