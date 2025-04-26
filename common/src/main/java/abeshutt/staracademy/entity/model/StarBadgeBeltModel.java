package abeshutt.staracademy.entity.model;

import abeshutt.staracademy.StarAcademyMod;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class StarBadgeBeltModel extends Model {

    public static final Identifier TEXTURE = StarAcademyMod.id("textures/entity/star_badge_belt.png");

    private final ModelPart root;

    public StarBadgeBeltModel() {
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
        ModelPartData body = root.addChild("body", ModelPartBuilder.create().uv(0, 9).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.51F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData cube_r1 = body.addChild("cube_r1", ModelPartBuilder.create().uv(1, 1).cuboid(-5.5F, -1.0F, -3.5F, 11.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 11.25F, 0.5F, 0.0F, 0.0F, 0.3927F));
        ModelPartData RightLeg = root.addChild("right", ModelPartBuilder.create().uv(24, 9).cuboid(-2.0F, -0.25F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.5F)), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));
        ModelPartData LeftLeg = root.addChild("left", ModelPartBuilder.create().uv(0, 25).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.5F)), ModelTransform.pivot(1.9F, 12.0F, 0.0F));
        return TexturedModelData.of(data, 64, 64);
    }

}
