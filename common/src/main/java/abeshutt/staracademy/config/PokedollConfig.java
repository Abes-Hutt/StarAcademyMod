package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class PokedollConfig extends FileConfig {


    @Expose
    private Set<Identifier> blocksToDetect = new HashSet<>();

    @Expose
    private int radius = 16;

    @Expose
    private int numberOfPokedolls = 5;

    @Expose
    private int checkInterval = 20 * 3; // In ticks.


    @Override
    public String getPath() {
        return "pokedoll";
    }

    @Override
    protected void reset() {
        blocksToDetect.clear();
        radius = 16;
        numberOfPokedolls = 5;
        checkInterval = 20 * 3;
    }

    public Set<Identifier> getBlocksToDetect() {
        return blocksToDetect;
    }

    public int getRadius() {
        return radius;
    }

    public int getNumberOfPokedolls() {
        return numberOfPokedolls;
    }

    public int getCheckInterval() {
        return checkInterval;
    }
}
