package abeshutt.staracademy.mixin.sophisticated;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.CookingLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.CookingUpgradeConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(CookingLogic.class)
public class MixinCookingLogic {

    @Shadow @Final @Mutable private Predicate<ItemStack> isFuel;

    @Inject(method = "<init>(Lnet/minecraft/item/ItemStack;Ljava/util/function/Consumer;Ljava/util/function/Predicate;Ljava/util/function/Predicate;Lnet/p3pp3rf1y/sophisticatedcore/upgrades/cooking/CookingUpgradeConfig;Lnet/minecraft/recipe/RecipeType;F)V", at = @At("RETURN"))
    public void init(ItemStack upgrade, Consumer<ItemStack> saveHandler, Predicate<ItemStack> isFuel,
                     Predicate<ItemStack> isInput, CookingUpgradeConfig cookingUpgradeConfig,
                     RecipeType<AbstractCookingRecipe> recipeType, float burnTimeModifier, CallbackInfo ci) {
        this.isFuel = this.isFuel.or(stack -> stack.getItem() == Items.WATER_BUCKET);
    }

}
