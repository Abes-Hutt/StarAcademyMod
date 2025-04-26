package abeshutt.staracademy.command;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.data.PlayerProfileData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.util.*;

import static net.minecraft.command.argument.RegistryEntryReferenceArgumentType.registryEntry;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.Formatting.GRAY;

public class StatsCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        LiteralArgumentBuilder<ServerCommandSource> node = literal("print");

        for(StatType<?> type : Registries.STAT_TYPE) {
            Identifier id = Registries.STAT_TYPE.getId(type);
            if(id == null) continue;
            node.then(literal(id.toString())
                    .then(argument("stat", registryEntry(access, type.getRegistry().getKey()))
                        .then(literal("all")
                            .executes(context -> this.onPrintStats(type, type.getRegistry().getKey(), false, context)))
                        .then(argument("target", EntityArgumentType.players())
                            .executes(context -> this.onPrintStats(type, type.getRegistry().getKey(), true, context)))));
        }

        dispatcher.register(literal(StarAcademyMod.ID)
                .then(literal("stats").then(node)));
    }

    private int onPrintStats(StatType type, RegistryKey key, boolean hasTarget, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        RegistryEntry.Reference entry = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "stat", key);
        PlayerProfileData data = ModWorldData.PLAYER_PROFILE.getGlobal(context.getSource().getServer());

        Set<UUID> players = new HashSet<>();

        if(hasTarget) {
            EntityArgumentType.getPlayers(context, "target").stream().map(Entity::getUuid).forEach(players::add);
        } else {
            players.addAll(data.getProfiles().keySet());
        }

        List<Pair<UUID, Integer>> values = new ArrayList<>();

        for(UUID uuid : players) {
            ServerStatHandler stats = getStats(context.getSource().getServer(), uuid);
            int value = stats.getStat(type, entry.value());
            if(value == 0) continue;
            values.add(new Pair<>(uuid, value));
        }

        values.sort((o1, o2) -> Integer.compare(o2.getRight(), o1.getRight()));

        context.getSource().sendFeedback(() -> {
            Stat stat = type.getOrCreateStat(entry.value());
            String translationKey = "stat." + stat.getValue().toString().replace(':', '.');

            return Text.empty().append(type.getName())
                    .append(Text.literal(" / ").formatted(GRAY))
                    .append(Text.translatable(translationKey));
        }, false);

        for(Pair<UUID, Integer> value : values) {
            Stat stat = type.getOrCreateStat(entry.value());

            context.getSource().sendFeedback(() -> {
                return Text.empty().append(Text.literal(" > ").formatted(GRAY))
                        .append(Text.literal(data.getProfile(value.getLeft()).flatMap(profile -> {
                            return Optional.ofNullable(profile.getName()).map(String::toString);
                        }).orElseGet(() -> value.getLeft().toString())))
                        .append(Text.literal(" | ").formatted(GRAY))
                        .append(stat.format(value.getRight()));
            }, false);
        }

        return 0;
    }

    public static ServerStatHandler getStats(MinecraftServer server, UUID uuid) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
        if(player != null) return player.getStatHandler();
        File root = server.getSavePath(WorldSavePath.STATS).toFile();
        return new ServerStatHandler(server, new File(root, uuid + ".json"));
    }

}
