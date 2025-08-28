package abeshutt.staracademy.data.adapter.util;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.Optional;

import static abeshutt.staracademy.data.adapter.basic.EnumAdapter.Mode.NAME;

public class ServerSound implements ISerializable<NbtCompound, JsonObject> {

    protected Identifier id;
    protected SoundCategory category;
    protected float volume;
    protected float pitch;
    protected Vec3d position;
    protected boolean repeat;
    protected int repeatDelay;
    protected AttenuationType attenuationType;
    protected boolean relative;

    public ServerSound() {

    }

    public ServerSound(Identifier id, SoundCategory category, float volume, float pitch, Vec3d position, boolean repeat,
                       int repeatDelay, AttenuationType attenuationType, boolean relative) {
        this.id = id;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
        this.position = position;
        this.repeat = repeat;
        this.repeatDelay = repeatDelay;
        this.attenuationType = attenuationType;
        this.relative = relative;
    }

    @Environment(EnvType.CLIENT)
    public PositionedSoundInstance build(Random random) {
        return new PositionedSoundInstance(this.id, this.category, this.volume, this.pitch, random, this.repeat,
                this.repeatDelay, this.attenuationType.toVanilla(), this.position.x, this.position.y, this.position.z, this.relative);
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.IDENTIFIER.writeBits(this.id, buffer);
        Adapters.ofEnum(SoundCategory.class, NAME).writeBits(this.category, buffer);
        Adapters.FLOAT.writeBits(this.volume, buffer);
        Adapters.FLOAT.writeBits(this.pitch, buffer);
        Adapters.BOOLEAN.writeBits(this.repeat, buffer);
        Adapters.INT.writeBits(this.repeatDelay, buffer);
        Adapters.ofEnum(AttenuationType.class, NAME).writeBits(this.attenuationType, buffer);
        Adapters.VEC_3D.writeBits(this.position, buffer);
        Adapters.BOOLEAN.writeBits(this.relative, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.id = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
        this.category = Adapters.ofEnum(SoundCategory.class, NAME).readBits(buffer).orElseThrow();
        this.volume = Adapters.FLOAT.readBits(buffer).orElseThrow();
        this.pitch = Adapters.FLOAT.readBits(buffer).orElseThrow();
        this.repeat = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
        this.repeatDelay = Adapters.INT.readBits(buffer).orElseThrow();
        this.attenuationType = Adapters.ofEnum(AttenuationType.class, NAME).readBits(buffer).orElseThrow();
        this.position = Adapters.VEC_3D.readBits(buffer).orElseThrow();
        this.relative = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
    }

    @Override
    public Optional<JsonObject> writeJson() {
        JsonObject json = new JsonObject();
        Adapters.IDENTIFIER.writeJson(this.id).ifPresent(tag -> json.add("id", tag));
        Adapters.ofEnum(SoundCategory.class, NAME).writeJson(this.category).ifPresent(tag -> json.add("category", tag));
        Adapters.FLOAT.writeJson(this.volume).ifPresent(tag -> json.add("volume", tag));
        Adapters.FLOAT.writeJson(this.pitch).ifPresent(tag -> json.add("pitch", tag));
        Adapters.BOOLEAN.writeJson(this.repeat).ifPresent(tag -> json.add("repeat", tag));
        Adapters.INT.writeJson(this.repeatDelay).ifPresent(tag -> json.add("repeatDelay", tag));
        Adapters.ofEnum(AttenuationType.class, NAME).writeJson(this.attenuationType).ifPresent(tag -> json.add("attenuation", tag));
        Adapters.VEC_3D.writeJson(this.position).ifPresent(tag -> json.add("position", tag));
        Adapters.BOOLEAN.writeJson(this.relative).ifPresent(tag -> json.add("relative", tag));
        return Optional.of(json);
    }

    @Override
    public void readJson(JsonObject json) {
        this.id = Adapters.IDENTIFIER.readJson(json.get("id")).orElseThrow();
        this.category = Adapters.ofEnum(SoundCategory.class, NAME).readJson(json.get("category")).orElseThrow();
        this.volume = Adapters.FLOAT.readJson(json.get("volume")).orElseThrow();
        this.pitch = Adapters.FLOAT.readJson(json.get("pitch")).orElseThrow();
        this.position = Adapters.VEC_3D.readJson(json.get("position")).orElseThrow();
        this.repeat = Adapters.BOOLEAN.readJson(json.get("repeat")).orElseThrow();
        this.repeatDelay = Adapters.INT.readJson(json.get("repeatDelay")).orElseThrow();
        this.attenuationType = Adapters.ofEnum(AttenuationType.class, NAME).readJson(json.get("attenuation")).orElseThrow();
        this.relative = Adapters.BOOLEAN.readJson(json.get("relative")).orElseThrow();
    }

    public enum AttenuationType {
        NONE,
        LINEAR;

        @Environment(EnvType.CLIENT)
        public SoundInstance.AttenuationType toVanilla() {
            return switch(this) {
                case NONE -> SoundInstance.AttenuationType.NONE;
                case LINEAR -> SoundInstance.AttenuationType.LINEAR;
            };
        }
    }

}

