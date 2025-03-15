package abeshutt.staracademy.world.data;

import abeshutt.staracademy.GameStarterHandler;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModNetwork;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.net.UpdateStarterRaffleS2CPacket;
import abeshutt.staracademy.world.StarterEntry;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.advancement.CobblemonCriteria;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.starter.StarterChosenEvent;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.storage.player.PlayerData;
import com.cobblemon.mod.common.config.starter.StarterCategory;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.starter.CobblemonStarterHandler;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import com.cobblemon.mod.common.world.gamerules.CobblemonGameRules;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import kotlin.Unit;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

import static abeshutt.staracademy.data.adapter.basic.EnumAdapter.Mode.NAME;
import static abeshutt.staracademy.world.data.StarterMode.RAFFLE_ENABLED;
import static abeshutt.staracademy.world.data.StarterMode.RAFFLE_PAUSED;
import static com.cobblemon.mod.common.util.ResourceLocationExtensionsKt.asIdentifierDefaultingNamespace;

public class PokemonStarterData extends WorldData {

    public static final PokemonStarterData CLIENT = new PokemonStarterData(0, RAFFLE_PAUSED, 0);

    private final Set<Identifier> starters;
    private final Map<UUID, StarterEntry> entries;
    private long timeInterval;
    private long timeLeft;

    private StarterMode mode;
    private StarterMode lastMode;

    private int selectionCooldown;

    private boolean changed;

    private PokemonStarterData(long timeInterval, StarterMode mode, int selectionCooldown) {
        this.starters = new LinkedHashSet<>();
        this.entries = new HashMap<>();
        this.timeInterval = timeInterval;
        this.timeLeft = this.timeInterval;
        this.mode = mode;
        this.lastMode = null;
        this.selectionCooldown = selectionCooldown;
    }

    public PokemonStarterData() {
        this(ModConfigs.STARTER_RAFFLE.getTimeInterval(), ModConfigs.STARTER_RAFFLE.getMode(),
                ModConfigs.STARTER_RAFFLE.getSelectionCooldown());
    }

    public Set<Identifier> getStarters() {
        return this.starters;
    }

    public Map<UUID, StarterEntry> getEntries() {
        return this.entries;
    }

    public Identifier getPick(UUID uuid) {
        StarterEntry entry = this.entries.get(uuid);
        if(entry == null) return null;
        return entry.getPick();
    }

    public void setPick(UUID uuid, Identifier pick) {
        StarterEntry entry = this.entries.computeIfAbsent(uuid, key -> new StarterEntry());
        entry.setPick(pick);
    }

    public long getTimeInterval() {
        return this.timeInterval;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
        this.timeLeft = this.timeInterval;
        this.setChanged(true);
    }

    public long getTimeLeft() {
        return this.timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
        this.setChanged(true);
    }

    public StarterMode getMode() {
        return this.mode;
    }

    public boolean setMode(StarterMode mode) {
        boolean changed = this.mode != mode;
        this.mode = mode;

        if(changed) {
            this.setChanged(true);
            return true;
        }

        return false;
    }

    public int getSelectionCooldown() {
        return this.selectionCooldown;
    }

    public void setSelectionCooldown(int selectionCooldown) {
        if(this.selectionCooldown != selectionCooldown) {
            this.setChanged(true);
        }

        this.selectionCooldown = selectionCooldown;
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
        this.markDirty();
    }

    public boolean isGranted(Identifier speciesId) {
        for(StarterEntry entry : this.entries.values()) {
            if(speciesId.equals(entry.getGranted())) {
                return true;
            }
        }

        return false;
    }

