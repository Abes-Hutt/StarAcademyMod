package abeshutt.staracademy.init;

import abeshutt.staracademy.screen.handler.CardAlbumScreenHandler;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers extends ModRegistries {

    public static RegistrySupplier<ScreenHandlerType<CardAlbumScreenHandler>> CARD_ALBUM;

    public static void register() {
        CARD_ALBUM = ModScreenHandlers.register(SCREEN_HANDLERS, "card_album", () -> {
            return MenuRegistry.ofExtended(CardAlbumScreenHandler::new);
        });
    }

}
