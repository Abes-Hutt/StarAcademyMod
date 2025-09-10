package abeshutt.staracademy.init;

import abeshutt.staracademy.command.*;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Supplier;

public class ModCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        register(ReloadCommand::new, dispatcher, access, environment);
        register(StarterCommand::new, dispatcher, access, environment);
        register(SafariCommand::new, dispatcher, access, environment);
        register(PartnerCommand::new, dispatcher, access, environment);
        register(WardrobeCommand::new, dispatcher, access, environment);
        register(StatsCommand::new, dispatcher, access, environment);
        register(HouseCommand::new, dispatcher, access, environment);
        register(SummaryCommand::new, dispatcher, access, environment);
        register(NickCommand::new, dispatcher, access, environment);
    }

    private static <T extends Command> T register(Supplier<T> supplier, CommandDispatcher<ServerCommandSource> dispatcher,
                                                  CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        T command = supplier.get();
        command.register(dispatcher, access, environment);
        return command;
    }

}
