package abeshutt.staracademy.mixin;

import abeshutt.staracademy.attribute.again.Attribute;
import abeshutt.staracademy.attribute.again.NodeAttribute;
import abeshutt.staracademy.attribute.again.type.AttributeTypes;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.util.AttributeHolder;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntityAttributes implements AttributeHolder {

    private Attribute<?> attribute;

    @Override
    public Attribute<?> getRoot() {
        if(this.attribute == null) {
            this.attribute = ModConfigs.ATTRIBUTE.getRoot().copy();
        }

        return this.attribute;
    }

    @Override
    public void setRoot(Attribute<?> root) {
        this.attribute = root;
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyFrom(Entity original, CallbackInfo ci) {
        AttributeHolder.setRoot(this, AttributeHolder.getRoot(original));
    }

}
