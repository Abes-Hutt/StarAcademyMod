package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class LegendaryItemsConfig extends FileConfig {

    @Expose private List<String> custom;
    @Expose private List<Identifier> occurrences;
    @Expose private boolean unique;

    @Override
    public String getPath() {
        return "legendary_items";
    }

    public List<String> getCustom() {
        return this.custom;
    }

    public List<Identifier> getOccurrences() {
        return this.occurrences;
    }

    public boolean isUnique() {
        return this.unique;
    }

    @Override
    protected void reset() {
        this.custom = new ArrayList<>();
        this.custom.add("academy:test");

        this.occurrences = new ArrayList<>();
        this.occurrences.add(Identifier.ofVanilla("diamond"));
        this.occurrences.add(Identifier.ofVanilla("emerald"));
        this.unique = true;
    }

}
