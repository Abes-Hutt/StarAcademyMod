package abeshutt.staracademy.world.data.save;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.init.ModWorldData;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;

public class PartnerData extends WorldData {

    private final Map<UUID, Identifier> selections;

    public PartnerData() {
        this.selections = new HashMap<>();
    }

    public Optional<Species> getSelection(UUID uuid) {
        return Optional.ofNullable(this.selections.get(uuid)).map(PokemonSpecies.INSTANCE::getByIdentifier);
    }

    public void setSelection(UUID uuid, Species species) {
        this.selections.put(uuid, species.getResourceIdentifier());
        this.setDirty(true);
    }

    public void setAndNotifySelection(ServerPlayerEntity player, Species species) {
        this.setSelection(player.getUuid(), species);
        if(player.getServer() == null) return;

        int color = species.getPrimaryType().getHue();

        for(ServerPlayerEntity other : player.getServer().getPlayerManager().getPlayerList()) {
            other.sendMessage(Text.empty()
                    .append(player.getName())
                    .append(Text.literal(" selected ").formatted(Formatting.GRAY))
                    .append(species.getTranslatedName().setStyle(Style.EMPTY.withColor(color)))
                    .append(Text.literal(" as their companion!").formatted(Formatting.GRAY)));
        }
    }

    public void onTick(MinecraftServer server) {
        Set<Identifier> picked = new HashSet<>(this.selections.values());

        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            PCStore pc;

            pc = Cobblemon.INSTANCE.getStorage().getPC(player);

            for(int i = 0; i < party.size(); i++) {
                Pokemon entry = party.get(i);
                if(entry == null) continue;
                if(!picked.contains(entry.getSpecies().getResourceIdentifier())) continue;

                PCPosition target = pc.getFirstAvailablePosition();

                if(target != null) {
                    party.remove(entry);
                    pc.set(target, entry);
                } else {
                    if(party.getOverflowPC(server.getRegistryManager()) == null) continue;
                    target = party.getOverflowPC(server.getRegistryManager()).getFirstAvailablePosition();
                    if(target == null) continue;
                    party.remove(entry);
                    party.getOverflowPC(server.getRegistryManager()).set(target, entry);
                }
            }
        }
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            NbtCompound selections = new NbtCompound();

            this.selections.forEach((uuid, species) -> Adapters.IDENTIFIER.writeNbt(species)
                    .ifPresent(tag -> selections.put(uuid.toString(), tag)));

            nbt.put("selections", selections);
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.selections.clear();
        NbtCompound selections = nbt.getCompound("selections");

        for(String key : selections.getKeys()) {
           Adapters.IDENTIFIER.readNbt(selections.get(key)).ifPresent(species -> {
               this.selections.put(UUID.fromString(key), species);
           });
        }
    }

    public static void init() {
        TickEvent.SERVER_POST.register(server -> {
            PartnerData data = ModWorldData.PARTNER.getGlobal(server);
            data.onTick(server);
        });
    }

}
