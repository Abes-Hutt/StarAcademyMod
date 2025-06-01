package abeshutt.staracademy.config;

import abeshutt.staracademy.util.ItemUseLogic;
import com.google.gson.annotations.Expose;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static abeshutt.staracademy.util.ItemUseLogic.CommandExecutionContext.PLAYER;
import static abeshutt.staracademy.util.ItemUseLogic.CommandExecutionContext.SERVER;

public class ItemLogicConfig extends FileConfig {

    @Expose private List<ItemUseLogic> use;

    @Override
    public String getPath() {
        return "item_logic";
    }

    public Optional<ItemUseLogic> getUseLogic(ItemStack stack) {
        for(ItemUseLogic entry : this.use) {
           if(entry.getPredicate().test(stack)) {
               return Optional.of(entry);
           }
        }

        return Optional.empty();
    }

    @Override
    protected void reset() {
        this.use = new ArrayList<>();
        this.use.add(new ItemUseLogic("academy:hunt", false, PLAYER, "/hunt"));
        //this.use.add(new ItemUseLogic("academy:shiny_incense", true, SERVER, "/yacb boost ${user_name} 4 24000 shiny"));
        //this.use.add(new ItemUseLogic("academy:strong_shiny_incense", true, SERVER, "/yacb boost ${user_name} 8 36000 shiny"));
        //this.use.add(new ItemUseLogic("academy:uber_shiny_incense", true, SERVER, "/yacb boost ${user_name} 12 72000 shiny"));
        this.use.add(new ItemUseLogic("academy:lootbag_mythsandlegends", true, SERVER, "/lootables random ${user_name} lootables:mythsandlegends"));
        this.use.add(new ItemUseLogic("academy:lootbag_megastone", true, SERVER, "/lootables random ${user_name} lootables:megastones"));
        this.use.add(new ItemUseLogic("academy:lootbag_shinyplushie", true, SERVER, "/lootables random ${user_name} lootables:pokeblocks_shiny"));
    }

}
