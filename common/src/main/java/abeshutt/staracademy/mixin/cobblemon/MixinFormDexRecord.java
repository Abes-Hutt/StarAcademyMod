package abeshutt.staracademy.mixin.cobblemon;

import abeshutt.staracademy.world.data.HousePokedexManager;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.PokedexDataChangedEvent;
import com.cobblemon.mod.common.api.pokedex.FormDexRecord;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.pokedex.scanner.PokedexEntityData;
import kotlin.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FormDexRecord.class)
public class MixinFormDexRecord {

    @Inject(method = "addInformation", at = @At("HEAD"), cancellable = true, remap = false)
    private void addInformation(PokedexEntityData pokedexEntityData, PokedexEntryProgress knowledge, CallbackInfo ci) {
        FormDexRecord value = (FormDexRecord)(Object)this;

        if(!(value.speciesDexRecord.pokedexManager instanceof HousePokedexManager pokedexManager)) {
            return;
        }

        CobblemonEvents.POKEDEX_DATA_CHANGED_PRE.postThen(
                new PokedexDataChangedEvent.Pre(
                        pokedexEntityData,
                        knowledge,
                        pokedexManager.getUuid(),
                        value
                ), pre -> {
                    return Unit.INSTANCE;
                }, pre -> {
                    value.getGenders().add(pokedexEntityData.getPokemon().getGender());
                    value.getSeenShinyStates().add(pokedexEntityData.getPokemon().getShiny() ? "shiny" : "normal");

                    if(knowledge.ordinal() > value.getKnowledge().ordinal()) {
                        value.setKnowledgeProgress(knowledge);
                    }

                    value.speciesDexRecord.addInformation(pokedexEntityData, knowledge);
                    value.speciesDexRecord.onFormRecordUpdated(value);
                    CobblemonEvents.POKEDEX_DATA_CHANGED_POST.post(
                            new PokedexDataChangedEvent.Post[] {
                                    new PokedexDataChangedEvent.Post(
                                            pokedexEntityData,
                                            knowledge,
                                            pokedexManager.getUuid(),
                                            value
                                    )
                            }, post -> Unit.INSTANCE
                    );

                    return Unit.INSTANCE;
                });

        ci.cancel();
    }

}
