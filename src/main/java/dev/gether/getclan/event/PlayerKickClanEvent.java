package dev.gether.getclan.event;

import dev.gether.getclan.model.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerKickClanEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player kickedPlayer;
    private final Clan clan;
    private boolean isCancelled;
    public PlayerKickClanEvent(Clan clan, Player kickedPlayer) {
        this.kickedPlayer = kickedPlayer;
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

    public Player getKickedPlayer() {
        return kickedPlayer;
    }

    public Clan getClan() {
        return clan;
    }
}
