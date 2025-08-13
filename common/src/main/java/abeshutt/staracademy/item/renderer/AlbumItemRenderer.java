package abeshutt.staracademy.item.renderer;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.item.CardAlbumItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class AlbumItemRenderer extends SpecialItemRenderer {

    public static final AlbumItemRenderer INSTANCE = new AlbumItemRenderer();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        CardAlbumItem.get(stack, true).ifPresent(entry -> {
            this.renderModel(StarAcademyMod.mid(entry.getModel(), "inventory"), stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);
        });
    }

}
