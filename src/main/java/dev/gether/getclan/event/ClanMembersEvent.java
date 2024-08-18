package dev.gether.getclan.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClanMembersEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private List<Player> playersOnline = new ArrayList<>();
    private boolean isCancelled;
    public ClanMembersEvent(List<Player> playersOnline) {
        this.playersOnline = playersOnline;
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

    public List<Player> getPlayersOnline() {
        return playersOnline;
    }

    public void removePlayer(Player player) {
        this.playersOnline.remove(player);
    }
}
