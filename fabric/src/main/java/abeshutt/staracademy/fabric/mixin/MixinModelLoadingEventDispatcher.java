package abeshutt.staracademy.fabric.mixin;

import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingEventDispatcher;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelLoadingEventDispatcher.class)
public class MixinModelLoadingEventDispatcher {

    @Inject(method = "resolveModel", at = @At("HEAD"), cancellable = true, remap = false)
    public void resolveModel(Identifier id, CallbackInfoReturnable<UnbakedModel> ci) {
        if(id == null) {
            ci.setReturnValue(null);
        }
    }

}