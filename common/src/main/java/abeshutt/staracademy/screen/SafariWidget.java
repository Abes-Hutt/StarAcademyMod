package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
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

import java.time.Duration;
import java.time.Instant;

public class SafariWidget implements Drawable {

    public static final Identifier SAFARI_TIMER = StarAcademyMod.id("textures/gui/safari_timer.png");
    public static final int SAFARI_TIMER_HEIGHT = 24;
    public static final int SAFARI_TIMER_WIDTH = 27;

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

        var endTime = StarAcademyMod.SAFARI_TIMER;
        var now = Instant.now();

        int color = this.getTextColor(now, endTime);
        String text = this.formatTimeString(now, endTime);

        var secondsRemaining = Duration.between(now, endTime).getSeconds();

        matrices.push();
        matrices.scale(1.1F, 1.1F, 1.1F);
        context.drawText(textRenderer, text, -textRenderer.getWidth(text) / 2, 12, color, true);
        matrices.pop();

        float wobble = (float)Math.sin(Math.PI * 2.0F / 80.0F * secondsRemaining * 20 * (secondsRemaining <= 60 ? 5.0F : 1.0F));

        matrices.push();
        matrices.translate(0.0F, -SAFARI_TIMER_HEIGHT / 2.0F, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(wobble * 30.0F));
        matrices.translate(-SAFARI_TIMER_WIDTH / 2.0F, 0.0F, 0);
        context.drawTexture(SAFARI_TIMER, 0, -2, 3, 4, SAFARI_TIMER_WIDTH, SAFARI_TIMER_HEIGHT, 32, 32);
        matrices.pop();

        matrices.pop();
    }

    protected int getTextColor(Instant now, Instant end) {
        if (Duration.between(now, end).toSeconds() <= 60) {
            return 0xFF_E85959;
        }
        return 0xFF_FFFFFF;
    }

    protected String formatTimeString(Instant now, Instant end) {
        var duration = Duration.between(now, end);
        var seconds = duration.toSeconds();

        var hours = (seconds / 3600);
        var minutes = (seconds % 3600) / 60;
        var secs = seconds % 60;

        return hours > 0
                ? String.format("%02d:%02d:%02d", hours, minutes, secs)
                : String.format("%02d:%02d", minutes, secs);
    }

}
