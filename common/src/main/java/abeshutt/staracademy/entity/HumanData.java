package abeshutt.staracademy.entity;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public interface HumanData {

    Identifier getSkinTexture();

    Vec3d lerpVelocity(float tickDelta);

}
