package abeshutt.staracademy.screen;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModItems;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;

public class AcceptanceLetterScreen extends Screen {

    private static final Identifier PAGES = StarAcademyMod.id("textures/gui/acceptance_letter.png");

    public AcceptanceLetterScreen() {
        super(Text.translatable(ModItems.ACCEPTANCE_LETTER.get().getTranslationKey()));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        context.getMatrices().push();
        context.getMatrices().translate(centerX, centerY, 0.0F);
        context.getMatrices().scale(1.25F, 1.25F, 1.0F);
        context.getMatrices().translate(-132.0F / 2.0F, -164.0F / 2.0F, 0.0F);
        context.drawTexture(PAGES, 0, 0, 0, 0, 132, 165, 256, 256);
        context.getMatrices().pop();

        MutableText text = Text.empty();
        text.append(Text.literal("\nDear Trainer,\n\nYou have been selected for admission at our " +
                "prestigious academy. We would like to extend a heartfelt congratulations and hope you will find a " +
                "home here at our institution. Please confirm your acceptance below. \n\n Best regards,\n Your Mom"));
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

}
