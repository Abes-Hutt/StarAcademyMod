package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.cobblemon.mod.common.api.pokedex.AbstractPokedexManager;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData;
import com.cobblemon.mod.common.api.storage.player.client.ClientPokedexManager;
import com.cobblemon.mod.common.pokedex.scanner.PokedexEntityData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HousePokedexManager extends AbstractPokedexManager implements InstancedPlayerData, ISerializable<NbtCompound, JsonObject> {

    private UUID uuid;
    private Map<Identifier, SpeciesDexRecord> changes;

    public HousePokedexManager() {
        this(UUID.randomUUID());
    }

    public HousePokedexManager(UUID uuid) {
        this.uuid = uuid;
        this.changes = new LinkedHashMap<>();
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Map<Identifier, SpeciesDexRecord> getChanges() {
        return this.changes;
    }

    public void clearChanges() {
        this.changes.clear();
    }

    @Override
    public void initialize() {
        this.getSpeciesRecords().forEach((identifier, speciesDexRecord) -> {
            speciesDexRecord.initialize(this, identifier);
        });
    }

    @Override
    public void onSpeciesRecordUpdated(SpeciesDexRecord record) {
        super.onSpeciesRecordUpdated(record);
        this.changes.put(record.getId(), record);
    }

    public void onEncounter(Pokemon pokemon) {
        Identifier speciesId = pokemon.getSpecies().resourceIdentifier;
        String formName = pokemon.getForm().getName();
        this.getOrCreateSpeciesRecord(speciesId).getOrCreateFormRecord(formName)
                .encountered(new PokedexEntityData(pokemon, null));
        this.onSpeciesRecordUpdated(this.getSpeciesRecord(speciesId));
    }

    public void onEncounter(PokedexEntityData data) {
        Identifier speciesId = data.getApparentSpecies().resourceIdentifier;
        String formName = data.getApparentForm().getName();
        this.getOrCreateSpeciesRecord(speciesId).getOrCreateFormRecord(formName).encountered(data);
        this.onSpeciesRecordUpdated(this.getSpeciesRecord(speciesId));
    }

    public void onCapture(Pokemon pokemon) {
        Identifier speciesId = pokemon.getSpecies().resourceIdentifier;
        String formName = pokemon.getForm().getName();
        this.getOrCreateSpeciesRecord(speciesId).getOrCreateFormRecord(formName)
                .caught(new PokedexEntityData(pokemon, null));
        this.onSpeciesRecordUpdated(this.getSpeciesRecord(speciesId));
    }

    public ClientPokedexManager toClientData() {
        Map<Identifier, SpeciesDexRecord> copied = new LinkedHashMap<>();
        this.getSpeciesRecords().forEach((id, record) -> copied.put(id, record.clone()));
        return new ClientPokedexManager(copied);
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.UUID.writeNbt(this.uuid).ifPresent(tag -> nbt.put("uuid", tag));
            NbtCompound records = new NbtCompound();

            this.getSpeciesRecords().forEach((id, record) -> {
                Adapters.SPECIES_DEX_RECORD.writeNbt(record, this).ifPresent(tag -> {
                    records.put(id.toString(), tag);
                });
            });

            nbt.put("records", records);
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElseThrow();

        this.getSpeciesRecords().clear();
        NbtCompound records = nbt.getCompound("records");

        for(String key : records.getKeys()) {
            Adapters.SPECIES_DEX_RECORD.readNbt(records.getCompound(key), this).ifPresent(record -> {
                this.getSpeciesRecords().put(Identifier.tryParse(key), record);
            });
        }
    }

}
