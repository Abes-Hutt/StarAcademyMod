package abeshutt.staracademy.item.renderer;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.card.CardData;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.item.CardItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CardItemRenderer extends SpecialItemRenderer {

    public static final CardItemRenderer INSTANCE = new CardItemRenderer();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        CardData card = CardItem.get(stack).orElse(null);

        if(card == null) {
            return;
        }

        Identifier icon = ModConfigs.CARD_ICONS.get(card.getIcon()).orElse(null);
        ModelIdentifier frame = StarAcademyMod.mid("card/frame/" + card.getRarity().asString(), "inventory");

        this.renderModel(frame, stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);

        if(icon != null) {
            this.renderModel(StarAcademyMod.mid(icon, "inventory"), stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, () -> {
                //matrices.translate(0.0F, 0.0F, -0.002F);
            });
        }
    }

}
