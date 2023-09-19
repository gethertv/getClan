package dev.gether.getclan.event;

import dev.gether.getclan.model.Clan;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DisbandAllianceEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Clan firstClan;
    private final Clan secondClan;
    private boolean isCancelled;
    public DisbandAllianceEvent(Clan firstClan, Clan secondClan) {
        this.firstClan = firstClan;
        this.secondClan = secondClan;
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


    public Clan getFirstClan() {
        return firstClan;
    }

    public Clan getSecondClan() {
        return secondClan;
    }

}
