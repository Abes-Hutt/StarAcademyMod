package abeshutt.staracademy.mixin.numismaticoverhaul;

import com.glisco.numismaticoverhaul.block.ShopBlock;
import com.glisco.numismaticoverhaul.block.ShopBlockEntity;
import com.glisco.numismaticoverhaul.currency.CurrencyConverter;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShopBlock.class)
public abstract class MixinShopBlock extends BlockWithEntity {

    @Shadow @Final private boolean inexhaustible;

    protected MixinShopBlock(Settings settings) {
        super(settings);
    }

    /**
     * @author StarAcademyMod
     * @reason Prevent inexhaustible shops from dropping currency.
     */
    @Overwrite
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.getBlock() != newState.getBlock()) {
            if(world.getBlockEntity(pos) instanceof ShopBlockEntity shop) {
                shop.getMerchant().setCustomer(null);

                if(!this.inexhaustible) {
                    CurrencyConverter.getAsValidStacks(shop.getStoredCurrency()).forEach(stack -> {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                    });
                }

                ItemScatterer.spawn(world, pos, shop);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

}
