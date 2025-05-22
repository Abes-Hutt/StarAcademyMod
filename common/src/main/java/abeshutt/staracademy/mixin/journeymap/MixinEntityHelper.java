package abeshutt.staracademy.mixin.journeymap;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.compat.journeymap.JourneyMapCompat;
import journeymap.client.model.entity.EntityHelper;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = { "journeymap.client.model.entity.EntityHelper" })
public class MixinEntityHelper {

    @Inject(method = "getIconData", at = @At(value = "RETURN"), remap = false)
    private static void getIconData(Entity entity, CallbackInfoReturnable<EntityHelper.IconData> ci) {
        if(JourneyMapCompat.shouldLogIconData()) {
            StarAcademyMod.LOGGER.warn(ci.getReturnValue());
        }
    }

    @Redirect(method = "getIconData", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"), remap = false)
    private static boolean equals(String instance, Object object) {
        if("cobblemon".equals(object)) {
            return "cobblemon".equals(instance) || JourneyMapCompat.isCobblemonLike(instance);
        }

        return instance.equals(object);
    }

}
