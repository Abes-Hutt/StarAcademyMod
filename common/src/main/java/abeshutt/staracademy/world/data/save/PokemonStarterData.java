package abeshutt.staracademy.world.data.save;

import abeshutt.staracademy.compat.cobblemon.GameStarterHandler;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.net.UpdateStarterRaffleS2CPacket;
import abeshutt.staracademy.world.data.StarterEntry;
import abeshutt.staracademy.world.data.StarterId;
import abeshutt.staracademy.world.data.StarterMode;
import abeshutt.staracademy.world.data.StarterPokemon;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.advancement.CobblemonCriteria;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.starter.StarterChosenEvent;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.storage.player.GeneralPlayerData;
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreTypes;
import com.cobblemon.mod.common.config.starter.StarterCategory;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.starter.CobblemonStarterHandler;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import com.cobblemon.mod.common.world.gamerules.CobblemonGameRules;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import kotlin.Unit;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

import static abeshutt.staracademy.data.adapter.basic.EnumAdapter.Mode.NAME;
import static abeshutt.staracademy.world.data.StarterMode.RAFFLE_ENABLED;
import static abeshutt.staracademy.world.data.StarterMode.RAFFLE_PAUSED;

public class PokemonStarterData extends WorldData {

    public static final PokemonStarterData CLIENT = new PokemonStarterData(0, RAFFLE_PAUSED);

    private List<StarterPokemon> starters;
    private final Map<UUID, StarterEntry> entries;
    private long timeInterval;
    private long timeLeft;

    private StarterMode mode;
    private StarterMode lastMode;

    private int allocations;

    private boolean changed;

    private PokemonStarterData(long timeInterval, StarterMode mode) {
        this.starters = new ArrayList<>();
        this.entries = new HashMap<>();
        this.timeInterval = timeInterval;
        this.timeLeft = this.timeInterval;
        this.mode = mode;
        this.lastMode = null;
    }

    public PokemonStarterData() {
        this(ModConfigs.STARTER_RAFFLE.getTimeInterval(), ModConfigs.STARTER_RAFFLE.getMode());
    }

    public List<StarterPokemon> getStarters() {
        return this.starters;
    }

    public boolean setStarters(List<StarterPokemon> starters) {
        boolean changed = !this.starters.equals(starters);
        this.starters = starters;

        if(changed) {
            this.setChanged(true);
            return true;
        }

        return false;
    }

    public Map<UUID, StarterEntry> getEntries() {
        return this.entries;
    }

    public StarterId getPick(UUID uuid) {
        StarterEntry entry = this.entries.get(uuid);
        if(entry == null) return null;
        return entry.getPick();
    }

    public void setPick(UUID uuid, StarterId pick) {
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

    public int getAllocations() {
        return this.allocations;
    }

    public boolean setAllocations(int allocations) {
        boolean changed = this.allocations != allocations;
        this.allocations = allocations;

        if(changed) {
            this.setChanged(true);
            return true;
        }

        return false;
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
        this.markDirty();
    }

    public int getRemainingAllocations(StarterId starter) {
        int allocations = 0;

        for(Map.Entry<UUID, StarterEntry> entry : this.entries.entrySet()) {
            if (starter.equals(entry.getValue().getGranted())) {
                //allocations++;
            }
        }

        return this.allocations - allocations;
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

        this.setAllocations(ModConfigs.STARTER_RAFFLE.getAllocations());

        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            StarterEntry entry = this.getEntries().get(player.getUuid());
            if(entry == null) continue;
            GeneralPlayerData playerData = Cobblemon.playerDataManager.getGenericData(player);
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

        if(!changes.isEmpty() || this.isChanged()) {
            for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                Map<UUID, StarterEntry> message = new HashMap<>();

                changes.forEach((uuid, entry) -> {
                    if(entry.getGranted() != null || player.getUuid().equals(uuid)) {
                        message.put(uuid, entry);
                    }
                });

                NetworkManager.sendToPlayer(player, new UpdateStarterRaffleS2CPacket(null,
                        message, this.timeInterval, this.timeLeft, this.mode, this.allocations));
            }
        }
    }

