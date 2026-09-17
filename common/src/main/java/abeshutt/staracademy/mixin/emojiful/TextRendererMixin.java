package abeshutt.staracademy.mixin.emojiful;

import com.cobblemon.mod.common.client.CobblemonClient;
import com.hrznstudio.emojiful.api.Emoji;
import com.hrznstudio.emojiful.render.EmojiFontHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

    @Inject(method = "drawWithOutline", at = @At("HEAD"), cancellable = true)
    private void staracademy$stopBreakingBattleUI(OrderedText reorderingProcessor, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (CobblemonClient.INSTANCE.getBattle() != null && !CobblemonClient.INSTANCE.getBattle().getMinimised()) return;

        if (reorderingProcessor != null) {
            StringBuilder builder = new StringBuilder();
            if (reorderingProcessor != null) {
                reorderingProcessor.accept((p_accept_1_, p_accept_2_, ch) -> {
                    builder.append((char) ch);
                    return true;
                });
            }
            String text = builder.toString();
            if (text.length() > 0) {
                color = (color & -67108864) == 0 ? color | -16777216 : color;
                HashMap<Integer, Emoji> emojis = new LinkedHashMap<>();
                try {
                    Pair<String, HashMap<Integer, Emoji>> cache = EmojiFontHelper.RECENT_STRINGS.get(text);
                    text = cache.getLeft();
                    emojis = cache.getRight();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (!emojis.isEmpty() || text.startsWith(EmojiFontHelper.SCAPED_STRING)) {
                    text = text.replace(EmojiFontHelper.SCAPED_STRING, "");
                    List<OrderedText> processors = new ArrayList<>();
                    HashMap<Integer, Emoji> finalEmojis = emojis;
                    AtomicInteger cleanPos = new AtomicInteger();
                    AtomicBoolean ignore = new AtomicBoolean(false);
                    reorderingProcessor.accept((pos, style, ch) -> {
                        if (!ignore.get()) {
                            if (finalEmojis.get(cleanPos.get()) == null) {
                                processors.add(new EmojiFontHelper.CharacterProcessor(cleanPos.getAndIncrement(), style, ch));
                            } else {
                                processors.add(new EmojiFontHelper.CharacterProcessor(cleanPos.get(), style, ' '));
                                ignore.set(true);
                                return true;
                            }
                        }
                        if (ignore.get() && ch == ':') {
                            ignore.set(false);
                            cleanPos.getAndIncrement();
                        }
                        return true;
                    });
                    Matrix4f matrix4f = new Matrix4f(matrix);

                    /*if (isShadow) {
                        EmojiFontRenderer.EmojiCharacterRenderer fontrenderer$characterrenderer = new EmojiFontRenderer.EmojiCharacterRenderer(emojis, buffer, x, y, color, true, matrix4f, displayMode == Font.DisplayMode.SEE_THROUGH, packedLight);
                        FormattedCharSequence.fromList(processors).accept(fontrenderer$characterrenderer);
                        fontrenderer$characterrenderer.finish(colorBackgroundIn, x);
                        matrix4f.translate(EmojiFontRenderer.SHADOW_OFFSET);
                    }*/
                    EmojiFontHelper.EmojiCharacterRenderer fontrenderer$characterrenderer = new EmojiFontHelper.EmojiCharacterRenderer(emojis, vertexConsumers, x, y, color, false, matrix4f, false, light);
                    OrderedText.innerConcat(processors).accept(fontrenderer$characterrenderer);
                    ci.cancel();
                }
            }
        }
    }

}
