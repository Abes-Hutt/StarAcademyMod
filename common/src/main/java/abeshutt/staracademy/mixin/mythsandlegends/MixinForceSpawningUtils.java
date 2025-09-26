package abeshutt.staracademy.mixin.mythsandlegends;

import abeshutt.staracademy.StarAcademyMod;
import com.cobblemon.mod.common.api.spawning.detail.EntitySpawnResult;
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = { "com.github.d0ctorleon.mythsandlegends.utils.ForceSpawningUtils" })
public abstract class MixinForceSpawningUtils {

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

    @Inject(method = "handleSpawnAction2", at = @At("HEAD"), remap = false)
    private static void handleSpawnAction2(SpawnAction<?> spawnAction, CallbackInfo ci) {
        spawnAction.getFuture().thenAccept(object -> {
            if(object instanceof EntitySpawnResult result) {
                for(Entity entity : result.getEntities()) {
                    if(entity instanceof MobEntity mob) {
                        mob.setPersistent();
                    }
                }
            }
        });
    }

    @Redirect(method = "isSpawnDetailForKeyItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;getPath()Ljava/lang/String;"))
    private static String isSpawnDetailForKeyItem(Identifier instance) {
        return instance.toString();
    }

}