    private void onJoin(ServerPlayerEntity player) {
        this.entries.putIfAbsent(player.getUuid(), new StarterEntry());
        Map<UUID, StarterEntry> message = new HashMap<>();

        this.entries.forEach((uuid, entry) -> {
            if(entry.getGranted() != null || player.getUuid().equals(uuid)) {
                message.put(uuid, entry);
            }
        });

        List<StarterPokemon> starters = new ArrayList<>();
        List<StarterCategory> categories = Cobblemon.INSTANCE.getStarterHandler().getStarterList(player);

        for(StarterCategory category : categories) {
            for(int i = 0; i < category.getPokemon().size(); i++) {
               starters.add(new StarterPokemon(new StarterId(category.getName(), i),
                       category.getPokemon().get(i).asRenderablePokemon().getSpecies(),
                       category.getPokemon().get(i).getAspects()));
            }
        }

        if (message.containsKey(player.getUuid())) {
            NetworkManager.sendToPlayer(player, new UpdateStarterRaffleS2CPacket(starters,
                    Map.of(player.getUuid(), message.get(player.getUuid())),
                    this.timeInterval, this.timeLeft, this.mode, this.allocations));
        }
    }

    private void onRaffle(MinecraftServer server) {
        Map<StarterId, Set<UUID>> picks = new HashMap<>();

        this.entries.forEach((uuid, entry) -> {
            if(entry.getPick() == null) return;
            picks.computeIfAbsent(entry.getPick(), k -> new HashSet<>()).add(uuid);
        });

        picks.forEach((pick, pickers) -> {
            //if(pickers.size() > this.getRemainingAllocations(pick)) {
            if (pickers.size() > 1) {
                return;
            }

            for(UUID picker : pickers) {
                StarterEntry entry = this.getEntries().get(picker);
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(picker);
                if(player == null) return;

                this.giveStarter(player, pick).ifPresent(pokemon -> {
                    entry.setGranted(pick);

                    for(ServerPlayerEntity other : server.getPlayerManager().getPlayerList()) {
                        other.sendMessage(Text.empty()
                                .append(player.getName())
                                .append(Text.literal(" has claimed ").formatted(Formatting.GRAY))
                                .append(pokemon.getDisplayName(false).setStyle(Style.EMPTY.withColor(pokemon.getSpecies().getPrimaryType().getHue())))
                                .append(Text.literal(" as their starter!").formatted(Formatting.GRAY)));
                    }
                });
            }
        });

        for(StarterEntry entry : this.entries.values()) {
            entry.onCompleteRound();
        }
    }

    public Optional<Pokemon> giveStarter(ServerPlayerEntity player, StarterId starter) {
        GeneralPlayerData playerData = Cobblemon.playerDataManager.getGenericData(player);

        if(playerData.getStarterSelected()) {
            player.sendMessage(LocalizationUtilsKt.lang("ui.starter.alreadyselected")
                    .formatted(Formatting.RED), true);
            return Optional.empty();
        } else if(playerData.getStarterLocked()) {
            player.sendMessage(LocalizationUtilsKt.lang("ui.starter.cannotchoose")
                    .formatted(Formatting.RED), true);
            return Optional.empty();
        }

        PokemonProperties properties = null;

        for(StarterCategory category : Cobblemon.starterConfig.getStarters()) {
           if(category.getName().equals(starter.getCategory())) {
               List<PokemonProperties> pokemons = category.getPokemon();

               if(starter.getIndex() < 0 || starter.getIndex() >= pokemons.size()) {
                   return Optional.empty();
               }

               properties = pokemons.get(starter.getIndex());
               break;
           }
        }

        if(properties == null) {
            return Optional.empty();
        }

        Pokemon pokemon = properties.create();

        CobblemonEvents.STARTER_CHOSEN.postThen(new StarterChosenEvent(player, properties, pokemon),
            event -> {
                return Unit.INSTANCE;
            },
            event -> {
                Pokemon eventPokemon = event.getPokemon();
                playerData.setStarterSelected(true);
                playerData.setStarterUUID(eventPokemon.getUuid());

                if(player.getWorld().getGameRules().getBoolean(CobblemonGameRules.SHINY_STARTERS)) {
                    pokemon.setShiny(true);
                }

                Cobblemon.INSTANCE.getStorage().getParty(player).add(eventPokemon);
                CobblemonCriteria.PICK_STARTER.trigger(player, pokemon);
                Cobblemon.playerDataManager.saveSingle(playerData, PlayerInstancedDataStoreTypes.INSTANCE.getGENERAL());
                playerData.sendToPlayer(player);
                return Unit.INSTANCE;
            });

        return Optional.of(pokemon);
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
