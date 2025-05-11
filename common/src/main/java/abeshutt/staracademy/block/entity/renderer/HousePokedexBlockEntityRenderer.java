package abeshutt.staracademy.block.entity.renderer;

import abeshutt.staracademy.block.entity.HousePokedexBlockEntity;
import abeshutt.staracademy.model.PokedexModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public class HousePokedexBlockEntityRenderer implements BlockEntityRenderer<HousePokedexBlockEntity> {

    private final GeoObjectRenderer<HousePokedexBlockEntity> pokedex;

    public HousePokedexBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.pokedex = new GeoObjectRenderer<>(new PokedexModel());
    }

    @Override
    public void render(HousePokedexBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5F, 0.75F, 0.5F);
        float tick = (float)entity.getTick(null);
        matrices.translate(0.0F, 0.1F + MathHelper.sin(tick * 0.1F) * 0.01F, 0.0F);
        double rotation = entity.rotation - entity.lastRotation;

        while(rotation >= Math.PI) {
            rotation -= 2.0D * Math.PI;
        }

        while(rotation < -Math.PI) {
            rotation += Math.PI * 2.0D;
        }

        rotation = entity.lastRotation + rotation * tickDelta + Math.PI / 2.0D;

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)-rotation));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-20.0F));
        matrices.translate(-0.5F, -0.5F, -0.5F);

        this.pokedex.render(matrices, entity, vertexConsumers, null,
                null, light, tickDelta);
        matrices.pop();
    }

}
