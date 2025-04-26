package abeshutt.staracademy.mixin.cobblemon;

import abeshutt.staracademy.util.ProxySelectionButton;
import com.cobblemon.mod.common.client.gui.startselection.widgets.preview.SelectionButton;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SelectionButton.class)
public class MixinSelectionButton implements ProxySelectionButton {

    @Unique private String text;

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/util/LocalizationUtilsKt;lang(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;"))
    private MutableText renderButton(String subKey, Object[] objects) {
        return LocalizationUtilsKt.lang(this.text == null ? subKey : this.text, objects);
    }

}
