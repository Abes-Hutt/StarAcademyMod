package abeshutt.staracademy.config;

import abeshutt.staracademy.item.data.outfit.OutfitEntry;
import com.google.gson.annotations.Expose;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class WardrobeConfig extends FileConfig {

    @Expose private Map<String, OutfitEntry> entries;

    @Override
    public String getPath() {
        return "wardrobe";
    }

    public Optional<OutfitEntry> get(String id) {
        return Optional.ofNullable(this.entries.get(id));
    }

    @Override
    protected void reset() {
        this.entries = new LinkedHashMap<>();
    }

}
