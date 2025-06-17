package abeshutt.staracademy.item;

import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.item.renderer.OutfitItemRenderer;
import abeshutt.staracademy.world.random.RandomSource;
import com.google.gson.JsonObject;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class OutfitEntry implements ISerializable<NbtCompound, JsonObject> {

    public abstract Stream<String> generate();

    public Optional<OutfitEntry> flatten(RandomSource random) {
        return Optional.of(this);
    }

    public String getNameKey() {
        return null;
    }

    public abstract void render(OutfitItemRenderer renderer, ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                                VertexConsumerProvider vertexConsumers, int light, int overlay);

    @Override
    public void writeBits(BitBuffer buffer) {

    }

    @Override
    public void readBits(BitBuffer buffer) {

    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound());
    }

    @Override
    public void readNbt(NbtCompound nbt) {

    }

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject());
    }

    @Override
    public void readJson(JsonObject json) {

    }

    public static class Adapter extends TypeSupplierAdapter<OutfitEntry> {
        public Adapter() {
            super("type", true);
            this.register("value", ValueOutfitEntry.class, ValueOutfitEntry::new);
            this.register("reference", ReferenceOutfitEntry.class, ReferenceOutfitEntry::new);
        }
    }

}
