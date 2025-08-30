package abeshutt.staracademy.init;

import de.keksuccino.fancymenu.util.input.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.option.KeyBinding;

public class ModKeyBindings extends ModRegistries {

    public static KeyBinding BROADCAST_ITEM;

    public static void register() {
        BROADCAST_ITEM = register(new KeyBinding(
                "key.academy.broadcast_item",
                InputConstants.Type.KEYSYM,
                InputConstants.GLFW_KEY_LEFT_SHIFT | InputConstants.GLFW_KEY_T,
                "category.academy"
        ));
    }

    private static <T extends KeyBinding> T register(T key) {
        KeyMappingRegistry.register(key);
        return key;
    }

}
