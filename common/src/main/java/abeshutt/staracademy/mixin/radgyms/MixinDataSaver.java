package abeshutt.staracademy.mixin.radgyms;

import abeshutt.staracademy.proxy.ProxyGymData;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Debug(export = true)
@Mixin(value = Entity.class, priority = 1500)
public abstract class MixinDataSaver implements ProxyGymData {

    @Unique private NbtCompound academy$gymData;

    @Shadow public abstract UUID getUuid();

    @Shadow public abstract String getUuidAsString();

    @TargetHandler(mixin = "lol.gito.radgyms.mixin.DataSaver", name = "getGymsPersistentData")
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    public void getGymsPersistentData(CallbackInfoReturnable<NbtCompound> ci) {
        if(this.academy$gymData == null) {
            this.academy$gymData = new NbtCompound();
            RadGyms.INSTANCE.debug("Academy PersistentData created for player " + this.getUuid());
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

    @TargetHandler(mixin = "lol.gito.radgyms.mixin.DataSaver", name = "RadGyms$injectWriteMethod")
    @Inject(method = "@MixinSquared:Handler", at = @At("RETURN"))
    protected void RadGyms$injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir, CallbackInfo ci) {
        if (this.academy$gymData != null) {
            nbt.put("rad-gyms.entity_data", this.academy$gymData);
            RadGyms.INSTANCE.debug("Academy PersistentData wrote for player " + this.getUuid());
        }
    }

    @TargetHandler(mixin = "lol.gito.radgyms.mixin.DataSaver", name = "RadGyms$injectReadMethod")
    @Inject(method = "@MixinSquared:Handler", at = @At("RETURN"))
    protected void RadGyms$injectReadMethod(NbtCompound nbt, CallbackInfo info, CallbackInfo ci) {
        if (nbt.contains("rad-gyms.entity_data", 10)) {
            this.academy$gymData = nbt.getCompound("rad-gyms.entity_data");
            RadGyms.INSTANCE.debug("Academy PersistentData read for player " + this.getUuidAsString());
        }
    }

}
