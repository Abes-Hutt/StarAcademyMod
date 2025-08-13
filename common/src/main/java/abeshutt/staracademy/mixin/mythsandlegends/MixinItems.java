package abeshutt.staracademy.mixin.mythsandlegends;

import abeshutt.staracademy.config.LegendaryItemsConfig;
import abeshutt.staracademy.init.ModConfigs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = { "com.github.d0ctorleon.mythsandlegends.items.Items" }, remap = false)
public class MixinItems {

    @Shadow @Mutable @Final private static List<String> ITEM_NAMES;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void clinit(CallbackInfo ci) {
        List<String> names = new ArrayList<>(ITEM_NAMES);
        ModConfigs.LEGENDARY_ITEMS = new LegendaryItemsConfig().read();
        names.addAll(ModConfigs.LEGENDARY_ITEMS.getCustom());
        ITEM_NAMES = names;
    }

}
