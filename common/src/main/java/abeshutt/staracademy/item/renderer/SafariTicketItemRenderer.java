package abeshutt.staracademy.item.renderer;

import abeshutt.staracademy.item.SafariTicketItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class SafariTicketItemRenderer extends SpecialItemRenderer {

    public static final SafariTicketItemRenderer INSTANCE = new SafariTicketItemRenderer();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                          VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SafariTicketItem.getEntry(stack, true).ifPresent(entry -> {
            this.renderModel(entry.getModelId(), stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);
        });
    }

}
