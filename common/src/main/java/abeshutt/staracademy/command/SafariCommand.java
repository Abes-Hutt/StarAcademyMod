package abeshutt.staracademy.command;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.item.SafariTicketItem;
import abeshutt.staracademy.world.data.SafariData;
import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SafariCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal(StarAcademyMod.ID)
                .then(literal("safari")
                    .requires(source -> source.hasPermissionLevel(4))
                    .then(literal("pause")
                        .executes(this::onPause))
                    .then(literal("unpause")
                        .executes(this::onUnpause))
                    .then(literal("restart")
                        .executes(this::onRestart))
                    .then(literal("add_time")
                        .then(argument("players", EntityArgumentType.players())
                            .then(argument("time", TimeArgumentType.time(Integer.MIN_VALUE))
                                .executes(this::onAddTime))))));
    }

    private int onAddTime(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
        long time = context.getArgument("time", Integer.class);

        SafariData data = ModWorldData.SAFARI.getGlobal(context.getSource().getServer());

        for(ServerPlayerEntity player : players) {
            SafariData.Entry entry = data.getOrCreate(player.getUuid());
            entry.setTimeLeft(entry.getTimeLeft() + time);
            context.getSource().sendFeedback(() -> SafariTicketItem.getTimeMessage(player, time, false), true);
            player.sendMessage(SafariTicketItem.getTimeMessage(player, time, true), false);
        }

        return 0;
    }

    private int onRestart(CommandContext<ServerCommandSource> context) {
        SafariData data = ModWorldData.SAFARI.getGlobal(context.getSource().getServer());
        data.onStart(context.getSource().getServer());

        context.getSource().sendFeedback(() -> {
            return Text.empty().append(Text.literal("The Safari has been restarted.").formatted(Formatting.GRAY));
        }, true);
        return 0;
    }

    private int onPause(CommandContext<ServerCommandSource> context) {
        SafariData data = ModWorldData.SAFARI.getGlobal(context.getSource().getServer());

        if(data.setPaused(true)) {
            context.getSource().sendFeedback(() -> {
                return Text.empty().append(Text.literal("The Safari is now ").formatted(Formatting.GRAY))
                        .append(Text.literal("paused").formatted(Formatting.RED)
                        .append(Text.literal(".").formatted(Formatting.GRAY)));
            }, true);
        } else {
            context.getSource().sendFeedback(() -> {
                return Text.empty().append(Text.literal("The Safari is already ").formatted(Formatting.GRAY))
                        .append(Text.literal("paused").formatted(Formatting.RED)
                        .append(Text.literal(".").formatted(Formatting.GRAY)));
            }, true);
        }

        return 0;
    }

    private int onUnpause(CommandContext<ServerCommandSource> context) {
        SafariData data = ModWorldData.SAFARI.getGlobal(context.getSource().getServer());

        if(data.setPaused(false)) {
            context.getSource().sendFeedback(() -> {
                return Text.empty().append(Text.literal("The Safari is now ").formatted(Formatting.GRAY))
                        .append(Text.literal("unpaused").formatted(Formatting.GREEN)
                        .append(Text.literal(".").formatted(Formatting.GRAY)));
            }, true);
        } else {
            context.getSource().sendFeedback(() -> {
                return Text.empty().append(Text.literal("The Safari is already ").formatted(Formatting.GRAY))
                        .append(Text.literal("unpaused").formatted(Formatting.GREEN)
                        .append(Text.literal(".").formatted(Formatting.GRAY)));
            }, true);
        }

        return 0;
    }

}
