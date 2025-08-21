package abeshutt.staracademy.mixin.shutters;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.stehschnitzel.shutter.block.Shutter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Shutter.class)
public class MixinShutter {

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"), cancellable = true)
    private void update(BlockState state, Direction direction, BlockState neighborState, WorldAccess world,
                        BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> ci) {
        if(!(world instanceof World)) {
            ci.setReturnValue(state);
        }
    }

}
