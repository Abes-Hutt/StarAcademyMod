package abeshutt.staracademy;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import dev.architectury.platform.Platform;

import java.util.List;

public class AcademyMixinCanceller implements MixinCanceller {

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (Platform.isModLoaded("moonrise")) {
            if ("ca.spottedleaf.moonrise.mixin.chunk_system.ChunkHolderMixin".equals(mixinClassName)) {
                return true;
            } else if ("ca.spottedleaf.moonrise.mixin.collisions.ExplosionMixin".equals(mixinClassName)) {
                return true;
            }
        }

        return false;
    }

}
