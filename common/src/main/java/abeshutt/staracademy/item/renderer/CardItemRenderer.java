package abeshutt.staracademy.item.renderer;

import abeshutt.staracademy.CardRarity;
import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.card.CardData;
import abeshutt.staracademy.card.CardIconEntry;
import abeshutt.staracademy.config.SafariConfig;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.item.CardItem;
import abeshutt.staracademy.item.SafariTicketEntry;
import abeshutt.staracademy.util.ClientScheduler;
import abeshutt.staracademy.world.random.JavaRandom;
import abeshutt.staracademy.world.random.RandomSource;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class CardItemRenderer extends SpecialItemRenderer {

    public static final CardItemRenderer INSTANCE = new CardItemRenderer();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        CardData card = CardItem.get(stack).orElse(null);
        ModelIdentifier frame;
        Identifier icon;

        if(card == null) {
            int index = (int)(ClientScheduler.getTick() >> 5);
            RandomSource random = JavaRandom.ofScrambled(JavaRandom.ofScrambled(index).nextLong());
            CardRarity rarity = CardRarity.values()[random.nextInt(CardRarity.values().length)];
            frame = StarAcademyMod.mid("card/frame/" + rarity.asString(), "inventory");
            List<CardIconEntry> icons = ModConfigs.CARD_ICONS.getValues().values().stream().toList();
            icon = icons.isEmpty() ? null : icons.get(random.nextInt(icons.size())).getModel(rarity).orElse(null);
        } else {
            frame = StarAcademyMod.mid("card/frame/" + card.getRarity().asString(), "inventory");
            icon = ModConfigs.CARD_ICONS.get(card.getIcon())
                    .flatMap(entry -> entry.getModel(card.getRarity()))
                    .orElse(null);
        }

        this.renderModel(frame, stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);

        if(icon != null) {
            this.renderModel(StarAcademyMod.mid(icon, "inventory"), stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, () -> {
                //matrices.translate(0.0F, 0.0F, -0.002F);
            });
        }
    }

}
