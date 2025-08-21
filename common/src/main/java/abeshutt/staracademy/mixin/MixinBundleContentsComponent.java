package abeshutt.staracademy.mixin;

import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContentsComponent.class)
public class MixinBundleContentsComponent {

    @Inject(method = "getOccupancy(Lnet/minecraft/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;", at = @At("RETURN"), cancellable = true)
    private static void getOccupancy(ItemStack stack, CallbackInfoReturnable<Fraction> ci) {
        ci.setReturnValue(Fraction.getFraction(1, Integer.MAX_VALUE));
    }

}
