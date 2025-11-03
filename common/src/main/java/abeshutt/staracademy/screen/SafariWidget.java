package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.world.data.save.SafariData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class SafariWidget implements Drawable {

    public static final Identifier SAFARI_TIMER = StarAcademyMod.id("textures/gui/safari_timer.png");
    public static final Identifier SAFARI_TIMER_ARROW = StarAcademyMod.id("textures/gui/safari_timer_arrow.png");

    public SafariWidget() {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;
        MatrixStack matrices = context.getMatrices();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        matrices.push();
        matrices.translate(92, MinecraftClient.getInstance().getWindow().getScaledHeight() - 25, 0);
        int iconWidth = 27;
        int iconHeight = 24;

        SafariData.Entry entry = SafariData.CLIENT.get(player.getUuid()).orElse(null);

        if(entry != null) {
            long timer = player.getWorld().getRegistryKey() == StarAcademyMod.SAFARI
                    ? entry.getTimeLeft() : SafariData.CLIENT.getTimeLeft();
            int color = this.getTextColor(timer);
            String text = this.formatTimeString(timer);

            matrices.push();
            matrices.scale(1.1F, 1.1F, 1.1F);
            context.drawText(textRenderer, text, -textRenderer.getWidth(text) / 2, 12, color, true);
            matrices.pop();

            if(player.getWorld().getRegistryKey() == StarAcademyMod.SAFARI) {
                float wobble = (float)Math.sin(Math.PI * 2.0F / 80.0F * timer * (timer <= 20 * 60 ? 5.0F : 1.0F));

                matrices.push();
                matrices.translate(0.0F, -iconHeight / 2.0F, 0);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(wobble * 30.0F));
                matrices.translate(-iconWidth / 2.0F, 0.0F, 0);
                context.drawTexture(SAFARI_TIMER, 0, -2, 3, 4, iconWidth, iconHeight, 32, 32);
                matrices.pop();
            } else {
                if(entry.getNearestPortal() != null) {
                    Vec3d target = Vec3d.ofCenter(entry.getNearestPortal());
                    double direction = Math.atan2(target.getZ() - player.getZ(), target.getX() - player.getX()) / (Math.PI * 2.0D);
                    double yaw = MathHelper.floorMod(player.getBodyYaw() / 360.0D, 1.0D);
                    double angle = MathHelper.floorMod(0.5D - (yaw - 0.25D - direction), 1.0F) * Math.PI * 2.0D;
                    matrices.push();
                    matrices.translate(0, -2, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float)angle));
                    matrices.translate(-iconWidth / 2.0F, -iconHeight / 2.0F, 0);
                    context.drawTexture(SAFARI_TIMER_ARROW, -3, -4, 0, 0, 32, 32, 32, 32);
                    matrices.pop();
                }

                matrices.push();
                matrices.translate(-iconWidth / 2.0F, -iconHeight / 2.0F, 0);
                context.drawTexture(SAFARI_TIMER, 0, -2, 3, 4, iconWidth, iconHeight, 32, 32);
                matrices.pop();
            }
        }

        matrices.pop();
    }

    protected int getTextColor(long time) {
        if(time < 60 * 20 && time % 10 < 5) {
            return 0xFF_E85959;
        }

        return 0xFF_FFFFFF;
    }

    protected String formatTimeString(long remainingTicks) {
        long seconds = (remainingTicks / 20) % 60;
        long minutes = ((remainingTicks / 20) / 60) % 60;
        long hours = ((remainingTicks / 20) / 60) / 60;
        return hours > 0
                ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }

}
