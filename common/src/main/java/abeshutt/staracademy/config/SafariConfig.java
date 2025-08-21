package abeshutt.staracademy.config;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.item.SafariTicketEntry;
import abeshutt.staracademy.world.roll.IntRoll;
import com.google.gson.annotations.Expose;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SafariConfig extends FileConfig {

    public static final SafariConfig CLIENT = new SafariConfig();

    @Expose private long seed;
    @Expose private Identifier structure;
    @Expose private BlockPos placementOffset;
    @Expose private BlockPos spawnPositionRelative;
    @Expose private float spawnYaw;
    @Expose private float spawnPitch;
    @Expose private long playerDuration;
    @Expose private int providedSafariBalls;
    @Expose private long startEpoch;
    @Expose private long restartDelay;
    @Expose private boolean paused;
    @Expose private boolean resetWorld;
    @Expose private Map<String, SafariTicketEntry> tickets = new HashMap<>();

    @Override
    public String getPath() {
        return "safari";
    }

    public long getSeed() {
        return this.seed;
    }

    public Identifier getStructure() {
        return this.structure;
    }

    public BlockPos getPlacementOffset() {
        return this.placementOffset;
    }

    public BlockPos getRelativeSpawnPosition() {
        return this.spawnPositionRelative;
    }

    public float getSpawnYaw() {
        return this.spawnYaw;
    }

    public float getSpawnPitch() {
        return this.spawnPitch;
    }

    public long getPlayerDuration() {
        return this.playerDuration;
    }

    public int getProvidedSafariBalls() {
        return this.providedSafariBalls;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public boolean isResetWorld() {
        return this.resetWorld;
    }

    public long getTimeLeft(long lastUpdated) {
        long refreshes = (lastUpdated - this.startEpoch) / this.restartDelay;
        long nextRefresh = this.startEpoch + (refreshes + 1) * this.restartDelay;
        return nextRefresh - System.currentTimeMillis();
    }

    public Map<String, SafariTicketEntry> getTickets() {
        return this.tickets;
    }

    public Optional<SafariTicketEntry> getTicket(String id) {
        return Optional.ofNullable(this.tickets.get(id));
    }

    @Override
    protected void reset() {
        this.resetWorld = false;
        this.seed = -22L;
        this.structure = StarAcademyMod.id("safari_spawn");
        this.placementOffset = new BlockPos(335, 54, 134);
        this.spawnPositionRelative = new BlockPos(102, 3, 78);
        this.spawnYaw = -180.0F;
        this.spawnPitch = 0.0F;
        this.playerDuration = 30 * 60 * 20;
        this.providedSafariBalls = 32;
        this.startEpoch = 25200000;
        this.restartDelay = 1000 * 60 * 60 * 24;
        this.paused = false;

        this.tickets = new LinkedHashMap<>();

        this.tickets.put("base", new SafariTicketEntry("Safari Ticket", 0xFFFFFF,
                IntRoll.ofConstant(20 * 60 * 10), "academy:safari_ticket/base#inventory"));

        this.tickets.put("great", new SafariTicketEntry("Great Safari Ticket", 0xAC3C34,
                IntRoll.ofConstant(20 * 60 * 25), "academy:safari_ticket/great#inventory"));

        this.tickets.put("golden", new SafariTicketEntry("Golden Safari Ticket", 0xD4841C,
                IntRoll.ofUniform(20 * 60 * 40, 20 * 60 * 60), "academy:safari_ticket/golden#inventory"));
    }

}
