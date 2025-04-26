package abeshutt.staracademy.mixin;

import abeshutt.staracademy.util.ProxyGameProfile;
import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GameProfile.class, remap = false)
public class MixinGameProfile implements ProxyGameProfile {

    @Shadow @Final @Mutable private String name;

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
