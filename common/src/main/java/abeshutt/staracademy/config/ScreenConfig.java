package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;
import dev.architectury.platform.Platform;

public class ScreenConfig extends FileConfig {

    @Expose private boolean enabled;
    @Expose private boolean titleButtons;
    @Expose private boolean titleLogo;

    @Override
    public String getPath() {
        return "screen";
    }

    public boolean isEnabled() {
        return this.enabled || Platform.isDevelopmentEnvironment();
    }

    public boolean hasTitleButtons() {
        return this.titleButtons || Platform.isDevelopmentEnvironment();
    }

    public boolean hasTitleLogo() {
        return this.titleLogo || Platform.isDevelopmentEnvironment();
    }

    @Override
    protected void reset() {
        this.enabled = false;
        this.titleButtons = false;
        this.titleLogo = false;
    }

}
