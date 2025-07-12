package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;

public class ECCobblemonConfig extends FileConfig {

    @Expose private float blueMoonShinyMultiplier;
    @Expose private float bloodMoonIVsMultiplier;
    @Expose private double harvestMoonExpShareMultiplier;
    @Expose private float auroraMoonRarePokemonSpawnMultiplier;

    @Override
    public String getPath() {
        return "ec_cobblemon";
    }

    public float getBlueMoonShinyMultiplier() {
        return blueMoonShinyMultiplier;
    }

    public float getBloodMoonIVsMultiplier() {
        return bloodMoonIVsMultiplier;
    }

    public double getHarvestMoonExpShareMultiplier() {
        return harvestMoonExpShareMultiplier;
    }

    public float getAuroraMoonRarePokemonSpawnMultiplier() {
        return auroraMoonRarePokemonSpawnMultiplier;
    }

    @Override
    protected void reset() {
        this.blueMoonShinyMultiplier = 1.0F;
        this.bloodMoonIVsMultiplier = 1.0F;
        this.harvestMoonExpShareMultiplier = 1.0F;
        this.auroraMoonRarePokemonSpawnMultiplier = 4F;
    }
}
