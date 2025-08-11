package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class LegendaryItemsConfig extends FileConfig {

    @Expose private List<String> custom;

    @Override
    public String getPath() {
        return "legendary_items";
    }

    public List<String> getCustom() {
        return this.custom;
    }

    @Override
    protected void reset() {
        this.custom = new ArrayList<>();
        this.custom.add("test");
    }

}