    public void onTick(MinecraftServer server) {
        if(this.mode != this.lastMode) {
            Cobblemon.INSTANCE.setStarterHandler(switch(this.mode) {
                case DEFAULT -> new CobblemonStarterHandler();
                case RAFFLE_ENABLED, RAFFLE_PAUSED -> new GameStarterHandler();
            });

            this.lastMode = this.mode;
        }

        if(this.mode == RAFFLE_ENABLED) {
            if(this.getTimeLeft() <= 0) {
                this.onRaffle(server);
                this.setTimeLeft(this.getTimeInterval());
            } else {
                this.setTimeLeft(this.getTimeLeft() - 1);
            }
        }

        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            StarterEntry entry = this.getEntries().get(player.getUuid());
            if(entry == null) continue;
            PlayerData playerData = Cobblemon.playerData.get(player);
            entry.setAvailable(!playerData.getStarterSelected() && !playerData.getStarterLocked());
        }

        Map<UUID, StarterEntry> changes = new HashMap<>();

        this.entries.forEach((uuid, entry) -> {
            if(entry.isChanged()) {
                changes.put(uuid, entry);
                entry.setChanged(false);
                this.markDirty();
            }
        });

        this.setSelectionCooldown(ModConfigs.STARTER_RAFFLE.getSelectionCooldown());

