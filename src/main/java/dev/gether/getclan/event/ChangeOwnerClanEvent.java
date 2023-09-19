package dev.gether.getclan.event;

import dev.gether.getclan.model.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


public class ChangeOwnerClanEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Clan clan;
    private final Player previousOwner;
    private final Player newOwner;
    private boolean isCancelled;
    public ChangeOwnerClanEvent(Clan clan, Player previousOwner, Player newOwner) {
        this.clan = clan;
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
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

    public Player getNewOwner() {
        return newOwner;
    }

    public Player getPreviousOwner() {
        return previousOwner;
    }
}
