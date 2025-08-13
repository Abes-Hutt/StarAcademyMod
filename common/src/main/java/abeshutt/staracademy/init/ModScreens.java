package abeshutt.staracademy.init;

import abeshutt.staracademy.screen.CardAlbumScreen;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.menu.MenuRegistry;

public class ModScreens extends ModRegistries {

    public static void register() {
        ClientLifecycleEvent.CLIENT_SETUP.register(minecraft -> {
            MenuRegistry.registerScreenFactory(ModScreenHandlers.CARD_ALBUM.get(), CardAlbumScreen::new);
        });
    }

}
