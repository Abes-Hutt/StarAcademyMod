package abeshutt.staracademy.mixin.mythsandlegends;

import abeshutt.staracademy.config.LegendaryItemsConfig;
import abeshutt.staracademy.init.ModConfigs;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = { "com.github.d0ctorleon.mythsandlegends.items.Items" })
public class MixinItems {

    @Shadow @Mutable @Final private static List<String> ITEM_NAMES;

    @Redirect(method = "registerItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;of(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/util/Identifier;"))
    private static Identifier id(String namespace, String path) {
        return Identifier.of(path);
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void clinit(CallbackInfo ci) {
        List<String> names = new ArrayList<>();

        for(String name : ITEM_NAMES) {
            names.add("myths_and_legends:" + name);
        }

        ModConfigs.LEGENDARY_ITEMS = new LegendaryItemsConfig().read();
        names.addAll(ModConfigs.LEGENDARY_ITEMS.getCustom());
        ITEM_NAMES = names;
    }

}
