package abeshutt.staracademy.command;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.config.StarterModeArgumentType;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.data.PokemonStarterData;
import abeshutt.staracademy.world.data.StarterMode;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class StarterCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal(StarAcademyMod.ID)
                .then(literal("starter")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("mode")
                                .then(argument("mode", StringArgumentType.word())
                                        .suggests(StarterModeArgumentType.SUGGESTIONS)
                                        .executes(this::onMode)))
                        .then(literal("set_raffle_interval")
                                .then(argument("interval", TimeArgumentType.time())
                                        .executes(this::onSetRaffleInterval)))));

    }

    private int onMode(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getServer();
        PokemonStarterData data = ModWorldData.POKEMON_STARTER.getGlobal(server);
        String modeStr = StarterModeArgumentType.getStarterMode(context, "mode");

        StarterMode mode = StarterMode.valueOf(modeStr.toUpperCase());

        if(data.setMode(mode)) {
            context.getSource().sendFeedback(() -> Text.empty()
                .append(Text.literal("The starter handler is now ").formatted(Formatting.GRAY))
                .append(Text.literal(modeStr).formatted(Formatting.AQUA))
                .append(Text.literal(".").formatted(Formatting.GRAY)), true);
        } else {
            context.getSource().sendFeedback(() -> Text.empty()
                .append(Text.literal("The starter handler is already ").formatted(Formatting.GRAY))
                .append(Text.literal(modeStr).formatted(Formatting.AQUA))
                .append(Text.literal(".").formatted(Formatting.GRAY)), true);
        }

        return 0;
    }

    private int onSetRaffleInterval(CommandContext<ServerCommandSource> context) {
        long interval = context.getArgument("interval", Integer.class);
        MinecraftServer server = context.getSource().getServer();
        PokemonStarterData data = ModWorldData.POKEMON_STARTER.getGlobal(server);
        data.setTimeInterval(interval);
        context.getSource().sendFeedback(() -> Text.empty()
                .append(Text.literal("The starter raffle interval has been updated to ").formatted(Formatting.GRAY))
                .append(Text.literal(String.valueOf(interval)).formatted(Formatting.AQUA))
                .append(Text.literal(" ticks.").formatted(Formatting.GRAY)), true);
        return 0;
    }

}
