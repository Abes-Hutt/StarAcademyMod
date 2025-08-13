package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.type.AttributeType;
import abeshutt.staracademy.data.adapter.IAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public abstract class ValueAttribute<T, V, G> extends ConfigAttribute<T, G> {

    protected V value;
    private final IAdapter<V, NbtElement, JsonElement, ?> valueAdapter;
    private final IAdapter<G, NbtElement, JsonElement, ?> configAdapter;

    protected ValueAttribute(AttributeType<T> type, V value, G config, IAdapter<V, ?, ?, ?> valueAdapter, IAdapter<G, ?, ?, ?> configAdapter) {
        super(type, config);
        this.value = value;
        this.valueAdapter = (IAdapter)valueAdapter;
        this.configAdapter = (IAdapter)configAdapter;
    }

    public V getValue() {
        return this.value;
    }

    @Override
    public final Option<T> get(Option<T> value, AttributeContext context) {
        this.populate(context);
        return this.compute(value, context);
    }

    protected abstract Option<T> compute(Option<T> value, AttributeContext context);

    @Override
    protected void writeBaseBits(BitBuffer buffer) {
        this.valueAdapter.writeBits(this.value, buffer, null);
    }

    @Override
    protected void readBaseBits(BitBuffer buffer) {
        this.value = this.valueAdapter.readBits(buffer, null).orElse(null);
    }

    @Override
    protected void writeBaseNbt(NbtCompound nbt) {
        this.valueAdapter.writeNbt(this.value, null).ifPresent(tag -> nbt.put("value", tag));
    }

    @Override
    protected void readBaseNbt(NbtCompound nbt) {
        this.value = this.valueAdapter.readNbt(nbt.get("value"), null).orElse(null);
    }

    @Override
    protected void writeBaseJson(JsonObject json) {
        this.valueAdapter.writeJson(this.value, null).ifPresent(tag -> json.add("value", tag));
    }

    @Override
    protected void readBaseJson(JsonObject json) {
        this.value = this.valueAdapter.readJson(json.get("value"), null).orElse(null);
    }

    @Override
    protected IAdapter<G, NbtElement, JsonElement, ?> getConfigAdapter() {
        return this.configAdapter;
    }

}
