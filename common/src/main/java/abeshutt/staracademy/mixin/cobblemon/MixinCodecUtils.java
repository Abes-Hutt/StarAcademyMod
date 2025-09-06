package abeshutt.staracademy.mixin.cobblemon;

import com.cobblemon.mod.common.util.codec.CodecUtils;
import com.mojang.serialization.Codec;
import kotlin.jvm.functions.Function0;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CodecUtils.class)
public class MixinCodecUtils {

    @Inject(method = "dynamicIntRange(Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;)Lcom/mojang/serialization/Codec;",
            at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void clinit(Function0<Integer> min, Function0<Integer> max, CallbackInfoReturnable<Codec<Integer>> cir) {
        cir.setReturnValue(Codec.INT);
    }

}
