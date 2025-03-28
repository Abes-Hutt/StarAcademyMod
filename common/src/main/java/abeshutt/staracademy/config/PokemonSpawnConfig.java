package abeshutt.staracademy.config;

import abeshutt.staracademy.world.roll.IntRoll;
import com.google.gson.annotations.Expose;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class PokemonSpawnConfig extends FileConfig {

    @Expose private double spawnProtectionDistance;
    @Expose private Map<Double, IntRoll> distanceToLevel;

    @Override
    public String getPath() {
        return "pokemon_spawn";
    }

    public double getSpawnProtectionDistance() {
        return this.spawnProtectionDistance;
    }

    public Optional<IntRoll> getLevel(double distance) {
        IntRoll roll = null;

        for(Map.Entry<Double, IntRoll> entry : this.distanceToLevel.entrySet()) {
           if(distance >= entry.getKey()) {
               roll = entry.getValue();
           } else {
               break;
           }
        }

        return Optional.ofNullable(roll);
    }

    @Override
    protected void reset() {
        this.spawnProtectionDistance = 100.0D;
        this.distanceToLevel = new LinkedHashMap<>();
        this.distanceToLevel.put(0.0D, IntRoll.ofUniform(0, 10));
        this.distanceToLevel.put(800.0D, IntRoll.ofUniform(0, 15));
        this.distanceToLevel.put(1600.0D, IntRoll.ofUniform(0, 20));
        this.distanceToLevel.put(2400.0D, IntRoll.ofUniform(0, 25));
        this.distanceToLevel.put(3200.0D, IntRoll.ofUniform(0, 30));
        this.distanceToLevel.put(4000.0D, IntRoll.ofUniform(0, 35));
        this.distanceToLevel.put(4800.0D, IntRoll.ofUniform(0, 40));
        this.distanceToLevel.put(5600.0D, IntRoll.ofUniform(0, 45));
        this.distanceToLevel.put(6400.0D, IntRoll.ofUniform(0, 50));
        this.distanceToLevel.put(7200.0D, IntRoll.ofUniform(0, 55));
        this.distanceToLevel.put(8000.0D, IntRoll.ofUniform(0, 60));
        this.distanceToLevel.put(9000.0D, IntRoll.ofUniform(0, 65));
        this.distanceToLevel.put(10000.0D, IntRoll.ofUniform(0, 70));
    }

    @Override
    public <T extends Config> T read() {
        PokemonSpawnConfig config = super.read();
        Map<Double, IntRoll> ordered = new TreeMap<>(Double::compare);
        ordered.putAll(config.distanceToLevel);
        config.distanceToLevel = ordered;
        return (T)config;
    }

}
