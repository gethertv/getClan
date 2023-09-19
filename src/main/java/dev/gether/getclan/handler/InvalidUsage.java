package dev.gether.getclan.handler;

import dev.gether.getclan.config.Config;
import dev.gether.getclan.utils.MessageUtil;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;

import java.util.List;

public class InvalidUsage implements InvalidUsageHandler<CommandSender> {

    private Config config;
    public InvalidUsage(Config config)
    {
        this.config = config;
    }
    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, Schematic schematic) {
        List<String> schematics = schematic.getSchematics();
        if (schematics.size() == 1) {
            String first = schematic.first();
            String[] split = first.split("\\|");
            if(split.length<=1)
            {
                MessageUtil.sendMessage(sender, config.langUsageCmd.replace("{usage}", first));
                return;
            }
        }
        MessageUtil.sendMessage(sender, config.langUsageList);
    }

}
