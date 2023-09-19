package dev.gether.getclans.handler;

import dev.gether.getclans.utils.MessageUtil;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;

public class PermissionMessage implements PermissionHandler<CommandSender> {

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        MessageUtil.sendMessage(sender, "&cNie masz permisji do tej komendy! &7(" + String.join(", ", requiredPermissions.getPermissions()) + ")");
    }

}