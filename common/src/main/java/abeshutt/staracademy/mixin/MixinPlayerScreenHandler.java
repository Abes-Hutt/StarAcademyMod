package abeshutt.staracademy.mixin;

import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.proxy.ProxyStarBadges;
import abeshutt.staracademy.screen.StarBadgeScreenHandler;
import abeshutt.staracademy.world.data.save.StarBadgeData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Inject as early as possible since the trinkets mod is too sensitive to slot ids changing.
@Mixin(value = PlayerScreenHandler.class, priority = 0)
public abstract class MixinPlayerScreenHandler<I extends RecipeInput, R extends Recipe<I>> extends AbstractRecipeScreenHandler<I, R> implements ProxyStarBadges {

    @Unique private StarBadgeScreenHandler starBadges;

    public MixinPlayerScreenHandler(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Override
    public StarBadgeScreenHandler getHandler() {
        return this.starBadges;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void ctor(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        if(onServer && owner.getServer() != null) {
            StarBadgeData data = ModWorldData.STAR_BADGE.getGlobal(owner.getServer());
            this.starBadges = new StarBadgeScreenHandler(this, data.getOrCreate(owner));
        } else {
            this.starBadges = new StarBadgeScreenHandler(this, new SimpleInventory(10) {
                @Override
                public ItemStack getStack(int slot) {
                    return StarBadgeData.CLIENT.isEnabled() ? super.getStack(slot) : ItemStack.EMPTY;
                }
            });
        }
    }

}
