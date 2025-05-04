package abeshutt.staracademy.mixin.scout;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(targets = { "pm.c7.scout.item.BaseBagItem" })
public abstract class MixinBaseBagItem {

    //@Shadow protected abstract void updateSlots(PlayerEntity player);

    //@Inject(method = { "method_7888", "inventoryTick" }, at = @At("HEAD"), require = 0)
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if(entity instanceof PlayerEntity player) {
            //this.updateSlots(player);
        }
    }

}
