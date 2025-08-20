package abeshutt.staracademy.mixin.cobblemon;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.gui.startselection.widgets.CategoryList;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CategoryList.Category.class)
public abstract class MixinCategoryListEntry extends AlwaysSelectedEntryListWidget.Entry<CategoryList.Category> {

    @Unique private static final Identifier TEXTURE = MiscUtilsKt.cobblemonResource("textures/gui/starterselection/starterselection_slot.png");

    @SuppressWarnings("deprecation")
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/client/render/RenderHelperKt;drawScaledText$default(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;Lnet/minecraft/text/MutableText;Ljava/lang/Number;Ljava/lang/Number;FLjava/lang/Number;IIZZLjava/lang/Integer;Ljava/lang/Integer;ILjava/lang/Object;)V"))
    private void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        CategoryList.Category selected = this.parentList.getSelectedOrNull();

        if(selected == null && !this.parentList.children().isEmpty()) {
            selected = this.parentList.children().get(0);
        }

        if(selected != (Object)this) {
            return;
        }

        GuiUtilsKt.blitk(context.getMatrices(), TEXTURE, x + 2.5F, y, 16.0F, 51.5F,
                0.0F, 0.0F, 51.5F, 16.0F, 0,
                0.75F, 0.75F, 0.75F, 1.0F, true, 1.0F);
    }

}
