package abeshutt.staracademy.mixin;

import abeshutt.staracademy.util.ProxySpeciesDexRecord;
import com.cobblemon.mod.common.api.pokedex.FormDexRecord;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = SpeciesDexRecord.class, remap = false)
public class MixinSpeciesDexRecord implements ProxySpeciesDexRecord {

    @Shadow @Final
    private Map<String, FormDexRecord> formRecords;

    @Override
    public Map<String, FormDexRecord> getFormRecords() {
        return this.formRecords;
    }

}
