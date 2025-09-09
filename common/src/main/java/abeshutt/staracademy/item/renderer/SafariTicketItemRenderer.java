package abeshutt.staracademy.item.renderer;

import abeshutt.staracademy.config.SafariConfig;
import abeshutt.staracademy.item.SafariTicketEntry;
import abeshutt.staracademy.item.SafariTicketItem;
import abeshutt.staracademy.util.ClientScheduler;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.List;

public class SafariTicketItemRenderer extends SpecialItemRenderer {

    public static final SafariTicketItemRenderer INSTANCE = new SafariTicketItemRenderer();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                          VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SafariTicketItem.getEntry(stack, true).ifPresentOrElse(entry -> {
            this.renderModel(entry.getModelId(), stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);
        }, () -> {
            int index = (int)(ClientScheduler.getTick() >> 5);
            List<SafariTicketEntry> tickets = SafariConfig.CLIENT.getTickets().values().stream().toList();

            if(!tickets.isEmpty()) {
                this.renderModel(tickets.get(index % tickets.size()).getModelId(), stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);
            }
        });
    }

}
