package dev.gether.getclan.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerInfoMessageEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private String message;
    private boolean isCancelled;
    public PlayerInfoMessageEvent(Player player, String message) {
        this.player = player;
        this.message = message;
        this.isCancelled = false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Player getPlayer() {
        return player;
    }

}
