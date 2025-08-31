package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.world.StarterEntry;
import abeshutt.staracademy.world.data.PokemonStarterData;
import abeshutt.staracademy.world.data.StarterId;
import abeshutt.staracademy.world.data.StarterMode;
import abeshutt.staracademy.world.data.StarterPokemon;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.*;

import static abeshutt.staracademy.data.adapter.basic.EnumAdapter.Mode.NAME;

public class UpdateStarterRaffleS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<UpdateStarterRaffleS2CPacket> ID = new Id<>(StarAcademyMod.id("update_starter_raffle_s2c"));

    private List<StarterPokemon> starters;
    private Map<UUID, StarterEntry> entries;
    private long timeInterval;
    private long timeLeft;
    private StarterMode mode;
    private int allocations;

    public UpdateStarterRaffleS2CPacket() {

    }

    public UpdateStarterRaffleS2CPacket(List<StarterPokemon> starters, Map<UUID, StarterEntry> entries, long timeInterval,
                                        long timeLeft, StarterMode mode, int allocations) {
        this.starters = starters;
        this.entries = entries;
        this.timeInterval = timeInterval;
        this.timeLeft = timeLeft;
        this.mode = mode;
        this.allocations = allocations;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        if(this.starters != null) {
            PokemonStarterData.CLIENT.setStarters(this.starters);
        }

        Map<UUID, StarterEntry> entries = PokemonStarterData.CLIENT.getEntries();

        if(this.entries == null) {
            entries.clear();
        } else {
            this.entries.forEach((uuid, pick) -> {
                if(pick == null) {
                    entries.remove(uuid);
                } else {
                    entries.put(uuid, pick);
                }
            });
        }

        PokemonStarterData.CLIENT.setTimeInterval(this.timeInterval);
        PokemonStarterData.CLIENT.setTimeLeft(this.timeLeft);
        PokemonStarterData.CLIENT.setMode(this.mode);
        PokemonStarterData.CLIENT.setAllocations(this.allocations);
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.BOOLEAN.writeBits(this.starters == null, buffer);

        if(this.starters != null) {
            Adapters.INT_SEGMENTED_3.writeBits(this.starters.size(), buffer);
            this.starters.forEach(species -> Adapters.STARTER_POKEMON.writeBits(species, buffer));
        }

        Adapters.BOOLEAN.writeBits(this.entries == null, buffer);

        if(this.entries != null) {
            Adapters.INT_SEGMENTED_3.writeBits(this.entries.size(), buffer);

            this.entries.forEach((uuid, profile) -> {
                Adapters.UUID.writeBits(uuid, buffer);
                profile.writeBits(buffer);
            });
        }

        Adapters.LONG.writeBits(this.timeInterval, buffer);
        Adapters.LONG.writeBits(this.timeLeft, buffer);
        Adapters.ofEnum(StarterMode.class, NAME).asNullable().writeBits(this.mode, buffer);
        Adapters.INT_SEGMENTED_3.writeBits(this.allocations, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
            this.starters = null;
        } else {
            this.starters = new ArrayList<>();
            int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

            for(int i = 0; i < size; i++) {
                this.starters.add(Adapters.STARTER_POKEMON.readBits(buffer).orElseThrow());
            }
        }

        if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
            this.entries = null;
        } else {
            this.entries = new HashMap<>();
            int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

            for(int i = 0; i < size; i++) {
                UUID uuid = Adapters.UUID.readBits(buffer).orElseThrow();
                StarterEntry entry = new StarterEntry();
                entry.readBits(buffer);
                this.entries.put(uuid, entry);
            }
        }

        this.timeInterval = Adapters.LONG.readBits(buffer).orElseThrow();
        this.timeLeft = Adapters.LONG.readBits(buffer).orElseThrow();
        this.mode = Adapters.ofEnum(StarterMode.class, NAME).asNullable().readBits(buffer).orElse(null);
        this.allocations = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
    }

}