        if(!changes.isEmpty() || this.changed) {
            for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                Map<UUID, StarterEntry> message = new HashMap<>();

                changes.forEach((uuid, entry) -> {
                    if(entry.getGranted() != null || player.getUuid().equals(uuid)) {
                        message.put(uuid, entry);
                    }
                });

                ModNetwork.CHANNEL.sendToPlayer(player, new UpdateStarterRaffleS2CPacket(null,
                        message, this.timeInterval, this.timeLeft, this.mode, this.selectionCooldown));
            }
        }
    }

    private void onJoin(ServerPlayerEntity player) {
        Set<Identifier> starters = Cobblemon.INSTANCE.getStarterHandler().getStarterList(player).stream()
                .flatMap(category -> category.getPokemon().stream())
                .map(PokemonProperties::getSpecies)
                .filter(Objects::nonNull)
                .map(s -> asIdentifierDefaultingNamespace(s, Cobblemon.MODID))
                .collect(Collectors.toSet());

        this.entries.putIfAbsent(player.getUuid(), new StarterEntry());
        Map<UUID, StarterEntry> message = new HashMap<>();

        this.entries.forEach((uuid, entry) -> {
            if(entry.getGranted() != null || player.getUuid().equals(uuid)) {
                message.put(uuid, entry);
            }
        });

        ModNetwork.CHANNEL.sendToPlayer(player, new UpdateStarterRaffleS2CPacket(starters, message,
                this.timeInterval, this.timeLeft, this.mode, this.selectionCooldown));
    }

    private void onRaffle(MinecraftServer server) {
        Map<Identifier, Set<UUID>> picks = new HashMap<>();

        this.entries.forEach((uuid, entry) -> {
            if(entry.getPick() == null) return;
            picks.computeIfAbsent(entry.getPick(), k -> new HashSet<>()).add(uuid);
        });

        picks.entrySet().removeIf(entry -> {
            if(entry.getValue().size() != 1) return false;
            UUID uuid = entry.getValue().iterator().next();
            StarterEntry data = this.getEntries().get(uuid);
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if(player == null) return false;
            Species species = PokemonSpecies.INSTANCE.getByIdentifier(entry.getKey());
            if(species == null) return false;

            if(this.giveStarter(player, entry.getKey())) {
                data.setGranted(entry.getKey());

                for(ServerPlayerEntity other : server.getPlayerManager().getPlayerList()) {
                    other.sendMessage(Text.empty()
                            .append(player.getName())
                            .append(Text.literal(" has claimed ").formatted(Formatting.GRAY))
                            .append(Text.literal(species.getName()).setStyle(Style.EMPTY.withColor(species.getPrimaryType().getHue())))
                            .append(Text.literal(" as their starter!").formatted(Formatting.GRAY)));
                }
            }

            return true;
        });

        for(StarterEntry entry : this.entries.values()) {
            entry.onCompleteRound(this.selectionCooldown);
        }
    }

    public boolean giveStarter(ServerPlayerEntity player, Identifier speciesId) {
        PlayerData playerData = Cobblemon.playerData.get(player);

        if(playerData.getStarterSelected()) {
            player.sendMessage(LocalizationUtilsKt.lang("ui.starter.alreadyselected")
                    .formatted(Formatting.RED), true);
            return false;
        } else if(playerData.getStarterLocked()) {
            player.sendMessage(LocalizationUtilsKt.lang("ui.starter.cannotchoose")
                    .formatted(Formatting.RED), true);
            return false;
        }

        PokemonProperties properties = null;

        loop:
        for(StarterCategory category : Cobblemon.starterConfig.getStarters()) {
            for(PokemonProperties pokemon : category.getPokemon()) {
                if(pokemon.getSpecies() == null) continue;
                Identifier other = asIdentifierDefaultingNamespace(
                        pokemon.getSpecies(), Cobblemon.MODID);

                if(speciesId.equals(other)) {
                   properties = pokemon;
                   break loop;
                }
            }
        }

        if(properties == null) return false;
        Pokemon pokemon = properties.create();

        CobblemonEvents.STARTER_CHOSEN.postThen(new StarterChosenEvent(player, properties, pokemon), event -> {
            return Unit.INSTANCE;
        }, event -> {
            playerData.setStarterSelected(true);
            playerData.setStarterUUID(pokemon.getUuid());

            if(player.getWorld().getGameRules().getBoolean(CobblemonGameRules.SHINY_STARTERS)) {
                pokemon.setShiny(true);
            }

            Cobblemon.INSTANCE.getStorage().getParty(player).add(pokemon);
            CobblemonCriteria.INSTANCE.getPICK_STARTER().trigger(player, pokemon);
            Cobblemon.playerData.saveSingle(playerData);
            playerData.sendToPlayer(player);
            return Unit.INSTANCE;
        });

        return true;
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            NbtCompound entries = new NbtCompound();

            for(Map.Entry<UUID, StarterEntry> entry : this.entries.entrySet()) {
                entry.getValue().writeNbt().ifPresent(tag -> {
                    entries.put(entry.getKey().toString(), tag);
                });
            }

            nbt.put("entries", entries);
            Adapters.LONG.writeNbt(this.timeInterval).ifPresent(tag -> nbt.put("timeInterval", tag));
            Adapters.LONG.writeNbt(this.timeLeft).ifPresent(tag -> nbt.put("timeLeft", tag));
            Adapters.ofEnum(StarterMode.class, NAME).writeNbt(this.mode).ifPresent(tag -> nbt.put("mode", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtCompound entries = nbt.getCompound("entries");
        this.entries.clear();

        for(String key : entries.getKeys()) {
            StarterEntry entry = new StarterEntry();
            entry.readNbt(entries.getCompound(key));
            this.entries.put(UUID.fromString(key), entry);
        }

        this.timeInterval = Adapters.LONG.readNbt(nbt.get("timeInterval")).orElseGet(ModConfigs.STARTER_RAFFLE::getTimeInterval);
        this.timeLeft = Math.min(Adapters.LONG.readNbt(nbt.get("timeLeft")).orElse(this.timeInterval), this.timeInterval);

        if(nbt.contains("mode")) {
            this.mode = Adapters.ofEnum(StarterMode.class, NAME).readNbt(nbt.get("mode")).orElseGet(ModConfigs.STARTER_RAFFLE::getMode);
        } else {
            this.mode = Adapters.BOOLEAN.readNbt(nbt.get("paused")).orElse(false) ? RAFFLE_PAUSED : RAFFLE_ENABLED;
        }
    }

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            PokemonStarterData data = ModWorldData.POKEMON_STARTER.getGlobal(player.getWorld());
            data.onJoin(player);
        });

        TickEvent.SERVER_POST.register(server -> {
            PokemonStarterData data = ModWorldData.POKEMON_STARTER.getGlobal(server);
            data.onTick(server);
        });
    }

}
