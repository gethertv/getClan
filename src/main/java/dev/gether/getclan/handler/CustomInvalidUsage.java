package dev.gether.getclan.handler;

import dev.gether.getclan.config.FileManager;
import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;

public class CustomInvalidUsage implements InvalidUsageHandler<CommandSender> {

    private final FileManager fileManager;

    public CustomInvalidUsage(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> resultHandlerChain) {
        Schematic schematic = result.getSchematic();
        if (schematic.isOnlyFirst()) {
            MessageUtil.sendMessage(invocation.sender(), fileManager.getLangConfig().getMessage("usage-cmd").replace("{usage}", schematic.first()));
            return;
        }
        MessageUtil.sendMessage(invocation.sender(), fileManager.getLangConfig().getMessage("usage-list"));
    }
}
