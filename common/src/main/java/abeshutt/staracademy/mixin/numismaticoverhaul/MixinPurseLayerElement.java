package abeshutt.staracademy.mixin.numismaticoverhaul;

import abeshutt.staracademy.StarAcademyMod;
import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.client.gui.CurrencyTooltipComponent;
import com.glisco.numismaticoverhaul.client.gui.PurseLayerElement;
import com.glisco.numismaticoverhaul.currency.Currency;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import com.glisco.numismaticoverhaul.item.CurrencyTooltipData;
import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.inject.ComponentStub;
import io.wispforest.owo.ui.layers.Layer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(targets = { "com.glisco.numismaticoverhaul.client.gui.PurseLayerElement" })
public class MixinPurseLayerElement {

    @Inject(method = "accept(Lio/wispforest/owo/ui/layers/Layer$Instance;)V", at = @At("RETURN"), remap = false)
    private void accept(Layer<?, StackLayout>.Instance instance, CallbackInfo ci,
                        @Local ButtonComponent button) {
        StarAcademyMod.CLIENT_TICKERS.add(() -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if(player == null) return;
            CurrencyComponent comp = ModComponents.CURRENCY.get(player);

            if(button instanceof ComponentStub stub) {
                stub.tooltip(List.of(TooltipComponent
                        .of(Text.translatable("gui.numismatic-overhaul.purse_title").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Currency.GOLD.getNameColor())))
                                .asOrderedText()),
                        new CurrencyTooltipComponent(new CurrencyTooltipData(comp.getValue(), -1L))));
            }
        });
    }

}
