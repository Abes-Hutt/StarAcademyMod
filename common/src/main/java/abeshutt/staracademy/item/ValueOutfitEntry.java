package abeshutt.staracademy.item;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.init.ModOutfits;
import abeshutt.staracademy.item.renderer.OutfitItemRenderer;
import abeshutt.staracademy.outfit.core.OutfitPiece;
import com.google.gson.JsonObject;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;
import java.util.stream.Stream;

public class ValueOutfitEntry extends OutfitEntry {

    private String id;

    public ValueOutfitEntry() {

    }

    public ValueOutfitEntry(String id) {
        this.id = id;
    }

    @Override
    public String getNameKey() {
        return this.id;
    }

    @Override
    public Stream<String> generate() {
        return Stream.of(this.id);
    }

    @Override
    public void render(OutfitItemRenderer renderer, ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ModelIdentifier icon = StarAcademyMod.mid("outfit/" + this.id, "inventory");
        renderer.renderModel(icon, stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);
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
