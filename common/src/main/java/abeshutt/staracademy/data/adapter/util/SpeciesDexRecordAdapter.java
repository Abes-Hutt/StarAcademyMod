package abeshutt.staracademy.data.adapter.util;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.IAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.util.ProxySpeciesDexRecord;
import com.cobblemon.mod.common.api.pokedex.AbstractPokedexManager;
import com.cobblemon.mod.common.api.pokedex.FormDexRecord;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.Map;
import java.util.Optional;

public class SpeciesDexRecordAdapter implements IAdapter<SpeciesDexRecord, NbtCompound, JsonObject, AbstractPokedexManager> {

    private final boolean nullable;

    public SpeciesDexRecordAdapter(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public SpeciesDexRecordAdapter asNullable() {
        return new SpeciesDexRecordAdapter(true);
    }

    @Override
    public void writeBits(SpeciesDexRecord value, BitBuffer buffer, AbstractPokedexManager context) {
        if(this.nullable) {
            Adapters.BOOLEAN.writeBits(value == null, buffer);
            if(value == null) return;
        }

        Adapters.IDENTIFIER.writeBits(value.id, buffer);
        Adapters.INT_SEGMENTED_3.writeBits(value.getAspects().size(), buffer);

        for(String aspect : value.getAspects()) {
            Adapters.UTF_8.writeBits(aspect, buffer);
        }

        Map<String, FormDexRecord> formRecords = ProxySpeciesDexRecord.of(value).orElseThrow().getFormRecords();
        Adapters.INT_SEGMENTED_3.writeBits(formRecords.size(), buffer);

        for(Map.Entry<String, FormDexRecord> entry : formRecords.entrySet()) {
           Adapters.UTF_8.writeBits(entry.getKey(), buffer);
           Adapters.FORM_DEX_RECORD.writeBits(entry.getValue(), buffer, value);
        }
    }

    @Override
    public Optional<SpeciesDexRecord> readBits(BitBuffer buffer, AbstractPokedexManager context) {
        if(this.nullable && Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
            return Optional.empty();
        }

        SpeciesDexRecord record = new SpeciesDexRecord();
        record.initialize(context, Adapters.IDENTIFIER.readBits(buffer).orElseThrow());
        int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            record.getAspects().add(Adapters.UTF_8.readBits(buffer).orElseThrow());
        }

        Map<String, FormDexRecord> formRecords = ProxySpeciesDexRecord.of(record).orElseThrow().getFormRecords();
        formRecords.clear();

        size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            formRecords.put(
                Adapters.UTF_8.readBits(buffer).orElseThrow(),
                Adapters.FORM_DEX_RECORD.readBits(buffer, record).orElseThrow()
            );
        }

        return Optional.of(record);
    }

    @Override
    public Optional<NbtCompound> writeNbt(SpeciesDexRecord value, AbstractPokedexManager context) {
        if(value == null) {
            return Optional.empty();
        }

        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.IDENTIFIER.writeNbt(value.id).ifPresent(tag -> {
                nbt.put("id", tag);
            });

            NbtList aspects = new NbtList();

            for(String aspect : value.getAspects()) {
               Adapters.UTF_8.writeNbt(aspect).ifPresent(aspects::add);
            }

            nbt.put("aspects", aspects);
            Map<String, FormDexRecord> formRecords = ProxySpeciesDexRecord.of(value).orElseThrow().getFormRecords();

            NbtCompound records = new NbtCompound();

            formRecords.forEach((id, record) -> {
                Adapters.FORM_DEX_RECORD.writeNbt(record, value).ifPresent(tag -> {
                    records.put(id, tag);
                });
            });

            nbt.put("records", records);
            return nbt;
        });
    }

    @Override
    public Optional<SpeciesDexRecord> readNbt(NbtCompound nbt, AbstractPokedexManager context) {
        if(nbt == null) {
            return Optional.empty();
        }

        SpeciesDexRecord record = new SpeciesDexRecord();
        record.initialize(context, Adapters.IDENTIFIER.readNbt(nbt.get("id")).orElseThrow());

        Map<String, FormDexRecord> formRecords = ProxySpeciesDexRecord.of(record).orElseThrow().getFormRecords();
        record.getAspects().clear();
        formRecords.clear();

        if(nbt.get("aspects") instanceof NbtList aspects) {
            for(NbtElement aspect : aspects) {
                Adapters.UTF_8.readNbt(aspect).ifPresent(record.getAspects()::add);
            }
        }

        NbtCompound records = nbt.getCompound("records");

        for(String id : records.getKeys()) {
            Adapters.FORM_DEX_RECORD.readNbt(records.getCompound(id), record).ifPresent(child -> {
                formRecords.put(id, child);
            });
        }

        return Optional.of(record);
    }

}
