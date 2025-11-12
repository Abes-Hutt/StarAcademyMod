package abeshutt.staracademy.screen.widget;

import abeshutt.staracademy.util.ClientScheduler;
import abeshutt.staracademy.util.ColorBlender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;

public class StreamWidget implements Drawable, Element, Widget, Selectable {

    public static final int HEIGHT = 20;

    private final ImageTexture profilePicture;
    private final String login;
    private final String name;
    private final boolean live;

    private boolean focused;
    private boolean hovered;
    private int x, y;
    private final int width, height;

    private float hoverTime;

    public StreamWidget(ImageTexture profilePicture, String login, String name, boolean live, int x, int y, int width) {
        this.profilePicture = profilePicture;
        this.login = login;
        this.name = name;
        this.live = live;

        this.focused = false;
        this.hovered = false;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = HEIGHT;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.isWithinBounds(mouseX, mouseY)) {
            if (!this.hovered) {
                MinecraftClient.getInstance().getSoundManager()
                        .play(PositionedSoundInstance.master(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 4.0F));
            }

            this.hovered = true;
        } else {
            this.hovered = false;
        }

        this.hovered = this.isWithinBounds(mouseX, mouseY);

        if (!this.hovered) {
            this.hoverTime = 0;
        }

        context.getMatrices().push();
        context.getMatrices().translate(this.x, this.y, 0);

        context.getMatrices().push();
        context.getMatrices().scale(20.0f / this.profilePicture.getWidth(),
                20.0f / this.profilePicture.getHeight(), 1.0f);
        context.drawTexture(this.profilePicture.getId(), 0, 0, 0, 0,
                this.profilePicture.getWidth(), this.profilePicture.getHeight(),
                this.profilePicture.getWidth(), this.profilePicture.getHeight());
        context.getMatrices().pop();

        context.getMatrices().push();
        context.getMatrices().translate(23.25f, 2.75f, 0.0f);
        MutableText name = this.hovered
                ? styleText(this.name, ClientScheduler.getTick(delta), 10.0f)
                : Text.literal(this.name);

        context.drawText(MinecraftClient.getInstance().textRenderer,
                name.setStyle(Style.EMPTY.withObfuscated(this.hovered && this.hoverTime < 0)),
                    0, 0, 0xFFFFFF, false);
        context.getMatrices().pop();

        context.getMatrices().push();
        context.getMatrices().scale(0.8f, 0.8f, 0.8f);
        context.getMatrices().translate(23.25f / 0.8f, 12.25f / 0.8f, 0.0f);
        Text state = this.live ? Text.empty()
                    .append(Text.literal("Live ").formatted(Formatting.GRAY))
                    .append(Text.literal("⬤").setStyle(Style.EMPTY.withColor(0x6441A5)))
                : Text.empty()
                    .append(Text.literal("Offline ").formatted(Formatting.GRAY))
                    .append(Text.literal("⬤").formatted(Formatting.GRAY));
        context.drawText(MinecraftClient.getInstance().textRenderer,
                state, 0, 0, 0xFFFFFF, false);
        context.getMatrices().pop();

        context.getMatrices().pop();

        this.hoverTime += delta;
    }

    private static MutableText styleText(String string, double time, float offset) {
        ColorBlender blender = new ColorBlender(1.5F)
                .add(0xf48396, 250.0F)
                .add(0x86c5fb, 250.0F);
        MutableText text = Text.empty();
        int count = 0;

        for(int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            text = text.append(Text.literal(String.valueOf(c))
                    .setStyle(Style.EMPTY.withColor(blender.getColor(time + count * offset))));
            if(c != ' ') count++;
        }

        return text;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isWithinBounds(mouseX, mouseY)) {
            MinecraftClient.getInstance().getSoundManager()
                    .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            try {
                Util.getOperatingSystem().open(new URL("https://www.twitch.tv/" + this.login).toURI());
            } catch (MalformedURLException | URISyntaxException ignored) {

            }

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return button == 0;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return button == 0;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.isWithinBounds(mouseX, mouseY);
    }

    public boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY()
                && mouseX < this.getX() + this.getWidth() && mouseY < this.getY() + this.getHeight();
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isSelected() {
        return this.isHovered() || this.isFocused();
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return !this.isFocused() ? GuiNavigationPath.of(this) : null;
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return new ScreenRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public SelectionType getType() {
        if (this.isFocused()) {
            return Selectable.SelectionType.FOCUSED;
        } else {
            return this.hovered ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {

    }

}
