package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.again.type.AttributeType;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.IAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;

public abstract class ConfigAttribute<T, C> extends Attribute<T> {

    private C config;
    private boolean populated;

    protected ConfigAttribute(AttributeType<T> type, C config) {
        super(type);
        this.config = config;
    }

    public C getConfig() {
        return this.config;
    }

    public boolean isPopulated() {
        return this.populated;
    }

    @Override
    public void populate(AttributeContext context) {
        if(!this.populated) {
            this.generate(context);
            this.populated = true;
        }

        super.populate(context);
    }

    protected abstract void generate(AttributeContext context);

    protected abstract void writeBaseBits(BitBuffer buffer);
    protected abstract void readBaseBits(BitBuffer buffer);
    protected abstract void writeBaseNbt(NbtCompound nbt);
    protected abstract void readBaseNbt(NbtCompound nbt);
    protected abstract void writeBaseJson(JsonObject json);
    protected abstract void readBaseJson(JsonObject json);

    protected abstract IAdapter<C, NbtElement, JsonElement, ?> getConfigAdapter();

    @Override
    public final void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.BOOLEAN.writeBits(this.populated, buffer);

        if(this.populated) {
            this.writeBaseBits(buffer);
        }

        this.getConfigAdapter().writeBits(this.config, buffer, null);
    }

    @Override
    public final void readBits(BitBuffer buffer) {
        super.readBits(buffer);

        if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
            this.readBaseBits(buffer);
        }

        this.config = this.getConfigAdapter().readBits(buffer, null).orElse(null);
    }

    @Override
    public final Optional<NbtElement> writeNbt() {
        if(!this.populated) {
            return this.getConfigAdapter().writeNbt(this.config, null);
        }

        NbtCompound nbt = new NbtCompound();
        this.writeBaseNbt(nbt);
        nbt.put("config", this.getConfigAdapter().writeNbt(this.config, null).orElseThrow());
        return Optional.of(nbt);
    }

    @Override
    public final void readNbt(NbtElement nbt) {
        super.readNbt(nbt);

        if(nbt instanceof NbtCompound object && object.contains("config")) {
            this.readBaseNbt(object);
            this.config = this.getConfigAdapter().readNbt(object.get("config"), null).orElseThrow();
            this.populated = true;
        } else {
            this.config = this.getConfigAdapter().readNbt(nbt, null).orElseThrow();
            this.populated = false;
        }
    }

    @Override
    public final Optional<JsonElement> writeJson() {
        if(!this.populated) {
            return this.getConfigAdapter().writeJson(this.config, null);
        }

        JsonObject json = new JsonObject();
        this.writeBaseJson(json);
        json.add("config", this.getConfigAdapter().writeJson(this.config, null).orElseThrow());
        return Optional.of(json);
    }

    @Override
    public final void readJson(JsonElement json) {
        super.readJson(json);

        if(json instanceof JsonObject object && object.has("config")) {
            this.readBaseJson(object);
            this.config = this.getConfigAdapter().readJson(object.get("config"), null).orElseThrow();
            this.populated = true;
        } else {
            this.config = this.getConfigAdapter().readJson(json, null).orElseThrow();
            this.populated = false;
        }
    }

}
