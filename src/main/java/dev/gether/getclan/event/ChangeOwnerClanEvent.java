package dev.gether.getclan.event;

import dev.gether.getclan.model.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class ChangeOwnerClanEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Clan clan;
    private final UUID previousOwnerUUID;
    private final UUID newOwnerUUID;
    private boolean isCancelled;
    public ChangeOwnerClanEvent(Clan clan, UUID previousOwnerUUID, UUID newOwnerUUID) {
        this.clan = clan;
        this.previousOwnerUUID = previousOwnerUUID;
        this.newOwnerUUID = newOwnerUUID;
        this.isCancelled = false;
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

    public Clan getClan() {
        return clan;
    }

    public UUID getPreviousOwnerUUID() {
        return previousOwnerUUID;
    }

    public UUID getNewOwnerUUID() {
        return newOwnerUUID;
    }
}
