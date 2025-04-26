package abeshutt.staracademy.entity.model;

import abeshutt.staracademy.StarAcademyMod;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ShootingStarModel extends Model {

    public static final Identifier TEXTURE = StarAcademyMod.id("textures/entity/shooting_star.png");

    private final ModelPart root;

    public ShootingStarModel() {
        super(RenderLayer::getEntityTranslucent);
        this.root = createMesh().createModel();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }

    public static TexturedModelData createMesh() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        root.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, -20.0F, -10.0F, 20.0F, 20.0F, 20.0F, new Dilation(0.0F)), ModelTransform.rotation(0.0F, 24.0F, 0.0F));
        root.addChild("cube_r1", ModelPartBuilder.create().uv(0, 80).cuboid(-10.0F, -10.0F, -10.0F, 20.0F, 20.0F, 20.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -10.0F, 0.0F, 2.397F, -0.5956F, -0.5468F));
        root.addChild("cube_r2", ModelPartBuilder.create().uv(0, 40).cuboid(-10.0F, -10.0F, -10.0F, 20.0F, 20.0F, 20.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -10.0F, 0.0F, 0.4971F, -0.445F, 0.6707F));
        return TexturedModelData.of(data, 128, 128);
    }

}
