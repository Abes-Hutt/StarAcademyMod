package abeshutt.staracademy.mixin;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.api.AcademyClient;
import abeshutt.staracademy.api.packet.AcademyPackets;
import abeshutt.staracademy.proxy.ProxyAcademyClient;
import abeshutt.staracademy.screen.overlay.SplashLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements ProxyAcademyClient {

    @Shadow @Nullable public Screen currentScreen;

    @Shadow public abstract void onResolutionChanged();

    @Unique private AcademyClient client;

    @Override
    public AcademyClient getClient() {
        return this.client;
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;createUserApiService(Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;Lnet/minecraft/client/RunArgs;)Lcom/mojang/authlib/minecraft/UserApiService;", shift = At.Shift.AFTER))
    private void init(RunArgs args, CallbackInfo ci) {
        AcademyPackets.register();
        this.client = new AcademyClient((MinecraftClient)(Object)this);
        this.client.connect();
        this.client.awaitCodex();
        this.client.getCodex().setComplete(true);
        SplashLoader.load((MinecraftClient)(Object)this);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        this.client.tick();
        StarAcademyMod.CLIENT_TICKERS.forEach(Runnable::run);
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void stop(CallbackInfo ci) {
        this.client.disconnect();
    }

    @Inject(method = "setScreen", at = @At(value = "RETURN"))
    private void setScreen(Screen screen, CallbackInfo ci) {
        if (this.currentScreen instanceof TitleScreen) {
            this.onResolutionChanged();
        }
    }

}
