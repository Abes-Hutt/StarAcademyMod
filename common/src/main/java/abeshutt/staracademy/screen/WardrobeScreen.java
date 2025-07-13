package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.screen.helper.Texture9SliceRegion;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
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

public class WardrobeScreen extends Screen {

    private static final Identifier TEXTURE = StarAcademyMod.id("textures/gui/wardrobe.png");

    private static final Texture9SliceRegion PREVIEW_BG = new Texture9SliceRegion(0, 0, 9, 9, 256, 256);
    private static final Texture9SliceRegion PREVIEW_INNER = new Texture9SliceRegion(10, 0, 7, 7, 256, 256);
    private static final Texture9SliceRegion OUTFITS_BG = new Texture9SliceRegion(0, 0, 9, 9, 256, 256);

    protected int x, y;
    protected int backgroundWidth = 176;
    protected int backgroundHeight = 166;

    protected WardrobeOutfitsWidget outfitsWidget;

    public WardrobeScreen() {
        super(Text.literal("Wardrobe"));
        this.width = 270;
        this.height = 162;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        this.outfitsWidget = addDrawableChild(new WardrobeOutfitsWidget(this.x + (int) (this.backgroundWidth * 0.5f) / 2 + 9, this.y + 6,
                this.backgroundWidth - 14 - 6, this.backgroundHeight - 14,
                Text.literal("")));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        OUTFITS_BG.draw(context, TEXTURE,
                this.x + (int) (this.backgroundWidth * 0.5f) / 2 + 4, this.y,
                this.backgroundWidth, this.backgroundHeight);
        int padding = 6;
        PREVIEW_BG.draw(context, TEXTURE, this.x - 50, this.y,
                (int) (this.backgroundWidth * 0.55f), this.backgroundHeight);
        PREVIEW_INNER.draw(context, TEXTURE, this.x + padding - 50, this.y + padding,
                (int) (this.backgroundWidth * 0.55f) - 2 * padding, this.backgroundHeight - 2 * padding);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int playerHeight = 50;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            drawEntity(context, this.x - 1, this.y + backgroundWidth / 2 + playerHeight, playerHeight,
                    (float) (this.x + 51) - mouseX,
                    (float) (this.y + 75 - 50) - mouseY, player);
        }

        context.drawText(textRenderer, this.getTitle(), this.x - 48, this.y - 10, 0xFF_FFFFFF, true);
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float) Math.atan((double) (mouseX / 40.0F));
        float g = (float) Math.atan((double) (mouseY / 40.0F));
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity) {
        context.getMatrices().push();
        context.getMatrices().translate((double) x, (double) y, 50.0);
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
            entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context.getMatrices(), context.getVertexConsumers(), 15728880);
        });
        context.draw();
        entityRenderDispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

}
