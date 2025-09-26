package abeshutt.staracademy.mixin.mythsandlegends;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = { "com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.keyitem.KeyItemConditions$KeyItem" })
public class MixinKeyItemConditions {

    @Shadow public Identifier key_item;

    @Redirect(method = "fits", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;of(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/util/Identifier;"))
    private Identifier id(String namespace, String path) {
        return this.key_item;
    }

}
