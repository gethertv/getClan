package dev.gether.getclan.cmd.argument;

import dev.gether.getclan.config.Config;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.User;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.Result;

import java.util.List;
import java.util.stream.Collectors;

@ArgumentName("user")
public class UserArgument implements OneArgument<User> {

    private final UserManager userManager;
    private Config config;

    public UserArgument(Config config, UserManager userManager) {
        this.config = config;
        this.userManager = userManager;
    }

    @Override
    public Result<User, Object> parse(LiteInvocation invocation, String argument) {
        Player player = Bukkit.getPlayer(argument);
        if(player==null)
        {
            return Result.error(config.langPlayerNotOnline);
        }

        User user = userManager.getUserData().get(player.getUniqueId());
        return Result.ok(user);
    }
    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return Bukkit.getOnlinePlayers().stream()
                .map(user -> user.getName())
                .map(Suggestion::of)
                .collect(Collectors.toList());
    }

}
