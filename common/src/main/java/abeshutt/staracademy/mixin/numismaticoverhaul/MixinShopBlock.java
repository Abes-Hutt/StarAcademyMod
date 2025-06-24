package abeshutt.staracademy.mixin.numismaticoverhaul;

import com.glisco.numismaticoverhaul.block.ShopBlock;
import com.glisco.numismaticoverhaul.block.ShopBlockEntity;
import net.minecraft.block.BlockWithEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShopBlock.class)
public abstract class MixinShopBlock extends BlockWithEntity {

    @Shadow @Final private boolean inexhaustible;

    protected MixinShopBlock(Settings settings) {
        super(settings);
    }

    @Redirect(method = "onStateReplaced", at = @At(value = "INVOKE", target = "Lcom/glisco/numismaticoverhaul/block/ShopBlockEntity;getStoredCurrency()J", remap = false))
    public long onStateReplaced(ShopBlockEntity shop) {
        if(this.inexhaustible) {
            return 0L;
        }

        return shop.getStoredCurrency();
    }

}
