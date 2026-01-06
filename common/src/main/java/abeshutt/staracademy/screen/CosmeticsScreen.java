package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class CosmeticsScreen extends Screen {

    public static final Identifier BACKGROUND = StarAcademyMod.id("textures/gui/cosmetics/background.png");
    public static final int BACKGROUND_SIZE_X = 345;
    public static final int BACKGROUND_SIZE_Y = 207;

    private int minX;
    private int minY;

    public CosmeticsScreen() {
        super(Text.literal("Cosmetics"));
    }

    @Override
    protected void init() {
        super.init();
        this.minX = (this.width - BACKGROUND_SIZE_X) / 2;
        this.minY = (this.height - BACKGROUND_SIZE_Y) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int size = 65;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            context.getMatrices().push();
            context.enableScissor(this.minX + 185, this.minY + 28, this.minX + 318, this.minY + 180);
            drawEntity(context, this.minX + BACKGROUND_SIZE_X - 90, this.minY + size * 2 + 32, size,
                    this.minX + BACKGROUND_SIZE_X - 90 - mouseX,
                    this.minY + 75 - mouseY, player, delta);
            context.disableScissor();
            context.getMatrices().pop();
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.drawTexture(BACKGROUND, this.minX, this.minY,
                0, 0, BACKGROUND_SIZE_X, BACKGROUND_SIZE_Y,
                BACKGROUND_SIZE_X, BACKGROUND_SIZE_Y);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity, float delta) {
        float yaw = (float) Math.atan(mouseX / 40.0F);
        float pitch = (float) Math.atan(mouseY / 40.0F);
        Quaternionf extraRotation = (new Quaternionf()).rotateX(-8.0f * (float)Math.PI / 180.0f);
        Quaternionf baseRotation = (new Quaternionf()).rotateZ((float)Math.PI).mul(extraRotation);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + yaw * 20.0F;
        entity.setYaw(180.0F + yaw * 40.0F);
        entity.setPitch(-pitch * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        drawEntity(context, x, y, size, baseRotation, extraRotation, entity, delta);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity, float delta) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 50.0);
        context.getMatrices().multiplyPositionMatrix((new Matrix4f()).scaling((float) size, (float) size, (float) (-size)));
        context.getMatrices().multiply(quaternionf);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            quaternionf2.conjugate();
            entityRenderDispatcher.setRotation(quaternionf2);
        }

        entityRenderDispatcher.setRenderShadows(false);
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0f, context.getMatrices(), context.getVertexConsumers(), 15728880);
        });
        context.draw();
        entityRenderDispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }


}
