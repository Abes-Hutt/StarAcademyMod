package abeshutt.staracademy.fabric.mixin;

import net.fabricmc.fabric.impl.resource.loader.ResourcePackSourceTracker;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourcePackSourceTracker.class)
public class MixinResourcePack {

    @Inject(method = "setSource", at = @At("HEAD"))
    private static void setSource(ResourcePack pack, ResourcePackSource source, CallbackInfo ci) {
        System.out.println("Loading " + pack.getId() + ", " + pack.getInfo().title());
    }

}
