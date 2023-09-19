package dev.gether.getclans.handler;

import dev.gether.getclans.utils.MessageUtil;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class InvalidUsage implements InvalidUsageHandler<CommandSender> {

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, Schematic schematic) {
        List<String> schematics = schematic.getSchematics();

        if (schematics.size() == 1) {
            MessageUtil.sendMessage(sender, "&cNie poprawne użycie komendy &8>> &7" + schematics.get(0));
            return;
        }
        MessageUtil.sendMessage(sender, "&cNie poprawne użycie komendy!");
        for (String sch : schematics) {
            MessageUtil.sendMessage(sender, "&8 >> &7" + sch);
        }
    }

}
