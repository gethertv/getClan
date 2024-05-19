package dev.gether.getclan.event;

import dev.gether.getclan.core.clan.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CancelInviteClanEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player invitedPlayer;
    private final Clan clan;
    private boolean isCancelled;
    public CancelInviteClanEvent(Clan clan, Player invitedPlayer) {
        this.invitedPlayer = invitedPlayer;
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

    public Player getInvitedPlayer() {
        return invitedPlayer;
    }

    public Clan getClan() {
        return clan;
    }
}
