package abeshutt.staracademy.mixin;

import abeshutt.staracademy.block.entity.renderer.DynamicResourcePack;
import net.minecraft.resource.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

import static net.minecraft.resource.ResourcePackProfile.InsertionPosition.TOP;

@Mixin(FileResourcePackProvider.class)
public abstract class MixinFileResourcePackProvider {

    @Shadow @Final private ResourceType type;
    @Shadow @Final private ResourcePackSource source;

    @Inject(method = "register", at = @At("RETURN"))
    public void register(Consumer<ResourcePackProfile> profileAdder, CallbackInfo ci) {
        if(this.type == ResourceType.SERVER_DATA) {
            return;
        }

        DynamicResourcePack.open(this.type, this.source, (path, packFactory, info) -> {
            ResourcePackProfile resourcePackProfile = ResourcePackProfile.create(info, packFactory, this.type,
                    new ResourcePackPosition(true, TOP, true));

            if(resourcePackProfile != null) {
                profileAdder.accept(resourcePackProfile);
            }
        });
    }

}
