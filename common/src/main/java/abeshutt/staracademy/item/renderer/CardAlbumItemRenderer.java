package abeshutt.staracademy.item.renderer;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.card.CardAlbumEntry;
import abeshutt.staracademy.config.SafariConfig;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.item.CardAlbumItem;
import abeshutt.staracademy.item.SafariTicketEntry;
import abeshutt.staracademy.util.ClientScheduler;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CardAlbumItemRenderer extends SpecialItemRenderer {

    public static final CardAlbumItemRenderer INSTANCE = new CardAlbumItemRenderer();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        CardAlbumItem.get(stack, true).ifPresentOrElse(entry -> {
            this.renderModel(StarAcademyMod.mid(entry.getModel(), "inventory"), stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);
        }, () -> {
            int index = (int)(ClientScheduler.getTick() >> 5);
            List<CardAlbumEntry> albums = ModConfigs.CARD_ALBUMS.getValues().values().stream().toList();

            if(!albums.isEmpty()) {
                this.renderModel(StarAcademyMod.mid(albums.get(index % albums.size()).getModel(), "inventory"), stack, mode, leftHanded, matrices, vertexConsumers, light, overlay);
            }
        });
    }

}
