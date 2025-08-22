package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;

public class ECCobblemonConfig extends FileConfig {

    @Expose private float blueMoonShinyMultiplier;
    @Expose private float bloodMoonIVsMultiplier;
    @Expose private double harvestMoonExpShareMultiplier;
    @Expose private float auroraMoonRarePokemonSpawnMultiplier;
    @Expose private float superBlueMoonShinyMultiplier;
    @Expose private float superBloodMoonIVsMultiplier;
    @Expose private double superHarvestMoonExpShareMultiplier;
    @Expose private float superAuroraMoonRarePokemonSpawnMultiplier;

    @Expose private int forecastDayView = 10;
    @Expose private int hologramSwitchTime = 5 * 20; // 5 seconds in ticks

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

    public int getForecastDayView() {
        return forecastDayView;
    }

    public int getHologramSwitchTime() {
        return hologramSwitchTime;
    }

    public float getSuperBlueMoonShinyMultiplier() {
        return superBlueMoonShinyMultiplier;
    }

    public float getSuperBloodMoonIVsMultiplier() {
        return superBloodMoonIVsMultiplier;
    }

    public double getSuperHarvestMoonExpShareMultiplier() {
        return superHarvestMoonExpShareMultiplier;
    }

    public float getSuperAuroraMoonRarePokemonSpawnMultiplier() {
        return superAuroraMoonRarePokemonSpawnMultiplier;
    }

    @Override
    protected void reset() {
        this.blueMoonShinyMultiplier = 1.0F;
        this.bloodMoonIVsMultiplier = 1.0F;
        this.harvestMoonExpShareMultiplier = 1.0F;
        this.auroraMoonRarePokemonSpawnMultiplier = 4F;
        this.superBlueMoonShinyMultiplier = 2.0F;
        this.superBloodMoonIVsMultiplier = 2.0F;
        this.superHarvestMoonExpShareMultiplier = 2.0F;
        this.superAuroraMoonRarePokemonSpawnMultiplier = 8F;
    }
}
