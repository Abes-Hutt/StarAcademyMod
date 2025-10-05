package abeshutt.staracademy.mixin.outbreaks;

import com.scouter.cobblemonoutbreaks.data.OutbreakWaveData;
import com.scouter.cobblemonoutbreaks.portal.OutbreakPortal;
import com.scouter.cobblemonoutbreaks.portal.entity.OutbreakPortalEntityTickData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = { "com.scouter.cobblemonoutbreaks.portal.entity.OutbreakPortalEntity" })
public abstract class MixinOutbreakPortalEntity {

    @Shadow public abstract OutbreakPortalEntityTickData getTickData();

    @Shadow public abstract OutbreakPortal getPortal();

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/scouter/cobblemonoutbreaks/data/OutbreakWaveData;getWaves()I"), remap = false)
    public int getWaves(OutbreakWaveData instance) {
        if (this.getTickData().getTicksActive() >= this.getPortal().getGateTimer()) {
            return Integer.MAX_VALUE;
        }

        return instance.getWaves();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/scouter/cobblemonoutbreaks/portal/entity/OutbreakPortalEntityTickData;getTickCount()I"), remap = false)
    public int getTickCount(OutbreakPortalEntityTickData instance) {
        if (this.getTickData().getTicksActive() >= this.getPortal().getGateTimer()) {
            return 1;
        }

        return instance.getTickCount();
    }

}
