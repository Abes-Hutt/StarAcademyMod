package abeshutt.staracademy.proxy;

import java.util.List;
import java.util.Optional;

public interface ProxyCustomPrice {

    List<String> getCommands();

    static Optional<ProxyCustomPrice> of(Object object) {
        if(object instanceof ProxyCustomPrice proxy) {
            return Optional.of(proxy);
        }

        return Optional.empty();
    }

}
