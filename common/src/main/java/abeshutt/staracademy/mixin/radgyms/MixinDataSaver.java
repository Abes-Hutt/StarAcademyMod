package abeshutt.staracademy.mixin.radgyms;

import abeshutt.staracademy.util.ProxyGymData;
import com.bawnorton.mixinsquared.TargetHandler;
import lol.gito.radgyms.RadGyms;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Debug(export = true)
@Mixin(value = Entity.class, priority = 1500)
public abstract class MixinDataSaver implements ProxyGymData {

    @Unique private NbtCompound academy$gymData;

    @Shadow public abstract UUID getUuid();

    @TargetHandler(mixin = "lol.gito.radgyms.mixin.DataSaver", name = "getGymsPersistentData", print = true)
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true, require = 0)
    public void getGymsPersistentData(CallbackInfoReturnable<NbtCompound> ci) {
        if(this.academy$gymData == null) {
            this.academy$gymData = new NbtCompound();
            RadGyms.INSTANCE.debug("PersistentData created for player " + this.getUuid());
        }

        ci.setReturnValue(this.academy$gymData);
    }

    @Override
    public NbtCompound getGymData() {
        return this.academy$gymData;
    }

    @Override
    public void setGymData(NbtCompound gymData) {
        this.academy$gymData = gymData;
    }

}
