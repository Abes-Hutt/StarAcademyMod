package abeshutt.staracademy.item.renderer;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.item.BoosterPackItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class BoosterPackItemRenderer extends SpecialItemRenderer {

    public static final BoosterPackItemRenderer INSTANCE = new BoosterPackItemRenderer();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BoosterPackItem.get(stack, true).ifPresent(entry -> {
            Identifier model = stack.contains(DataComponentTypes.CONTAINER) ? entry.getModelRipped() : entry.getModelBase();
            this.renderModel(StarAcademyMod.mid(model, "inventory"), stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);
        });
    }

}
