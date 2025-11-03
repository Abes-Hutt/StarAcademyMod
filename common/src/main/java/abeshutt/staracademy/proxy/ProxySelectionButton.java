package abeshutt.staracademy.proxy;

import java.util.Optional;

public interface ProxySelectionButton {

    String getText();

    void setText(String text);

    static Optional<ProxySelectionButton> of(Object object) {
        if(object instanceof ProxySelectionButton handler) {
            return Optional.of(handler);
        }

        return Optional.empty();
    }

}
