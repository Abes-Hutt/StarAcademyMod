package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;

public class DuelingConfig extends FileConfig {

    @Expose private int cooldownTicks;

    @Override
    public String getPath() {
        return "dueling";
    }

    public int getCooldownTicks() {
        return this.cooldownTicks;
    }

    @Override
    protected void reset() {
        this.cooldownTicks = 20 * 60 * 10;
    }

}
