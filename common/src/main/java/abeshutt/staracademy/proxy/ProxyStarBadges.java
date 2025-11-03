package abeshutt.staracademy.proxy;

import abeshutt.staracademy.screen.StarBadgeScreenHandler;

import java.util.Optional;

public interface ProxyStarBadges {

    StarBadgeScreenHandler getHandler();

    static Optional<ProxyStarBadges> of(Object object) {
        if(object instanceof ProxyStarBadges handler) {
            return Optional.of(handler);
        }

        return Optional.empty();
    }

}
