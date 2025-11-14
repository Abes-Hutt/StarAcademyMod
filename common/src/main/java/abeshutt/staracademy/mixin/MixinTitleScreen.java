package abeshutt.staracademy.mixin;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.screen.widget.StreamListWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    @Shadow @Final private static Text COPYRIGHT;

    @Shadow private long backgroundFadeStart;
    @Shadow private boolean doBackgroundFade;
    @Shadow private float backgroundAlpha;
    @Shadow private SplashTextRenderer splashText;
    @Shadow private RealmsNotificationsScreen realmsNotificationGui;

    @Unique private ButtonWidget academy$singleplayerButton;
    @Unique private ButtonWidget academy$multiplayerButton;
    @Unique private ButtonWidget academy$joinSmpButton;
    @Unique private ButtonWidget academy$websiteButton;
    @Unique private ButtonWidget academy$discordButton;
    @Unique private ButtonWidget academy$rentServerButton;
    @Unique private ButtonWidget academy$optionsButton;
    @Unique private ButtonWidget academy$quitButton;
    @Unique private PressableTextWidget academy$copyrightButton;
    @Unique private PressableTextWidget academy$ostCreditButton;
    @Unique private PressableTextWidget academy$graphicCreditButton;

    @Shadow protected abstract boolean isRealmsNotificationsGuiDisplayed();
    @Shadow protected abstract Text getMultiplayerDisabledText();

    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo ci) {
        if (!ModConfigs.SCREEN.isEnabled()) {
            return;
        }

        this.academy$singleplayerButton = ButtonWidget.builder(Text.translatable("menu.singleplayer"),
                        button -> this.client.setScreen(new SelectWorldScreen(this)))
                .dimensions(0, 0, 0, 0)
                .build();

        Text text = this.getMultiplayerDisabledText();
        Tooltip tooltip = text != null ? Tooltip.of(text) : null;
        this.academy$multiplayerButton = ButtonWidget.builder(Text.translatable("menu.multiplayer"), button -> {
            Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.client.setScreen(screen);
        }).dimensions(0, 0, 0, 0).tooltip(tooltip).build();
        this.academy$multiplayerButton.active = text == null;

        this.academy$joinSmpButton = ButtonWidget.builder(Text.literal("Join an SMP!"),
                        button -> {})
                .dimensions(0, 0, 0, 0)
                .build();

        this.academy$websiteButton = ButtonWidget.builder(Text.literal("Website"),
                        button -> {})
                .dimensions(0, 0, 0, 0)
                .build();

        this.academy$discordButton = ButtonWidget.builder(Text.literal("Discord"),
                        button -> {})
                .dimensions(0, 0, 0, 0)
                .build();

        this.academy$rentServerButton = ButtonWidget.builder(Text.literal("Rent a server here!"),
                        button -> {})
                .dimensions(0, 0, 0, 0)
                .build();

        this.academy$optionsButton = ButtonWidget.builder(Text.translatable("menu.options"),
                        button -> this.client.setScreen(new OptionsScreen(this, this.client.options)))
                .dimensions(0, 0, 0, 0)
                .build();

        this.academy$quitButton = ButtonWidget.builder(Text.translatable("menu.quit"),
                button -> this.client.scheduleStop())
                .dimensions(0, 0, 0, 0)
                .build();

        this.academy$copyrightButton = new PressableTextWidget(0, 0, 0, 0,
                COPYRIGHT, button -> this.client.setScreen(new CreditsAndAttributionScreen(this)), this.textRenderer);

        this.academy$ostCreditButton = new PressableTextWidget(0, 0, 0, 0,
                Text.literal("OST by LilyPichu"), button -> {}, this.textRenderer);

        this.academy$graphicCreditButton = new PressableTextWidget(0, 0, 0, 0,
                Text.literal("Graphic & Animation by Polypuff"), button -> {}, this.textRenderer);

        this.addDrawableChild(this.academy$singleplayerButton);
        this.addDrawableChild(this.academy$multiplayerButton);
        this.addDrawableChild(this.academy$joinSmpButton);
        this.addDrawableChild(this.academy$websiteButton);
        this.addDrawableChild(this.academy$discordButton);
        this.addDrawableChild(this.academy$rentServerButton);
        this.addDrawableChild(this.academy$optionsButton);
        this.addDrawableChild(this.academy$quitButton);
        this.addDrawableChild(this.academy$copyrightButton);
        this.addDrawableChild(this.academy$ostCreditButton);
        this.addDrawableChild(this.academy$graphicCreditButton);

        if (this.realmsNotificationGui == null) {
            this.realmsNotificationGui = new RealmsNotificationsScreen();
        }

        if (this.isRealmsNotificationsGuiDisplayed()) {
            this.realmsNotificationGui.init(this.client, this.width, this.height);
        }

        ci.cancel();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ModConfigs.SCREEN.isEnabled()) {
            return;
        }

        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }

        float f = 1.0F;
        if (this.doBackgroundFade) {
            float g = (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 2000.0F;
            if (g > 1.0F) {
                this.doBackgroundFade = false;
                this.backgroundAlpha = 1.0F;
            } else {
                g = MathHelper.clamp(g, 0.0F, 1.0F);
                f = MathHelper.clampedMap(g, 0.5F, 1.0F, 0.0F, 1.0F);
                this.backgroundAlpha = MathHelper.clampedMap(g, 0.0F, 0.5F, 0.0F, 1.0F);
            }

            for (Element child : this.children()) {
                if (child instanceof ClickableWidget widget) {
                    widget.setAlpha(f);
                } else if (child instanceof StreamListWidget widget) {
                    widget.setAlpha(f);
                }
            }
        }

        this.renderPanoramaBackground(context, delta);
        int i = MathHelper.ceil(f * 255.0F) << 24;
        if ((i & -67108864) != 0) {
            super.render(context, mouseX, mouseY, delta);
            //this.logoDrawer.draw(context, this.width, f);

            Identifier logoId = StarAcademyMod.id("splash/logo");

            if (MinecraftClient.getInstance().getTextureManager()
                    .getOrDefault(logoId, null) instanceof NativeImageBackedTexture texture) {
                if (texture.getImage() != null) {
                    int width = texture.getImage().getWidth();
                    int height = texture.getImage().getHeight();
                    context.getMatrices().push();
                    float scale = (this.width / 3.0f) / width;
                    int logoWidth = (int)(width * scale);
                    int logoHeight = (int)(height * scale);
                    int rightMargin = 6;
                    int topMargin = 6;
                    context.getMatrices().scale(scale, scale, 1.0f);
                    context.getMatrices().translate((this.width - logoWidth - rightMargin) / scale, topMargin / scale, 1.0f);
                    context.drawTexture(logoId, 0, 0, 0, 0, width, height, width, height);
                    context.getMatrices().pop();

                    this.academy$singleplayerButton.setDimensions(logoWidth * 8 / 10, 20);
                    this.academy$singleplayerButton.setPosition(
                            this.width - logoWidth + (logoWidth - this.academy$singleplayerButton.getWidth()) / 2 - rightMargin,
                            logoHeight + topMargin + 2 + 4);

                    this.academy$multiplayerButton.setDimensions(logoWidth * 8 / 10, 20);
                    this.academy$multiplayerButton.setPosition(
                            this.width - logoWidth + (logoWidth - this.academy$multiplayerButton.getWidth()) / 2 - rightMargin,
                            logoHeight + topMargin + 2 + 28);

                    this.academy$joinSmpButton.setDimensions(logoWidth * 8 / 10, 20);
                    this.academy$joinSmpButton.setPosition(
                            this.width - logoWidth + (logoWidth - this.academy$joinSmpButton.getWidth()) / 2 - rightMargin,
                            logoHeight + topMargin + 2 + 52);

                    this.academy$websiteButton.setDimensions((this.academy$singleplayerButton.getWidth() - 4) / 2, 20);
                    this.academy$websiteButton.setPosition(
                            this.width - logoWidth + (logoWidth - this.academy$singleplayerButton.getWidth()) / 2 - rightMargin,
                            logoHeight + topMargin + 2 + 76);

                    this.academy$discordButton.setDimensions(this.academy$singleplayerButton.getWidth() - this.academy$websiteButton.getWidth() - 4, 20);
                    this.academy$discordButton.setPosition(
                            this.width - logoWidth + (logoWidth - this.academy$singleplayerButton.getWidth()) / 2 - rightMargin + this.academy$websiteButton.getWidth() + 4,
                            logoHeight + topMargin + 2 + 76);

                    this.academy$rentServerButton.setDimensions(logoWidth * 8 / 10, 20);
                    this.academy$rentServerButton.setPosition(
                            this.width - logoWidth + (logoWidth - this.academy$rentServerButton.getWidth()) / 2 - rightMargin,
                            logoHeight + topMargin + 2 + 100);

                    this.academy$optionsButton.setDimensions((this.academy$singleplayerButton.getWidth() - 4) / 2, 20);
                    this.academy$optionsButton.setPosition(
                            this.width - logoWidth + (logoWidth - this.academy$singleplayerButton.getWidth()) / 2 - rightMargin,
                            logoHeight + topMargin + 2 + 124);

                    this.academy$quitButton.setDimensions(this.academy$singleplayerButton.getWidth() - this.academy$optionsButton.getWidth() - 4, 20);
                    this.academy$quitButton.setPosition(
                            this.width - logoWidth + (logoWidth - this.academy$singleplayerButton.getWidth()) / 2 - rightMargin + this.academy$optionsButton.getWidth() + 4,
                            logoHeight + topMargin + 2 + 124);

                    this.academy$copyrightButton.setDimensions(this.textRenderer.getWidth(this.academy$copyrightButton.getMessage()), 10);
                    this.academy$copyrightButton.setPosition(this.width - this.academy$copyrightButton.getWidth() - 2, this.height - this.academy$copyrightButton.getHeight());

                    this.academy$ostCreditButton.setDimensions(this.textRenderer.getWidth(this.academy$ostCreditButton.getMessage()), 10);
                    this.academy$ostCreditButton.setPosition(2, this.height - this.academy$ostCreditButton.getHeight() - 12);

                    this.academy$graphicCreditButton.setDimensions(this.textRenderer.getWidth(this.academy$graphicCreditButton.getMessage()), 10);
                    this.academy$graphicCreditButton.setPosition(2, this.height - this.academy$graphicCreditButton.getHeight());
                }
            }

            if (this.splashText != null && !this.client.options.getHideSplashTexts().getValue()) {
                //this.splashText.render(context, this.width, this.textRenderer, i);
            }

            if (this.isRealmsNotificationsGuiDisplayed() && f >= 1.0F) {
                RenderSystem.enableDepthTest();
                this.realmsNotificationGui.render(context, mouseX, mouseY, delta);
            }
        }

        ci.cancel();
    }

}
