package dev.gether.getclan.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerNameEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private String playerName;
    private final Player player;
    private boolean isCancelled;
    public PlayerNameEvent(Player player) {
        this.playerName = player.getName();
        this.player = player;
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

    public String getPlayerName() {
        return playerName;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
