package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.IBitSerializable;
import abeshutt.staracademy.world.data.AcademyHouse;
import abeshutt.staracademy.world.data.HouseData;
import abeshutt.staracademy.world.data.HousePlayer;
import abeshutt.staracademy.world.data.HousePokedexManager;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateHousesS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<UpdateHousesS2CPacket> ID = new Id<>(StarAcademyMod.id("update_house_s2c"));

    private Map<String, House> houses;

    public UpdateHousesS2CPacket() {

    }

    public UpdateHousesS2CPacket(Map<String, House> houses) {
        this.houses = houses;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        Map<String, AcademyHouse> houses = HouseData.CLIENT.getHouses();

        if(this.houses == null) {
            houses.clear();
        } else {
            this.houses.forEach((uuid, house) -> {
                if(house == null) {
                    houses.remove(uuid);
                    return;
                }

                AcademyHouse full = houses.computeIfAbsent(uuid, AcademyHouse::new);

                full.setName(house.name);
                full.setColor(house.color);

                if(house.players == null) {
                    full.getPlayers().clear();
                } else {
                    full.getPlayers().putAll(house.players);
                }

                if(house.pokedex == null) {
                    full.getPokedex().getSpeciesRecords().clear();
                } else {
                    house.pokedex.forEach((identifier, speciesDexRecord) -> {
                        speciesDexRecord.initialize(full.getPokedex(), speciesDexRecord.getId());
                    });

                    full.getPokedex().getSpeciesRecords().putAll(house.pokedex);
                }

                full.getPokedex().clearCalculatedValues();
            });
        }
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.BOOLEAN.writeBits(this.houses == null, buffer);

        if(this.houses != null) {
            Adapters.INT_SEGMENTED_3.writeBits(this.houses.size(), buffer);

            this.houses.forEach((id, entry) -> {
                Adapters.UTF_8.writeBits(id, buffer);
                entry.writeBits(buffer);
            });
        }
    }

    @Override
    public void readBits(BitBuffer buffer) {
        if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
            this.houses = null;
        } else {
            this.houses = new HashMap<>();
            int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

            for(int i = 0; i < size; i++) {
                String id = Adapters.UTF_8.readBits(buffer).orElseThrow();
                House house = new House();
                house.readBits(buffer);
                this.houses.put(id, house);
            }
        }
    }

    public static class House implements IBitSerializable {
        public String name;
        public int color;
        public Map<UUID, HousePlayer> players;
        public Map<Identifier, SpeciesDexRecord> pokedex;

        public House() {

        }

        @Override
        public void writeBits(BitBuffer buffer) {
            Adapters.UTF_8.writeBits(this.name, buffer);
            Adapters.INT.writeBits(this.color, buffer);
            Adapters.BOOLEAN.writeBits(this.players == null, buffer);

            if(this.players != null) {
                Adapters.INT_SEGMENTED_3.writeBits(this.players.size(), buffer);

                this.players.forEach((uuid, player) -> {
                    Adapters.UUID.writeBits(uuid, buffer);
                    Adapters.HOUSE_PLAYER.asNullable().writeBits(player, buffer);
                });
            }

            Adapters.BOOLEAN.writeBits(this.pokedex == null, buffer);

            if(this.pokedex != null) {
                Adapters.INT_SEGMENTED_3.writeBits(this.pokedex.size(), buffer);

                this.pokedex.forEach((id, record) -> {
                    Adapters.IDENTIFIER.writeBits(id, buffer);
                    Adapters.SPECIES_DEX_RECORD.asNullable().writeBits(record, buffer, new HousePokedexManager());
                });
            }
        }

        @Override
        public void readBits(BitBuffer buffer) {
            this.name = Adapters.UTF_8.readBits(buffer).orElseThrow();
            this.color = Adapters.INT.readBits(buffer).orElseThrow();

            if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
                this.players = null;
            } else {
                this.players = new LinkedHashMap<>();
                int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

                for(int i = 0; i < size; i++) {
                    this.players.put(
                            Adapters.UUID.readBits(buffer).orElseThrow(),
                            Adapters.HOUSE_PLAYER.asNullable().readBits(buffer).orElseThrow()
                    );
                }
            }

            if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
                this.pokedex = null;
            } else {
                this.pokedex = new LinkedHashMap<>();
                int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

                for(int i = 0; i < size; i++) {
                    this.pokedex.put(
                            Adapters.IDENTIFIER.readBits(buffer).orElseThrow(),
                            Adapters.SPECIES_DEX_RECORD.asNullable().readBits(buffer, new HousePokedexManager()).orElseThrow()
                    );
                }
            }
        }
    }

}
