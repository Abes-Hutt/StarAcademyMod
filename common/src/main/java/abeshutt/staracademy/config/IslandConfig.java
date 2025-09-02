package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;

public class IslandConfig extends FileConfig {

    @Expose private boolean enabled;

    @Override
    public String getPath() {
        return "island";
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    protected void reset() {
        this.enabled = false;
    }

}
