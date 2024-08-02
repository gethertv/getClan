package dev.gether.getclan.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerNameEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private String playerName;
    private UUID uuid;
    private boolean isCancelled;
    public PlayerNameEvent(String playerName, UUID uuid) {
        this.playerName = playerName;
        this.uuid = uuid;
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

    public UUID getUuid() {
        return uuid;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
