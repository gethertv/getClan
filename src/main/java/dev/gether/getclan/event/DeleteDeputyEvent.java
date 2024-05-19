package dev.gether.getclan.event;

import dev.gether.getclan.core.clan.Clan;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DeleteDeputyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Clan clan;

    private final UUID removedDeputyUUID;
    private boolean isCancelled;
    public DeleteDeputyEvent(Clan clan, UUID removedDeputyUUID) {
        this.clan = clan;
        this.removedDeputyUUID = removedDeputyUUID;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public UUID getRemovedDeputyUUID() {
        return removedDeputyUUID;
    }

    public Clan getClan() {
        return clan;
    }

}
