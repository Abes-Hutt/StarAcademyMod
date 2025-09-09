package abeshutt.staracademy.mixin;

import abeshutt.staracademy.event.CommonEvents;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.data.AcademyHouse;
import abeshutt.staracademy.world.data.HouseData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerEntity.class, priority = 1000)
public abstract class MixinPlayerEntity extends LivingEntity {

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void postTick(CallbackInfo ci) {
        CommonEvents.PLAYER_TICK.invoker().tick((PlayerEntity)(Object)this);
    }

    @Inject(method = "getDisplayName", at = @At(value = "RETURN"), cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<Text> ci) {
        MutableText result = ci.getReturnValue().copy();
        HouseData data = this.getWorld().isClient() ? HouseData.CLIENT : ModWorldData.HOUSE.getGlobal(this.getWorld());
        AcademyHouse house = data.getFor(this.getUuid()).orElse(null);

        if(house != null) {
            ci.setReturnValue(result.setStyle(result.getStyle().withColor(house.getColor())));
        }
    }

}
