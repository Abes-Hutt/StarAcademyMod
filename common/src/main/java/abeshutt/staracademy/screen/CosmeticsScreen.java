package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.cosmetic.Cosmetic;
import abeshutt.staracademy.cosmetic.CosmeticSlot;
import abeshutt.staracademy.screen.widget.CosmeticButtonWidget;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosmeticsScreen extends Screen {

    public static final Identifier BACKGROUND = StarAcademyMod.id("textures/gui/cosmetics/background.png");
    public static final Identifier INVENTORY_BUTTON = StarAcademyMod.id("textures/gui/cosmetics/inv_button.png");
    public static final Identifier SHOP_BUTTON = StarAcademyMod.id("textures/gui/cosmetics/shop_button.png");
    public static final Identifier COSMETIC_TILE = StarAcademyMod.id("textures/gui/cosmetics/tile.png");

    public static final int BACKGROUND_SIZE_X = 345;
    public static final int BACKGROUND_SIZE_Y = 207;

    private int minX;
    private int minY;
    private Float yaw;
    private boolean draggingAvatar;
    private final Map<String, CosmeticButtonWidget> slotButtons;
    private final Map<String, CosmeticButtonWidget> cosmeticButtons;

    public CosmeticsScreen() {
        super(Text.literal("Cosmetics"));
        this.yaw = null;
        this.slotButtons = new HashMap<>();
        this.cosmeticButtons = new HashMap<>();
    }

    @Override
    protected void init() {
        super.init();
        this.minX = (this.width - BACKGROUND_SIZE_X) / 2;
        this.minY = (this.height - BACKGROUND_SIZE_Y) / 2;

        List<CosmeticSlot> slots = new ArrayList<>(StarAcademyMod.RESOURCES.getSlots().values());
        this.slotButtons.clear();

        for (int i = 0; i < slots.size(); i++) {
            CosmeticSlot slot = slots.get(i);
            int x = this.minX + 21;
            int y = this.minY + 46 + 18 * i + 1;

            CosmeticButtonWidget button = CosmeticButtonWidget.ofSlot(x, y, slot,
                    clicked -> this.triggerInventorySlot(slot.getId()));

            this.slotButtons.put(slot.getId(), button);
            this.addDrawableChild(button);
        }

        this.addDrawableChild(CosmeticButtonWidget.ofTab(
            this.minX + 15 + 3, this.minY + 15 + 3, "Inventory", "All your cosmetics are here.",
            INVENTORY_BUTTON, button -> {}
        ).setTriggered(true));

        this.addDrawableChild(CosmeticButtonWidget.ofTab(
            this.minX + 47 + 3, this.minY + 15 + 3, "Shop", "Coming soon!",
            SHOP_BUTTON, button -> {}
        ).setTriggered(false));

        this.triggerInventorySlot(slots.getFirst().getId());
    }

    public void triggerInventorySlot(String slotId) {
        CosmeticButtonWidget clickedButton = this.slotButtons.get(slotId);

        if (clickedButton != null) {
            for (CosmeticButtonWidget slotButton : this.slotButtons.values()) {
                slotButton.setTriggered(false);
            }

            clickedButton.setTriggered(true);
        }

        List<Cosmetic> cosmetics = StarAcademyMod.RESOURCES.getCosmetics().values().stream()
                .filter(cosmetic -> cosmetic.getSlots().contains(slotId))
                .toList();

        this.cosmeticButtons.values().forEach(this::remove);
        this.cosmeticButtons.clear();

        for (int i = 0; i < cosmetics.size(); i++) {
            Cosmetic cosmetic = cosmetics.get(i);
            int x = this.minX + 46 + 4 + (i % 4) * 30;
            int y = this.minY + 46 + 4 + (i / 4) * 30;

            CosmeticButtonWidget button = CosmeticButtonWidget.ofTile(x, y, cosmetic, COSMETIC_TILE,
                    clicked -> this.triggerInventoryCosmeticSlot(cosmetic.getId()));

            this.cosmeticButtons.put(cosmetic.getId(), button);
            this.addDrawableChild(button);
        }
    }

    public void triggerInventoryCosmeticSlot(String slotId) {
        CosmeticButtonWidget clickedButton = this.cosmeticButtons.get(slotId);

        if (clickedButton != null) {
            for (CosmeticButtonWidget slotButton : this.cosmeticButtons.values()) {
                slotButton.setTriggered(false);
            }

            clickedButton.setTriggered(true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int size = 60;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            context.getMatrices().push();
            context.enableScissor(this.minX + 185, this.minY + 28, this.minX + 318, this.minY + 180);
            this.drawEntity(context, this.minX + BACKGROUND_SIZE_X - 90, this.minY + 162, size,
                    this.minX + BACKGROUND_SIZE_X - 90 - mouseX,
                    this.minY + 85 - mouseY, player, delta);
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int minX = this.minX + 185;
        int minY = this.minY + 28;
        int maxX = this.minX + 318;
        int maxY = this.minY + 180;

        if (mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY) {
            if (button == 0) {
                this.draggingAvatar = true;
            } else if (button == 1) {
                this.yaw = null;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.draggingAvatar = false;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.draggingAvatar) {
            if (this.yaw == null) {
                this.yaw = 0.0f;
            }

            this.yaw += (float)deltaX / 10.0f;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public void drawEntity(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity, float delta) {
        float yaw = this.yaw != null ? 0.0f : (float) Math.atan(mouseX / 40.0F);
        float pitch = (float) Math.atan(mouseY / 40.0F);
        Quaternionf extraRotation = (new Quaternionf()).rotateX(-8.0f * (float)Math.PI / 180.0f);
        Quaternionf baseRotation = (new Quaternionf()).rotateZ((float)Math.PI).mul(extraRotation).rotateY(
                this.yaw != null ? this.yaw : 0.0f
        );
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
        this.drawEntity(context, x, y, size, baseRotation, extraRotation, entity, delta);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
    }

    public void drawEntity(DrawContext context, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity, float delta) {
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
