package abeshutt.staracademy.mixin.mythsandlegends;

import abeshutt.staracademy.StarAcademyMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = { "com.github.d0ctorleon.mythsandlegends.utils.ForceSpawningUtils" })
public class MixinForceSpawningUtils {

    @Inject(method = "forceSpawnv1", at = @At("HEAD"))
    private static void forceSpawnv1Head(World world, PlayerEntity playerEntity, Hand hand, String keyItemIdentifierPath,
                                         CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        StarAcademyMod.FORCE_SPAWNING.set(true);
    }

    @Inject(method = "forceSpawnv1", at = @At("RETURN"))
    private static void forceSpawnv1Return(World world, PlayerEntity playerEntity, Hand hand, String keyItemIdentifierPath,
                                           CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        StarAcademyMod.FORCE_SPAWNING.set(false);
    }

}
