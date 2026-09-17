package abeshutt.staracademy.mixin.cobblemon;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.gui.startselection.StarterSelectionScreen;
import com.cobblemon.mod.common.client.gui.startselection.widgets.CategoryList;
import com.cobblemon.mod.common.config.starter.RenderableStarterCategory;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = CategoryList.Category.class)
public abstract class MixinCategoryListEntry {

    @Unique private static final Identifier TEXTURE = MiscUtilsKt.cobblemonResource("textures/gui/starterselection/selection_container_header.png");

    @Shadow @Final private RenderableStarterCategory category;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/client/render/RenderHelperKt;drawScaledText$default(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;Lnet/minecraft/text/MutableText;Ljava/lang/Number;Ljava/lang/Number;FLjava/lang/Number;IIZZLjava/lang/Integer;Ljava/lang/Integer;ILjava/lang/Object;)V"))
    private void render(DrawContext context, int index, int x, int y, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if(!(MinecraftClient.getInstance().currentScreen instanceof StarterSelectionScreen screen)) {
            return;
        }

        if(!Objects.equals(screen.getCurrentCategory(), this.category)) {
            return;
        }

        GuiUtilsKt.blitk(context.getMatrices(), TEXTURE, x + CategoryList.MARGIN, y,
                CategoryList.HEADER_HEIGHT, CategoryList.CONTAINER_WIDTH,
                0.0F, 0.0F, CategoryList.CONTAINER_WIDTH, CategoryList.HEADER_HEIGHT, 0,
                0.75F, 0.75F, 0.75F, 1.0F, true, 1.0F);
    }

}
