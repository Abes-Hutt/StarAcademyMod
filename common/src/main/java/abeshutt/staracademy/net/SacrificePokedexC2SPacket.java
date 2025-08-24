package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.init.ModWorldData;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class SacrificePokedexC2SPacket extends ModPacket<ServerPlayNetworkHandler> {

    public static final Id<SacrificePokedexC2SPacket> ID = new Id<>(StarAcademyMod.id("sacrifice_pokedex_c2s"));

    private final List<Integer> slots;

    public SacrificePokedexC2SPacket() {
        this.slots = new ArrayList<>();
    }

    public SacrificePokedexC2SPacket(List<Integer> slots) {
        this.slots = slots;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ServerPlayNetworkHandler listener) {
        ServerPlayerEntity player = listener.getPlayer();

        ModWorldData.HOUSE.getGlobal(player.getWorld()).getFor(player.getUuid()).ifPresent(house -> {
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);

            for(Integer slot : this.slots) {
                Pokemon pokemon = party.get(slot);
                if(pokemon == null) continue;

                if(house.getPokedex().getSpeciesRecord(pokemon.getSpecies().resourceIdentifier) != null) {
                    continue;
                }

                house.getPokedex().onEncounter(pokemon);
                party.remove(pokemon);
            }
        });
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.INT_SEGMENTED_3.writeBits(this.slots.size(), buffer);

        for(Integer slot : this.slots) {
           Adapters.INT_SEGMENTED_3.writeBits(slot, buffer);
        }
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.slots.clear();
        int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            this.slots.add(Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow());
        }
    }

}

