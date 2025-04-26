package abeshutt.staracademy.mixin.hunt;

import abeshutt.staracademy.util.ProxyCustomPrice;
import com.cobblemon.mod.common.pokemon.Pokemon;
//import org.pokesplash.hunt.Hunt;
//import org.pokesplash.hunt.config.CustomPrice;
//import org.pokesplash.hunt.hunts.SingleHunt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

//@Mixin(SingleHunt.class)
public class MixinSingleHunt {

    /*
    @Shadow private ArrayList<String> commands;
    @Shadow private Pokemon pokemon;

    @Inject(method = "getCommands", at = @At("HEAD"), remap = false)
    public void getCommands(CallbackInfoReturnable<ArrayList<String>> cir) {
        if(this.commands == null) {
            List<CustomPrice> customPrices = Hunt.config.getCustomPrices();

            for(CustomPrice item : customPrices) {
                if(item.getSpecies().trim().equalsIgnoreCase(this.pokemon.getSpecies().getName().trim())
                        && (item.getForm().trim().equalsIgnoreCase("")
                        || item.getForm().trim().equalsIgnoreCase(this.pokemon.getForm().getName().trim()))) {
                    List<String> base = ProxyCustomPrice.of(item).orElseThrow().getCommands();
                    if(base == null) base = new ArrayList<>();
                    this.commands = new ArrayList<>(base);
                    break;
                }
            }
        }
    }*/

}
