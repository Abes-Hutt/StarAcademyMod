package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModItems;
import abeshutt.staracademy.net.ConfirmAcceptanceLetterC2SPacket;
import com.cobblemon.mod.common.CobblemonSounds;
import dev.architectury.hooks.item.ItemStackHooks;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static com.cobblemon.mod.common.api.gui.GuiUtilsKt.blitk;
import static com.cobblemon.mod.common.util.MiscUtilsKt.cobblemonResource;

public class AcceptanceLetterScreen extends Screen {

    private static final Identifier PAGES = StarAcademyMod.id("textures/gui/acceptance_letter.png");

    private final boolean enrolled;

    public AcceptanceLetterScreen(boolean enrolled) {
        super(Text.translatable(ModItems.ACCEPTANCE_LETTER.get().getTranslationKey()));
        this.enrolled = enrolled;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if(!this.enrolled) {
            this.addDrawableChild(new EnrolButton(centerX - 60 / 2, centerY - 15 / 2 + 75,
                    60, 15, Text.translatable("item.academy.acceptance_letter.enroll"), () -> {
                NetworkManager.sendToServer(new ConfirmAcceptanceLetterC2SPacket());
                this.close();
            }));
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        context.getMatrices().push();
        context.getMatrices().translate(centerX, centerY, 0.0F);
        context.getMatrices().scale(1.25F, 1.25F, 1.0F);
        context.getMatrices().translate(-132.0F / 2.0F, -164.0F / 2.0F, 0.0F);
        context.drawTexture(PAGES, 0, 0, 0, 0, 132, 165, 256, 256);
        context.getMatrices().pop();

        MutableText text = Text.empty();
        text.append(Text.translatable("item.academy.acceptance_letter.content"));
        List<OrderedText> parts = this.textRenderer.wrapLines(text, 145);

        context.getMatrices().push();
        context.getMatrices().translate(8.0F, 8.0F, 0.0F);
        context.getMatrices().translate(centerX, centerY, 0.0F);

        for(OrderedText part : parts) {
            context.drawText(this.textRenderer, part, (int)(-132.0F / 2.0F * 1.25F),
                    (int)(-164.0F / 2.0F * 1.25F), 0xFF7A6B56, false);
            context.getMatrices().translate(0.0F, this.textRenderer.fontHeight, 0.0F);
        }

        context.getMatrices().pop();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static class EnrolButton extends ClickableWidget {
        private final Runnable onClick;

        public EnrolButton(int x, int y, int width, int height, Text message, Runnable onClick) {
            super(x, y, width, height, message);
            this.onClick = onClick;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            MatrixStack matrices = context.getMatrices();
            TextRenderer text = MinecraftClient.getInstance().textRenderer;
            int width = text.getWidth(this.getMessage());
            int height = text.fontHeight;

            matrices.push();
            matrices.translate(this.getX(), this.getY(), 0);

            if(this.isHovered()) {
                context.fillGradient(0, 0, this.getWidth(), this.getHeight(),
                        0x117A6B56, 0x117A6B56);
            }

            context.drawBorder(0, 0, this.getWidth(), this.getHeight(), 0xFF7A6B56);

            matrices.push();
            matrices.translate((this.getWidth() - width) / 2.0F + 0.5F, (this.getHeight() - height) / 2.0F + 0.5F, 0);
            context.drawText(text, this.getMessage(), 0, 0, 0xFF7A6B56, false);
            matrices.pop();

            matrices.pop();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(mouseX > this.getX() && mouseX <= this.getX() + this.getWidth()
                    && mouseY > this.getY() && mouseY <= this.getY() + this.getHeight()) {
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.onClick.run();
                return true;
            }

            return false;
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }

}
