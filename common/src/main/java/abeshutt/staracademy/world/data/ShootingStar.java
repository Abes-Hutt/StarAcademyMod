package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public class ShootingStar implements ISerializable<NbtCompound, JsonObject> {

    private Vec3d start;
    private Vec3d mid;
    private Vec3d end;
    private int tick;

    private double speed;
    private Vec3d position;
    private Vec3d lastPosition;
    public RegistryKey<World> dimension;

    public ShootingStar(Vec3d start, Vec3d mid, Vec3d end, double speed, RegistryKey<World> dimension) {
        this.start = start;
        this.mid = mid;
        this.end = end;

        this.speed = speed;
        this.position = this.start;
        this.lastPosition = this.position;
        this.dimension = dimension;
    }

    public void tick() {
        double t = this.tick / 300.0D * this.speed;
        this.lastPosition = this.position;
        this.position = this.start.multiply((1 - t) * (1 - t))
                .add(this.mid.multiply(2 * (1 - t) * t))
                .add(this.end.multiply(t * t));
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.VEC_3D.writeBits(this.start, buffer);
        Adapters.VEC_3D.writeBits(this.mid, buffer);
        Adapters.VEC_3D.writeBits(this.end, buffer);
        Adapters.INT_SEGMENTED_7.writeBits(this.tick, buffer);
        Adapters.DOUBLE.writeBits(this.speed, buffer);
        Adapters.VEC_3D.writeBits(this.position, buffer);
        Adapters.VEC_3D.writeBits(this.lastPosition, buffer);
        Adapters.IDENTIFIER.writeBits(this.dimension.getValue(), buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.start = Adapters.VEC_3D.readBits(buffer).orElseThrow();
        this.mid = Adapters.VEC_3D.readBits(buffer).orElseThrow();
        this.end = Adapters.VEC_3D.readBits(buffer).orElseThrow();
        this.tick = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
        this.speed = Adapters.DOUBLE.readBits(buffer).orElseThrow();
        this.position = Adapters.VEC_3D.readBits(buffer).orElseThrow();
        this.lastPosition = Adapters.VEC_3D.readBits(buffer).orElseThrow();
        this.dimension = RegistryKey.of(RegistryKeys.WORLD, Adapters.IDENTIFIER.readBits(buffer).orElseThrow());
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.VEC_3D.writeNbt(this.start).ifPresent(tag -> nbt.put("modeMinimum", tag));
            Adapters.VEC_3D.writeNbt(this.mid).ifPresent(tag -> nbt.put("mid", tag));
            Adapters.VEC_3D.writeNbt(this.end).ifPresent(tag -> nbt.put("modeMaximum", tag));
            Adapters.INT.writeNbt(this.tick).ifPresent(tag -> nbt.put("tick", tag));
            Adapters.DOUBLE.writeNbt(this.speed).ifPresent(tag -> nbt.put("speed", tag));
            Adapters.VEC_3D.writeNbt(this.position).ifPresent(tag -> nbt.put("position", tag));
            Adapters.VEC_3D.writeNbt(this.lastPosition).ifPresent(tag -> nbt.put("lastPosition", tag));
            Adapters.IDENTIFIER.writeNbt(this.dimension.getValue()).ifPresent(tag -> nbt.put("dimension", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.start = Adapters.VEC_3D.readNbt(nbt.get("modeMinimum")).orElse(Vec3d.ZERO);
        this.mid = Adapters.VEC_3D.readNbt(nbt.get("mid")).orElse(Vec3d.ZERO);
        this.end = Adapters.VEC_3D.readNbt(nbt.get("modeMaximum")).orElse(Vec3d.ZERO);
        this.tick = Adapters.INT.readNbt(nbt.get("tick")).orElse(0);
        this.speed = Adapters.DOUBLE.readNbt(nbt.get("speed")).orElse(0.0D);
        this.position = Adapters.VEC_3D.readNbt(nbt.get("position")).orElse(Vec3d.ZERO);
        this.lastPosition = Adapters.VEC_3D.readNbt(nbt.get("lastPosition")).orElse(Vec3d.ZERO);
        this.dimension = RegistryKey.of(RegistryKeys.WORLD, Adapters.IDENTIFIER.readNbt(nbt.get("dimension")).orElseThrow());
    }

}
