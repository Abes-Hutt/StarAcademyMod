package abeshutt.staracademy.item.renderer;

import abeshutt.staracademy.CardRarity;
import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.item.CardItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class CardItemRenderer extends SpecialItemRenderer {

    public static final CardItemRenderer INSTANCE = new CardItemRenderer();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int index = CardItem.getIndex(stack);
        CardRarity rarity;
        ModelIdentifier icon;

        if(index == 0) {
            rarity = CardRarity.COMMON;
            icon = StarAcademyMod.mid("card/icon/bulbasaur", "inventory");
        } else if(index == 1) {
            rarity = CardRarity.UNCOMMON;
            icon = StarAcademyMod.mid("card/icon/gengar", "inventory");
        } else if(index == 2) {
            rarity = CardRarity.RARE;
            icon = StarAcademyMod.mid("card/icon/ivysaur", "inventory");
        } else if(index == 3) {
            rarity = CardRarity.EPIC;
            icon = StarAcademyMod.mid("card/icon/venusaur", "inventory");
        } else if(index == 4) {
            rarity = CardRarity.LEGENDARY;
            icon = StarAcademyMod.mid("card/icon/bulbasaur", "inventory");
        } else {
            rarity = CardRarity.SHINY;
            icon = StarAcademyMod.mid("card/icon/gengar", "inventory");
        }

        ModelIdentifier frame = StarAcademyMod.mid("card/frame/" + rarity.asString(), "inventory");

        this.renderModel(frame, stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);

        this.renderModel(icon, stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, () -> {
            //matrices.translate(0.0F, 0.0F, -0.002F);
        });
    }

}
