package abeshutt.staracademy.proxy;

import java.util.Optional;

public interface ProxySelectionScreen {

    int getSelection();

    void setSelection(int selection);

    static Optional<ProxySelectionScreen> of(Object object) {
        if(object instanceof ProxySelectionScreen handler) {
            return Optional.of(handler);
        }

        return Optional.empty();
    }

}
