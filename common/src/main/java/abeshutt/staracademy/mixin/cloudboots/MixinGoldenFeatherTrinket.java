package abeshutt.staracademy.mixin.cloudboots;

//import com.tiviacz.cloudboots.TrinketsCompat;
//import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//@Mixin(TrinketsCompat.GoldenFeatherTrinket.class)
public class MixinGoldenFeatherTrinket {

    /*
    @Redirect(method = "lambda$tick$0", at = @At(value = "INVOKE", target = "Lcom/tiviacz/cloudboots/TrinketsCompat$GoldenFeatherTrinket;onBreak(Lnet/minecraft/item/ItemStack;Ldev/emi/trinkets/api/SlotReference;Lnet/minecraft/entity/LivingEntity;)V"), require = 0)
    void onBreak(TrinketsCompat.GoldenFeatherTrinket instance, ItemStack stack, SlotReference slotReference, LivingEntity entity) {
        entity.playEquipmentBreakEffects(stack);
    }*/

}
