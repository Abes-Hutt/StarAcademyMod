package abeshutt.staracademy.mixin.ultrabeasts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = { "dev.darcosse.chimeras.fabric.entity.WormholeEntity" })
public abstract class MixinWormholeEntity extends Entity {

    @Shadow private int ambientSoundTimer;

    public MixinWormholeEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if(!this.getWorld().isClient && this.ambientSoundTimer == 0) {
            this.ambientSoundTimer = 1;
        }
    }

}
