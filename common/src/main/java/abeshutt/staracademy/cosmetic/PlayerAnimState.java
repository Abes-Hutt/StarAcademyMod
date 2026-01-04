package abeshutt.staracademy.cosmetic;

import com.cobblemon.mod.common.api.riding.util.Vec3Spring;

import java.util.Optional;

public interface PlayerAnimState {

    Vec3Spring getLocalVelocity();

    Vec3Spring getWorldVelocity();

    Vec3Spring getInput();

    void update();

    static Optional<PlayerAnimState> of(Object object) {
        if(object instanceof PlayerAnimState proxy) {
            return Optional.of(proxy);
        }

        return Optional.empty();
    }


}
