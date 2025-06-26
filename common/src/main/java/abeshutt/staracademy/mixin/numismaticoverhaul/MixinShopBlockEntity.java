package abeshutt.staracademy.mixin.numismaticoverhaul;

import abeshutt.staracademy.config.ShopConfig;
import abeshutt.staracademy.init.ModConfigs;
import com.glisco.numismaticoverhaul.block.ShopBlockEntity;
import com.glisco.numismaticoverhaul.block.ShopOffer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ShopBlockEntity.class)
public abstract class MixinShopBlockEntity {

    @Shadow private List<ShopOffer> offers;

    @Shadow public abstract void markDirty();

    @Unique private String reference;
    @Unique private ShopConfig lastConfig;

    @Inject(method = "tick(Lnet/minecraft/world/World;)V", at = @At("HEAD"))
    private void tick(World world, CallbackInfo ci) {
        if(world.isClient()) {
            return;
        }

        if (this.reference != null && this.lastConfig != ModConfigs.SHOP) {
            this.offers = ModConfigs.SHOP.parseOffers(this.reference, world.getRegistryManager())
                    .orElse(this.offers);
            this.lastConfig = ModConfigs.SHOP;
            this.markDirty();
        }
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        if(this.reference != null) {
            tag.putString("reference", this.reference);
        }
    }

    @Inject(method = "readNbt", at = @At("RETURN"))
    public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        if(tag.contains("reference", NbtElement.STRING_TYPE)) {
            this.reference = tag.getString("reference");
        } else {
            this.reference = null;
        }
    }

}
