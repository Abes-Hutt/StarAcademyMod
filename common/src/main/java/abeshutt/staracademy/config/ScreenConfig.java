package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;
import dev.architectury.platform.Platform;

public class ScreenConfig extends FileConfig {

    @Expose private boolean enabled;

    @Override
    public String getPath() {
        return "screen";
    }

    public boolean isEnabled() {
        return this.enabled || Platform.isDevelopmentEnvironment();
    }

    @Override
    protected void reset() {
        this.enabled = false;
    }

}
