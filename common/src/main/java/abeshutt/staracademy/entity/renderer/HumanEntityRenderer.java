package abeshutt.staracademy.entity.renderer;

import abeshutt.staracademy.entity.HumanData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class HumanEntityRenderer<T extends LivingEntity> extends LivingEntityRenderer<T, PlayerEntityModel<T>> {

    private final Function<T, HumanData> data;

    public HumanEntityRenderer(EntityRendererFactory.Context context, boolean slim, Function<T, HumanData> data) {
        super(context, new PlayerEntityModel<>(context.getPart(slim ?
                EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim), 0.5F);
        this.addFeature(new ArmorFeatureRenderer<>(this, new ArmorEntityModel<>(context.getPart(slim ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)), new ArmorEntityModel<>(context.getPart(slim ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
        this.addFeature(new StuckArrowsFeatureRenderer<>(context, this));
        this.addFeature(new HumanCapeFeatureRenderer<>(this));
        this.addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader(), context.getHeldItemRenderer()));
        this.addFeature(new ElytraFeatureRenderer<>(this, context.getModelLoader()));
        this.addFeature(new TridentRiptideFeatureRenderer<>(this, context.getModelLoader()));
        this.addFeature(new StuckStingersFeatureRenderer<>(this));
        this.data = data;
    }

    @Override
    public void render(T human, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.setModelPose(human);
        super.render(human, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Vec3d getPositionOffset(T abstractClientPlayerEntity, float f) {
        return abstractClientPlayerEntity.isInSneakingPose() ? new Vec3d(0.0, -0.125, 0.0) : super.getPositionOffset(abstractClientPlayerEntity, f);
    }

    @Override
    protected boolean hasLabel(T human) {
        return human.shouldRenderName() && human.hasCustomName();
    }

    private void setModelPose(T human) {
        PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = (PlayerEntityModel)this.getModel();
        if (human.isSpectator()) {
            playerEntityModel.setVisible(false);
            playerEntityModel.head.visible = true;
            playerEntityModel.hat.visible = true;
        } else {
            playerEntityModel.setVisible(true);
            playerEntityModel.hat.visible = true;
            playerEntityModel.jacket.visible = true;
            playerEntityModel.leftPants.visible = true;
            playerEntityModel.rightPants.visible = true;
            playerEntityModel.leftSleeve.visible = true;
            playerEntityModel.rightSleeve.visible = true;
            playerEntityModel.sneaking = human.isInSneakingPose();
            BipedEntityModel.ArmPose armPose = getArmPose(human, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose armPose2 = getArmPose(human, Hand.OFF_HAND);
            if (armPose.isTwoHanded()) {
                armPose2 = human.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
            }

            if (human.getMainArm() == Arm.RIGHT) {
                playerEntityModel.rightArmPose = armPose;
                playerEntityModel.leftArmPose = armPose2;
            } else {
                playerEntityModel.rightArmPose = armPose2;
                playerEntityModel.leftArmPose = armPose;
            }
        }

    }

    private static BipedEntityModel.ArmPose getArmPose(LivingEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return BipedEntityModel.ArmPose.EMPTY;
        } else {
            if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
                UseAction useAction = itemStack.getUseAction();
                if (useAction == UseAction.BLOCK) {
                    return BipedEntityModel.ArmPose.BLOCK;
                }

                if (useAction == UseAction.BOW) {
                    return BipedEntityModel.ArmPose.BOW_AND_ARROW;
                }

                if (useAction == UseAction.SPEAR) {
                    return BipedEntityModel.ArmPose.THROW_SPEAR;
                }

                if (useAction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
                    return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useAction == UseAction.SPYGLASS) {
                    return BipedEntityModel.ArmPose.SPYGLASS;
                }

                if (useAction == UseAction.TOOT_HORN) {
                    return BipedEntityModel.ArmPose.TOOT_HORN;
                }

                if (useAction == UseAction.BRUSH) {
                    return BipedEntityModel.ArmPose.BRUSH;
                }
            } else if (!player.handSwinging && itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
                return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
            }

            return BipedEntityModel.ArmPose.ITEM;
        }
    }

    @Override
    public Identifier getTexture(T human) {
        return this.data.apply(human).getSkinTexture();
    }

    protected void scale(T abstractClientPlayerEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375F;
        matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    protected void renderLabelIfPresent(T human, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float tickDelta) {
        matrixStack.push();
        super.renderLabelIfPresent(human, text, matrixStack, vertexConsumerProvider, i, tickDelta);
        matrixStack.pop();
    }

    public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T player) {
        this.renderArm(matrices, vertexConsumers, light, player, this.model.rightArm, this.model.rightSleeve);
    }

    public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T player) {
        this.renderArm(matrices, vertexConsumers, light, player, this.model.leftArm, this.model.leftSleeve);
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T human, ModelPart arm, ModelPart sleeve) {
        PlayerEntityModel<T> playerEntityModel = this.getModel();
        this.setModelPose(human);
        playerEntityModel.handSwingProgress = 0.0F;
        playerEntityModel.sneaking = false;
        playerEntityModel.leaningPitch = 0.0F;
        playerEntityModel.setAngles(human, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        arm.pitch = 0.0F;
        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(this.getTexture(human))), light, OverlayTexture.DEFAULT_UV);
        sleeve.pitch = 0.0F;
        sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(human))), light, OverlayTexture.DEFAULT_UV);
    }

    protected void setupTransforms(T human, MatrixStack matrixStack, float f, float g, float h, float i) {
        float j = human.getLeaningPitch(h);
        float k = human.getPitch(h);
        float l;
        float m;
        if (human.isFallFlying()) {
            super.setupTransforms(human, matrixStack, f, g, h, i);
            l = (float)human.getFallFlyingTicks() + h;
            m = MathHelper.clamp(l * l / 100.0F, 0.0F, 1.0F);
            if (!human.isUsingRiptide()) {
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m * (-90.0F - k)));
            }

            Vec3d vec3d = human.getRotationVec(h);
            Vec3d vec3d2 = this.data.apply(human).lerpVelocity(h);
            double d = vec3d2.horizontalLengthSquared();
            double e = vec3d.horizontalLengthSquared();
            if (d > 0.0 && e > 0.0) {
                double n = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double o = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation((float)(Math.signum(o) * Math.acos(n))));
            }
        } else if (j > 0.0F) {
            super.setupTransforms(human, matrixStack, f, g, h, i);
            l = human.isTouchingWater() ? -90.0F - k : -90.0F;
            m = MathHelper.lerp(j, 0.0F, l);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m));
            if (human.isInSwimmingPose()) {
                matrixStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.setupTransforms(human, matrixStack, f, g, h, i);
        }
    }

}
