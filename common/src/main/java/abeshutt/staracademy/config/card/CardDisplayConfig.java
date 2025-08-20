package abeshutt.staracademy.config.card;

import abeshutt.staracademy.attribute.again.Attribute;
import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.card.CardDisplayEntry;
import abeshutt.staracademy.card.DecimalPercentageAttributeStyle;
import abeshutt.staracademy.config.FileConfig;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.math.RoundingMode.HALF_UP;

public class CardDisplayConfig extends FileConfig {

    @Expose private List<CardDisplayEntry> displays;

    @Override
    public String getPath() {
        return "card.displays";
    }

    public Optional<CardDisplayEntry> get(Attribute<?> target) {
        for(CardDisplayEntry display : this.displays) {
            Attribute<?> child = target.path(display.getPath()).orElse(null);
            if(child == null) continue;

            if(child == target) {
                return Optional.of(display);
            }
        }

        return Optional.empty();
    }

    @Override
    protected void reset() {
        this.displays = new ArrayList<>();
        this.displays.add(new CardDisplayEntry(AttributePath.absolute("shiny_chance", "increased"),
                "text.academy.card.display.shiny_chance_increased",
                0xFFDC2E,
                new DecimalPercentageAttributeStyle(2, HALF_UP, false)));
    }

}
