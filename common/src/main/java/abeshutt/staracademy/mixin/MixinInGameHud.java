package abeshutt.staracademy.mixin;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.screen.SafariWidget;
import abeshutt.staracademy.screen.StarterSelectionWidget;
import abeshutt.staracademy.util.ClientScheduler;
import abeshutt.staracademy.world.StarterEntry;
import abeshutt.staracademy.world.data.PokemonStarterData;
import abeshutt.staracademy.world.data.SafariData;
import abeshutt.staracademy.world.data.StarterId;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abeshutt.staracademy.world.data.StarterMode.DEFAULT;
import static abeshutt.staracademy.world.data.StarterMode.RAFFLE_ENABLED;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        this.academy$renderRaffle(context, tickCounter.getTickDelta(true));
        this.academy$renderSafari(context, tickCounter.getTickDelta(true));
    }

    @Unique
    private void academy$renderRaffle(DrawContext context, float tickDelta) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;
        StarterEntry entry = PokemonStarterData.CLIENT.getEntries().get(player.getUuid());
        if(entry == null || entry.getGranted() != null || !entry.isAvailable()) return;

        StarterId pick = PokemonStarterData.CLIENT.getPick(player.getUuid());
        if(PokemonStarterData.CLIENT.getMode() != RAFFLE_ENABLED && pick == null) return;
        if(PokemonStarterData.CLIENT.getMode() == DEFAULT) return;

        StarterSelectionWidget widget = new StarterSelectionWidget(pick, PokemonStarterData.CLIENT.getTimeLeft(),
                PokemonStarterData.CLIENT.getMode() != RAFFLE_ENABLED);
        widget.render(context, 0, 0, ClientScheduler.getTick(tickDelta));
    }

    @Unique
    private void academy$renderSafari(DrawContext context, float tickDelta) {
        if(SafariData.CLIENT.getTimeLeft() <= 0 || SafariData.CLIENT.isPaused()) {
            return;
        }

         ClientWorld world = MinecraftClient.getInstance().world;

        if((world == null || world.getRegistryKey() != StarAcademyMod.SAFARI) && !MinecraftClient.getInstance().options.playerListKey.isPressed()) {
            return;
        }

        SafariWidget widget = new SafariWidget();
        widget.render(context, 0, 0, ClientScheduler.getTick(tickDelta));
    }

}
