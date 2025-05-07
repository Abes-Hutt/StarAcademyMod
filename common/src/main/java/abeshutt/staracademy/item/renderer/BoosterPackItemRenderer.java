package abeshutt.staracademy.item.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class BoosterPackItemRenderer extends SpecialItemRenderer {

    public static final BoosterPackItemRenderer INSTANCE = new BoosterPackItemRenderer();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ModelIdentifier model = null;
        this.renderModel(model, stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);
    }

}
