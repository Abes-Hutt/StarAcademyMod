package abeshutt.staracademy.command;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.data.NickData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NickCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal(StarAcademyMod.ID)
                .then(literal("nick")
                    .then(literal("set")
                        .then(argument("name", StringArgumentType.greedyString())
                            .executes(this::onNickSet)))
                    .then(literal("clear")
                        .executes(this::onNickClear))));
    }

    private int onNickSet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, "name");
        NickData data = ModWorldData.NICK.getGlobal(context.getSource().getServer());
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        data.set(player.getUuid(), name);
        return 0;
    }

    private int onNickClear(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        NickData data = ModWorldData.NICK.getGlobal(context.getSource().getServer());
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        data.set(player.getUuid(), null);
        return 0;
    }

}
