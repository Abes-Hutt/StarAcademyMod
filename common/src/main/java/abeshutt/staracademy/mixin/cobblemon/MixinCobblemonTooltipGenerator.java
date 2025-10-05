package abeshutt.staracademy.mixin.cobblemon;

import abeshutt.staracademy.StarAcademyMod;
import com.cobblemon.mod.common.client.tooltips.CobblemonTooltipGenerator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(CobblemonTooltipGenerator.class)
public abstract class MixinCobblemonTooltipGenerator {

    @Shadow protected abstract String baseLangKeyForItem(ItemStack stack);

    @Inject(method = "generateTooltip", at = @At("HEAD"), cancellable = true)
    private void generateTooltip(ItemStack stack, List<Text> lines, CallbackInfoReturnable<List<Text>> ci) {
        if (stack.getItem().getRegistryEntry().getKey().isPresent()
                && stack.getItem().getRegistryEntry().getKey().get().getValue().getNamespace().equals(StarAcademyMod.ID)) {
            List<Text> resultLines = new ArrayList<>();

            if (stack.get(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP) != null) {
                ci.setReturnValue(null);
            }

            Language language = Language.getInstance();
            String key = this.baseLangKeyForItem(stack);

            if (language.hasTranslation(key)) {
                resultLines.add(Text.translatable(key).formatted(Formatting.GRAY));
            }

            int i = 1;
            String listKey = key + "_" + i;

            while (language.hasTranslation(listKey)) {
                resultLines.add(Text.translatable(listKey).formatted(Formatting.GRAY));
                listKey = key + "_" + (++i);
            }

            ci.setReturnValue(resultLines);
        }
    }

}
