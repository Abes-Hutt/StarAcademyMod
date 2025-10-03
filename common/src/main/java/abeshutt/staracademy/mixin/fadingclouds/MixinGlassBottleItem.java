package abeshutt.staracademy.mixin.fadingclouds;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(value = GlassBottleItem.class, priority = 1500)
public class MixinGlassBottleItem {

    @TargetHandler(mixin = "me.lemo.fading_clouds.mixin.GlassBottleItemMixin", name = "fading_clouds$collectCloudsOnUse")
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;setReturnValue(Ljava/lang/Object;)V", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    public void collectCloudsOnUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, CallbackInfo ci) {
        ci.cancel();
    }


}
