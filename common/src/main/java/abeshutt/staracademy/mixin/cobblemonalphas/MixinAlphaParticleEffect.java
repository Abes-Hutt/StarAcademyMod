package abeshutt.staracademy.mixin.cobblemonalphas;

import dev.cudzer.cobblemonalphas.particles.AlphaParticleEffect;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.UUID;

@Mixin(targets = { "dev.cudzer.cobblemonalphas.particles.AlphaParticleEffect" })
public class MixinAlphaParticleEffect {

    @Shadow @Final private static Map<UUID, Long> alphaAmbientTimer;

    @Redirect(method = "spawnParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), remap = false)
    private static void spawnParticles(World instance, Entity entity, BlockPos source, SoundEvent pos,
                                       SoundCategory sound, float category, float volume) {
        if(!alphaAmbientTimer.containsKey(entity.getUuid())) {
            instance.playSound(entity, source, pos, sound, category, volume);
        }
    }

}
