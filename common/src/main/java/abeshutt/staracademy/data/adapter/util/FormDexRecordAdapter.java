package abeshutt.staracademy.data.adapter.util;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.IAdapter;
import abeshutt.staracademy.data.adapter.basic.EnumAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.cobblemon.mod.common.api.pokedex.FormDexRecord;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.cobblemon.mod.common.pokemon.Gender;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static abeshutt.staracademy.data.adapter.basic.EnumAdapter.Mode.NAME;
import static abeshutt.staracademy.data.adapter.basic.EnumAdapter.Mode.ORDINAL;
import static com.cobblemon.mod.common.pokemon.Gender.*;

public class FormDexRecordAdapter implements IAdapter<FormDexRecord, NbtCompound, JsonObject, SpeciesDexRecord> {

    private static final EnumAdapter<Gender> GENDER_NAME = Adapters.ofEnum(Gender.class, NAME);
    private static final EnumAdapter<PokedexEntryProgress> KNOWLEDGE_ORDINAL = Adapters.ofEnum(PokedexEntryProgress.class, ORDINAL);
    private static final EnumAdapter<PokedexEntryProgress> KNOWLEDGE_NAME = Adapters.ofEnum(PokedexEntryProgress.class, NAME);

    @Override
    public void writeBits(FormDexRecord value, BitBuffer buffer, SpeciesDexRecord context) {
        Adapters.UTF_8.writeBits(value.getFormName(), buffer);

        Set<Gender> genders = value.getGenders();
        Adapters.ofBoundedInt(8).writeBits((genders.contains(MALE) ? 1 : 0)
                | (genders.contains(FEMALE) ? 2 : 0) | (genders.contains(GENDERLESS) ? 4 : 0), buffer);

        Adapters.INT_SEGMENTED_3.writeBits(value.getSeenShinyStates().size(), buffer);

        for(String state : value.getSeenShinyStates()) {
            Adapters.UTF_8.writeBits(state, buffer);
        }

        KNOWLEDGE_ORDINAL.writeBits(value.getKnowledge(), buffer);
    }

    @Override
    public Optional<FormDexRecord> readBits(BitBuffer buffer, SpeciesDexRecord context) {
        FormDexRecord record = new FormDexRecord();
        record.initialize(context, Adapters.UTF_8.readBits(buffer).orElseThrow());

        int packed = Adapters.ofBoundedInt(8).readBits(buffer).orElseThrow();
        Set<Gender> genders = new HashSet<>();
        if((packed & 1) == 1) genders.add(MALE);
        if((packed & 2) == 2) genders.add(FEMALE);
        if((packed & 4) == 4) genders.add(GENDERLESS);
        record.getGenders().clear();
        record.getGenders().addAll(genders);

        int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        record.getSeenShinyStates().clear();

        for(int i = 0; i < size; i++) {
            record.getSeenShinyStates().add(Adapters.UTF_8.readBits(buffer).orElseThrow());
        }

        record.setKnowledgeProgress(KNOWLEDGE_ORDINAL.readBits(buffer).orElseThrow());
        return Optional.of(record);
    }

    @Override
    public Optional<NbtCompound> writeNbt(FormDexRecord value, SpeciesDexRecord context) {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.UTF_8.writeNbt(value.getFormName()).ifPresent(tag -> {
                nbt.put("name", tag);
            });

            NbtList genders = new NbtList();

            for(Gender gender : value.getGenders()) {
                GENDER_NAME.writeNbt(gender).ifPresent(genders::add);
            }

            nbt.put("genders", genders);

            NbtList seenShinyStates = new NbtList();

            for(String seenShinyState : value.getSeenShinyStates()) {
                Adapters.UTF_8.writeNbt(seenShinyState).ifPresent(genders::add);
            }

            nbt.put("seenShinyStates", seenShinyStates);

            KNOWLEDGE_NAME.writeNbt(value.getKnowledge()).ifPresent(tag -> nbt.put("knowledge", tag));
            return nbt;
        });
    }

    @Override
    public Optional<FormDexRecord> readNbt(NbtCompound nbt, SpeciesDexRecord context) {
        if(nbt == null) {
            return Optional.empty();
        }

        FormDexRecord value = new FormDexRecord();
        value.initialize(context, Adapters.UTF_8.readNbt(nbt.get("name")).orElseThrow());

        value.getGenders().clear();
        value.getSeenShinyStates().clear();

        if(nbt.get("genders") instanceof NbtList genders) {
            for(NbtElement gender : genders) {
               GENDER_NAME.readNbt(gender).ifPresent(value.getGenders()::add);
            }
        }

        if(nbt.get("seenShinyStates") instanceof NbtList seenShinyStates) {
            for(NbtElement state : seenShinyStates) {
                Adapters.UTF_8.readNbt(state).ifPresent(value.getSeenShinyStates()::add);
            }
        }

        KNOWLEDGE_NAME.readNbt(nbt.get("knowledge")).ifPresent(value::setKnowledgeProgress);
        return Optional.of(value);
    }

}
