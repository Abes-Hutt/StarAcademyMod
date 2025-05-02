package abeshutt.staracademy.util;

import com.cobblemon.mod.common.api.pokedex.FormDexRecord;

import java.util.Map;
import java.util.Optional;

public interface ProxySpeciesDexRecord {

    Map<String, FormDexRecord> getFormRecords();

    static Optional<ProxySpeciesDexRecord> of(Object object) {
        if(object instanceof ProxySpeciesDexRecord proxy) {
            return Optional.of(proxy);
        }

        return Optional.empty();
    }

}
