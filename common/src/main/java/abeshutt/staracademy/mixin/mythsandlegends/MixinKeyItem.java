package abeshutt.staracademy.mixin.mythsandlegends;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = { "com.github.d0ctorleon.mythsandlegends.items.KeyItem" })
public class MixinKeyItem {

    @Shadow @Final private String itemName;

    @Inject(method = "appendTooltip", at = @At(value = "HEAD"), cancellable = true)
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        tooltip.add(Text.translatable("item." + this.itemName + ".description"));
        ci.cancel();
    }

}
