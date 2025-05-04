package abeshutt.staracademy.mixin.bountiful;

//@Mixin(targets = { "io.ejekta.bountiful.content.BountyItem" }, remap = false)
public class MixinBountyItem {

    /*
    @Inject(method = "getName", at = @At("HEAD"), cancellable = true, require = 0)
    public void getName(ItemStack stack, CallbackInfoReturnable<Text> ci) {
        if(Platform.getEnv() == EnvType.SERVER) {
            ci.setReturnValue(Text.literal("Bounty Item"));
        }
    }*/

}
