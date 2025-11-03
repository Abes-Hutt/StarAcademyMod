package abeshutt.staracademy.world.data.save;

import abeshutt.staracademy.world.data.ShootingStar;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ShootingStarData extends WorldData {

    private Map<UUID, ShootingStar> stars;

    public ShootingStarData() {
        this.stars = new HashMap<>();
    }

    public UUID add(ShootingStar star) {
        UUID uuid = UUID.randomUUID();
        this.stars.put(uuid, star);
        this.setDirty(true);
        return uuid;
    }

    public void onTick() {
        for(ShootingStar star : this.stars.values()) {
            star.tick();
        }


    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {

    }

}
