package abeshutt.staracademy.mixin.modernfix;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(targets = { "org.embeddedt.modernfix.util.LRUMap" })
public class MixinLRUMap extends Object2ObjectLinkedOpenHashMap<Object, Object> {

    @Redirect(method = "dropEntriesToMeetSize", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z", remap = false), remap = false)
    public boolean dropEntriesToMeetSize(Set set, Object object) {
        try {
            return set.contains(object);
        } catch(Exception e) {
            return false;
        }
    }

}
