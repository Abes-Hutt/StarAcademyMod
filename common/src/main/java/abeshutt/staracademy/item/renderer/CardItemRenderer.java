package abeshutt.staracademy.item.renderer;

import abeshutt.staracademy.CardRarity;
import abeshutt.staracademy.StarAcademyMod;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class CardItemRenderer extends SpecialItemRenderer {

    public static final CardItemRenderer INSTANCE = new CardItemRenderer();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        CardRarity rarity = CardRarity.LEGENDARY;
        ModelIdentifier frame = StarAcademyMod.mid("item/card/frame/" + rarity.asString(), "inventory");
        ModelIdentifier icon = StarAcademyMod.mid("<icon>", "inventory");

        this.renderModel(frame, stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);

        this.renderModel(icon, stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, () -> {
            matrices.scale(0.5F, 0.5F, 1.0F);
            matrices.translate(0.5F, 0.53F, 0.02F);
        });
    }

}
