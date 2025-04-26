package abeshutt.staracademy.util;

import java.util.Optional;

public interface ProxyGameProfile {

    void setName(String name);

    static Optional<ProxyGameProfile> of(Object object) {
        if(object instanceof ProxyGameProfile proxy) {
            return Optional.of(proxy);
        }

        return Optional.empty();
    }

}
