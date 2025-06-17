package abeshutt.staracademy.item;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.init.ModConfigs;
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

public class ReferenceOutfitEntry extends OutfitEntry {

    private String id;

    public ReferenceOutfitEntry() {

    }

    @Override
    public Stream<String> generate() {
        return Stream.empty();
    }

    @Override
    public Optional<OutfitEntry> flatten(RandomSource random) {
        return ModConfigs.WARDROBE.get(this.id).flatMap(entry -> entry.flatten(random));
    }

    @Override
    public void render(OutfitItemRenderer renderer, ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

    }

    @Override
    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.UTF_8.asNullable().writeBits(this.id, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.id = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return super.writeNbt().map(nbt -> {
            Adapters.UTF_8.writeNbt(this.id).ifPresent(tag -> nbt.put("id", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.id = Adapters.UTF_8.readNbt(nbt.get("id")).orElse(null);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            Adapters.UTF_8.writeJson(this.id).ifPresent(tag -> json.add("id", tag));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.id = Adapters.UTF_8.readJson(json.get("id")).orElse(null);
    }

}
