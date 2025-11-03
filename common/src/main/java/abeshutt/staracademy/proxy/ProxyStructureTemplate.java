package abeshutt.staracademy.proxy;

import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public interface ProxyStructureTemplate {

    boolean isCustom();

    void setCustom(boolean custom);

    StructureTemplate.StructureBlockInfo get(BlockPos pos);

    static Optional<ProxyStructureTemplate> of(Object object) {
        if(object instanceof ProxyStructureTemplate proxy) {
            return Optional.of(proxy);
        }

        return Optional.empty();
    }

}
