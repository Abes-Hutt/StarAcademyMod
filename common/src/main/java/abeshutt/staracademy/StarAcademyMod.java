package abeshutt.staracademy;

import abeshutt.staracademy.attribute.Attributes;
import abeshutt.staracademy.compat.enhancedcelestials.EnhancedCelestialsCompat;
import abeshutt.staracademy.event.CommonEvents;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModRegistries;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public final class StarAcademyMod {

    public static final ThreadLocal<Boolean> FORCE_SPAWNING = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<Long> QUEST_ID = ThreadLocal.withInitial(() -> 0L);
    public static RegistryWrapper.WrapperLookup REGISTRIES;

    public static final String ID = "academy";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final RegistryKey<World> SAFARI = RegistryKey.of(RegistryKeys.WORLD, StarAcademyMod.id("safari"));
    public static List<Runnable> CLIENT_TICKERS = new ArrayList<>();

    public static void init() {
        LifecycleEvent.SERVER_STARTED.register(instance -> {
            REGISTRIES = instance.getRegistryManager();
        });

        if(Platform.isModLoaded("enhancedcelestials")) {
            EnhancedCelestialsCompat.init();
        }

        ModRegistries.register();
        Attributes.init();

        CommonEvents.POKEMON_SENT_PRE.register(event -> {
            if(event.getLevel().getRegistryKey() == SAFARI) {
                event.cancel();
            }
        });

        CommonEvents.BATTLE_STARTED_PRE.register(event -> {
            if(event.getBattle().getPlayers().stream().anyMatch(player -> player.getWorld().getRegistryKey() == SAFARI)) {
                event.cancel();
            }
        });

        CommonEvents.POKEMON_CATCH_RATE.register(event -> {
            if(event.getThrower().getWorld().getRegistryKey() == SAFARI) {
                if(event.getPokeBallEntity().getPokeBall().item() != CobblemonItems.SAFARI_BALL) {
                    event.setCatchRate(0.0F);
                }
            }
        }, Priority.LOWEST);

        CommonEvents.POKEMON_ENTITY_SPAWN.register(event -> {
            if(FORCE_SPAWNING.get()) {
                return;
            }

            World world = event.getEntity().getEntityWorld();
            WorldBorder border = world.getWorldBorder();
            double dx = event.getEntity().getPos().getX() - border.getCenterX();
            double dz = event.getEntity().getPos().getZ() - border.getCenterZ();
            double distance = Math.sqrt(dx * dx + dz * dz);

            if(distance <= ModConfigs.POKEMON_SPAWN.getSpawnProtectionDistance()) {
                event.cancel();
                return;
            }

            /*
            ModConfigs.POKEMON_SPAWN.getLevel(distance).ifPresent(roll -> {
                event.getEntity().getPokemon().setLevel(roll.get(JavaRandom.ofNanoTime()));
            });*/
        }, Priority.HIGHEST);

        CommonEvents.POKEMON_ENTITY_SPAWN.register(event -> {
            MinecraftServer server = event.getEntity().getWorld().getServer();
            if(server == null) return;
            Pokemon pokemon = event.getEntity().getPokemon();

            /*
            if(FORCE_SPAWNING.get()) {
                List<String> prefixes = new ArrayList<>();
                if(pokemon.getShiny()) prefixes.add("Shiny");
                if(pokemon.isLegendary()) prefixes.add("Legendary");

                MutableText message = Text.empty()
                        .append(Text.literal("A ").formatted(Formatting.BOLD))
                        .append(Text.literal(String.join(" ", prefixes)).formatted(Formatting.BOLD))
                        .append(prefixes.isEmpty() ? Text.empty() : Text.literal(" "))
                        .append(event.getEntity().getDisplayName().copy().formatted(Formatting.BOLD))
                        .append(Text.literal(" has been summoned!").formatted(Formatting.BOLD));

                for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    player.sendMessage(message);
                }
            } else {
                List<String> prefixes = new ArrayList<>();
                if(pokemon.getShiny()) prefixes.add("Shiny");
                if(pokemon.isLegendary()) prefixes.add("Legendary");

                if(pokemon.getShiny() || pokemon.isLegendary()) {
                    MutableText message = Text.empty()
                            .append(Text.literal("A ").formatted(Formatting.BOLD))
                            .append(Text.literal(String.join(" ", prefixes)).formatted(Formatting.BOLD))
                            .append(prefixes.isEmpty() ? Text.empty() : Text.literal(" "))
                            .append(event.getEntity().getDisplayName().copy().formatted(Formatting.BOLD))
                            .append(Text.literal(" has spawned near someone!").formatted(Formatting.BOLD));

                    for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        player.sendMessage(message);
                    }
                }
            }*/
        }, Priority.LOWEST);
    }

    public static Identifier id(String path) {
        return Identifier.of(ID, path);
    }

    @Environment(EnvType.CLIENT)
    public static ModelIdentifier mid(Identifier id, String variant) {
        return new ModelIdentifier(id, variant);
    }

    @Environment(EnvType.CLIENT)
    public static ModelIdentifier mid(String name, String variant) {
        return StarAcademyMod.mid(StarAcademyMod.id(name), variant);
    }

    public static Text translatableText(String key, Object... args) {
        return Text.translatable(ID + "." + key, args);
    }

}
