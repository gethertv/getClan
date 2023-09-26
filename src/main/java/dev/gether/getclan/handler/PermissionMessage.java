package dev.gether.getclan.handler;

import dev.gether.getclan.config.Config;
import dev.gether.getclan.config.lang.LangMessage;
import dev.gether.getclan.utils.MessageUtil;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;

public class PermissionMessage implements PermissionHandler<CommandSender> {

    private LangMessage lang;
    public PermissionMessage(LangMessage lang)
    {
        this.lang = lang;
    }
    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        MessageUtil.sendMessage(sender, lang.langNoPermission
                .replace("{permission}", String.join(", ", requiredPermissions.getPermissions()))
        );
    }

}