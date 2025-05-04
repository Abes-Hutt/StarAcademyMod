package abeshutt.staracademy.mixin.hunt;

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
