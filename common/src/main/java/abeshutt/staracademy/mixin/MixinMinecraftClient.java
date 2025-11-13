package abeshutt.staracademy.mixin;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.api.AcademyClient;
import abeshutt.staracademy.api.packet.AcademyPackets;
import abeshutt.staracademy.proxy.ProxyAcademyClient;
import abeshutt.staracademy.screen.overlay.SplashLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements ProxyAcademyClient {

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

}
