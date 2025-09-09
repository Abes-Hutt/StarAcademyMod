package abeshutt.staracademy.item.renderer;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.card.BoosterPackEntry;
import abeshutt.staracademy.card.CardAlbumEntry;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.item.BoosterPackItem;
import abeshutt.staracademy.util.ClientScheduler;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class BoosterPackItemRenderer extends SpecialItemRenderer {

    public static final BoosterPackItemRenderer INSTANCE = new BoosterPackItemRenderer();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BoosterPackItem.get(stack, true).ifPresentOrElse(entry -> {
            Identifier model = stack.contains(DataComponentTypes.CONTAINER) ? entry.getModelRipped() : entry.getModelBase();
            this.renderModel(StarAcademyMod.mid(model, "inventory"), stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);
        }, () -> {
            int index = (int)(ClientScheduler.getTick() >> 5);
            List<BoosterPackEntry> boosters = ModConfigs.CARD_BOOSTERS.getValues().values().stream().toList();

            if(!boosters.isEmpty()) {
                this.renderModel(StarAcademyMod.mid(boosters.get(index % boosters.size()).getModelBase(), "inventory"), stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);
            }
        });
    }

}
