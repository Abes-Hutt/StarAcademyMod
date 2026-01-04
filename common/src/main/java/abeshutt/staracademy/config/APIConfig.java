package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;

public class APIConfig extends FileConfig {

    @Expose private String liveUrl;

    @Override
    public String getPath() {
        return "api";
    }

    public String getLiveUrl() {
        return this.liveUrl;
    }

    @Override
    protected void reset() {
        this.liveUrl = "ws://localhost:6969/";
    }

}

