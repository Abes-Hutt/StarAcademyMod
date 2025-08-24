package abeshutt.staracademy.screen;

import com.cobblemon.mod.common.CobblemonSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.cobblemon.mod.common.api.gui.GuiUtilsKt.blitk;
import static com.cobblemon.mod.common.util.MiscUtilsKt.cobblemonResource;

public class SlotButtonWidget extends ClickableWidget {

    private static final Identifier slotResource = cobblemonResource("textures/gui/pokedex/pokedex_slot.png");
    private static final Identifier slotHighlight = cobblemonResource("textures/gui/pokedex/slot_select.png");

    private final Runnable onClick;

    public SlotButtonWidget(int x, int y, int width, int height, Text message, Runnable onClick) {
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
                    0x55FFFFFF, 0x55FFFFFF);
        }

        blitk(matrices, slotResource, 0, 0,
                this.height, this.width);

        matrices.push();
        matrices.translate((this.getWidth() - width) / 2.0F + 0.5F, (this.getHeight() - height) / 2.0F + 0.5F, 0);
        context.drawText(text, this.getMessage(), 0, 0, 0xFFFFFFFF, true);
        matrices.pop();

        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX > this.getX() && mouseX <= this.getX() + this.getWidth()
                && mouseY > this.getY() && mouseY <= this.getY() + this.getHeight()) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(CobblemonSounds.POKEDEX_CLICK, 1.0F));
            this.onClick.run();
            return true;
        }

        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

}
