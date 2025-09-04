package abeshutt.staracademy.mixin.megashowdown;

import com.cobblemon.yajatkaul.mega_showdown.event.ModEvents;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModEvents.class)
public class MixinModEvents {

    @Inject(method = "createExplorerMap", at = @At("HEAD"), cancellable = true)
    private static void createExplorerMap(World world, BlockPos pos, Entity entity, CallbackInfoReturnable<ItemStack> ci) {
        ci.setReturnValue(new ItemStack(Items.PAPER));
    }

}
