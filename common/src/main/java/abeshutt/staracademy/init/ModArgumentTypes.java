package abeshutt.staracademy.init;

import abeshutt.staracademy.config.StarterModeArgumentType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registry;

public class ModArgumentTypes extends ModRegistries {

    public static void register(Registry<ArgumentSerializer<?, ?>> registry) {
        ArgumentTypes.register(registry, "starter_mode", StarterModeArgumentType.class, ConstantArgumentSerializer.of(StarterModeArgumentType::starterMode));
    }

}
