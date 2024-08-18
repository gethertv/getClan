package dev.gether.getclan.handler;

import dev.gether.getclan.config.FileManager;
import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;

public class PermissionMessage implements MissingPermissionsHandler<CommandSender> {

    private final FileManager fileManager;

    public PermissionMessage(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions, ResultHandlerChain<CommandSender> resultHandlerChain) {
        MessageUtil.sendMessage(invocation.sender(), fileManager.getLangConfig().getMessage("no-permission")
                .replace("{permission}", String.join(", ", missingPermissions.getPermissions()))
        );
    }
}