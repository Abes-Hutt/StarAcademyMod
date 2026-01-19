package abeshutt.staracademy.mixin.cobblemon;

import abeshutt.staracademy.StarAcademyMod;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonEntity.class)
public abstract class MixinPokemonEntity extends LivingEntity {

    @Shadow
    public abstract void remove(@NotNull Entity.RemovalReason reason);

    protected MixinPokemonEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> ci) {
        if (this.getWorld().getRegistryKey() == StarAcademyMod.SAFARI) {
            ci.setReturnValue(true);
        }
    }

    @Redirect(method = "onStoppedTrackingBy", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;remove(Lnet/minecraft/entity/Entity$RemovalReason;)V"))
    private void remove(PokemonEntity entity, RemovalReason reason) {
        MinecraftServer server = entity.getServer();

        if (server != null) {
            server.send(new ServerTask(server.getTicks() + 1, () -> {
                this.remove(RemovalReason.DISCARDED);
            }));
        }
    }

}
