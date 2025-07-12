package abeshutt.staracademy.config;

import abeshutt.staracademy.world.data.StarterMode;
import com.google.gson.annotations.Expose;

public class StarterRaffleConfig extends FileConfig {

    @Expose private long timeInterval;
    @Expose private StarterMode mode;
    @Expose private int selectionCooldown;

    @Override
    public String getPath() {
        return "starter_raffle";
    }

    public long getTimeInterval() {
        return this.timeInterval;
    }

    public StarterMode getMode() {
        return this.mode;
    }

    public int getSelectionCooldown() {
        return this.selectionCooldown;
    }

    @Override
    protected void reset() {
        this.timeInterval = 20 * 60 * 2;
        this.mode = StarterMode.DEFAULT;
        this.selectionCooldown = 2;
    }

}
