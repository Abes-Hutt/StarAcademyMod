package abeshutt.staracademy.config;

import abeshutt.staracademy.world.data.StarterMode;
import com.google.gson.annotations.Expose;

public class StarterRaffleConfig extends FileConfig {

    @Expose private long timeInterval;
    @Expose private StarterMode mode;
    @Expose private int allocations;

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

    public int getAllocations() {
        return this.allocations;
    }

    @Override
    protected void reset() {
        this.timeInterval = 20 * 60 * 2;
        this.mode = StarterMode.DEFAULT;
        this.allocations = 2;
    }

}
