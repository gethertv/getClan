package dev.gether.getclans.event;

import dev.gether.getclans.model.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class JoinClanEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player joiningPlayer;
    private final Clan clan;
    private boolean isCancelled;
    public JoinClanEvent(Clan clan, Player joiningPlayer) {
        this.joiningPlayer = joiningPlayer;
        this.clan = clan;
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

    public Player getJoiningPlayer() {
        return joiningPlayer;
    }

    public Clan getClan() {
        return clan;
    }
}
