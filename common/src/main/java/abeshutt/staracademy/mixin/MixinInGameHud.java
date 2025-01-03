package abeshutt.staracademy.mixin;

import abeshutt.staracademy.screen.SafariWidget;
import abeshutt.staracademy.screen.StarterSelectionWidget;
import abeshutt.staracademy.util.ClientScheduler;
import abeshutt.staracademy.world.StarterEntry;
import abeshutt.staracademy.world.data.PokemonStarterData;
import abeshutt.staracademy.world.data.SafariData;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        this.renderRaffle(context, tickDelta);
        this.renderSafari(context, tickDelta);
    }

    private void renderRaffle(DrawContext context, float tickDelta) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;
        StarterEntry entry = PokemonStarterData.CLIENT.getEntries().get(player.getUuid());
        if(entry == null || entry.getGranted() != null) return;

        Identifier pick = PokemonStarterData.CLIENT.getPick(player.getUuid());
        Species species = pick == null ? null : PokemonSpecies.INSTANCE.getByIdentifier(pick);
        if(PokemonStarterData.CLIENT.isPaused() && pick == null) return;

        StarterSelectionWidget widget = new StarterSelectionWidget(species, PokemonStarterData.CLIENT.getTimeLeft(),
                PokemonStarterData.CLIENT.isPaused());
        widget.render(context, 0, 0, ClientScheduler.getTick(tickDelta));
    }

    private void renderSafari(DrawContext context, float tickDelta) {
        if(SafariData.CLIENT.getTimeLeft() <= 0) {
            return;
        }

        SafariWidget widget = new SafariWidget();
        widget.render(context, 0, 0, ClientScheduler.getTick(tickDelta));
    }

}
