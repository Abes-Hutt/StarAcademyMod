package abeshutt.staracademy.config.card;

import abeshutt.staracademy.CardRarity;
import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.WeightedList;
import abeshutt.staracademy.card.CardIconEntry;
import abeshutt.staracademy.card.CardIconEntry.Entry;
import abeshutt.staracademy.config.FileConfig;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.world.random.RandomSource;
import com.google.gson.annotations.Expose;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static abeshutt.staracademy.StarAcademyMod.id;

public class CardIconsConfig extends FileConfig {

    @Expose private Map<String, CardIconEntry> values;
    @Expose private Map<String, Map<String, Rational>> pools;

    @Override
    public String getPath() {
        return "card.icons";
    }

    public Map<String, CardIconEntry> getValues() {
        return this.values;
    }

    public Optional<CardIconEntry> get(String id) {
        return Optional.ofNullable(this.values.get(id));
    }

    public Option<String> flatten(String id, RandomSource random) {
        if(id.startsWith("@")) {
            Map<String, Rational> group = this.pools.get(id.substring(1));

            if(group == null) {
                return Option.absent();
            }

            WeightedList<String> weighted = WeightedList.of(group);
            return weighted.getRandom(random).mapFlat(s -> this.flatten(s, random));
        }

        return this.values.containsKey(id) ? Option.present(id) : Option.absent();
    }

    @Override
    protected void reset() {
        this.values = new LinkedHashMap<>();
        this.pools = new LinkedHashMap<>();

        this.values.put("001bulbasaur", new CardIconEntry("cobblemon.species.bulbasaur.name", Map.of(
                "*", new Entry(id("card/icon/001bulbasaur"), 0x3AD6C5, 0x2D7C68),
                CardRarity.SHINY.name(), new Entry(id("card/icon/001bulbasaurshiny"), 0xA5F082, 0x387E52))
        ));

        this.values.put("002ivysaur", new CardIconEntry("cobblemon.species.ivysaur.name", Map.of(
                "*", new Entry(id("card/icon/002ivysaur"), 0x36CEBF, 0x82776E),
                CardRarity.SHINY.name(), new Entry(id("card/icon/002ivysaurshiny"), 0xBCEF76, 0x458956))
        ));

        this.values.put("003venusaur", new CardIconEntry("cobblemon.species.venusaur.name", Map.of(
                "*", new Entry(id("card/icon/003venusaur"), 0x4ACBBA, 0x916B61),
                CardRarity.SHINY.name(), new Entry(id("card/icon/003venusaurshiny"), 0xB9F079, 0x708B5C))
        ));

        this.values.put("004charmander", new CardIconEntry("cobblemon.species.charmander.name", Map.of(
                "*", new Entry(id("card/icon/004charmander"), 0xF5AA61, 0xBE5421),
                CardRarity.SHINY.name(), new Entry(id("card/icon/004charmandershiny"), 0xFBDE5F, 0xEEA744))
        ));

        this.values.put("005charmeleon", new CardIconEntry("cobblemon.species.charmeleon.name", Map.of(
                "*", new Entry(id("card/icon/005charmeleon"), 0xD64A39, 0xE4B394),
                CardRarity.SHINY.name(), new Entry(id("card/icon/005charmeleonshiny"), 0xECBE50, 0xF9EAB1))
        ));

        this.values.put("006charizard", new CardIconEntry("cobblemon.species.charizard.name", Map.of(
                "*", new Entry(id("card/icon/006charizard"), 0xF69858, 0x956F47),
                CardRarity.SHINY.name(), new Entry(id("card/icon/006charizardshiny"), 0x606A75, 0x5D3A42))
        ));

        this.values.put("007squirtle", new CardIconEntry("cobblemon.species.squirtle.name", Map.of(
                "*", new Entry(id("card/icon/007squirtle"), 0x83E1DA, 0xA76639),
                CardRarity.SHINY.name(), new Entry(id("card/icon/007squirtleshiny"), 0xA6EEE3, 0xB18C58))
        ));

        this.values.put("008wartortle", new CardIconEntry("cobblemon.species.wartortle.name", Map.of(
                "*", new Entry(id("card/icon/008wartortle"), 0xE4E8F2, 0x9E818E),
                CardRarity.SHINY.name(), new Entry(id("card/icon/008wartortleshiny"), 0xBEE0D7, 0xB7809D))
        ));

        this.values.put("009blastoise", new CardIconEntry("cobblemon.species.blastoise.name", Map.of(
                "*", new Entry(id("card/icon/009blastoise"), 0xC0B7AC, 0x6D5A5C),
                CardRarity.SHINY.name(), new Entry(id("card/icon/009blastoiseshiny"), 0xC4B298, 0x826767))
        ));

        this.values.put("010caterpie", new CardIconEntry("cobblemon.species.caterpie.name", Map.of(
                "*", new Entry(id("card/icon/010caterpie"), 0xE8DD5C, 0x76943A),
                CardRarity.SHINY.name(), new Entry(id("card/icon/010caterpieshiny"), 0xFFE25F, 0xD88B47))
        ));

        this.values.put("011metapod", new CardIconEntry("cobblemon.species.metapod.name", Map.of(
                "*", new Entry(id("card/icon/011metapod"), 0x69CC46, 0x2FA52C),
                CardRarity.SHINY.name(), new Entry(id("card/icon/011metapodshiny"), 0xFF734D, 0xCD4623))
        ));

        this.values.put("012butterfree", new CardIconEntry("cobblemon.species.butterfree.name", Map.of(
                "*", new Entry(id("card/icon/012butterfree"), 0x3B2437, 0xC8D3E0),
                CardRarity.SHINY.name(), new Entry(id("card/icon/012butterfreeshiny"), 0x31283F, 0xDFCFBE))
        ));

        this.values.put("013weedle", new CardIconEntry("cobblemon.species.weedle.name", Map.of(
                "*", new Entry(id("card/icon/013weedle"), 0xCD6D49, 0xF3B396),
                CardRarity.SHINY.name(), new Entry(id("card/icon/013weedleshiny"), 0xF3E3A7, 0xDB9E5A))
        ));

        this.values.put("014kakuna", new CardIconEntry("cobblemon.species.kakuna.name", Map.of(
                "*", new Entry(id("card/icon/014kakuna"), 0xFAD457, 0xA37226),
                CardRarity.SHINY.name(), new Entry(id("card/icon/014kakunashiny"), 0x8BEF5D, 0x428F1F))
        ));

        this.values.put("015beedrill", new CardIconEntry("cobblemon.species.beedrill.name", Map.of(
                "*", new Entry(id("card/icon/015beedrill"), 0xE9CF9B, 0x524C59),
                CardRarity.SHINY.name(), new Entry(id("card/icon/015beedrillshiny"), 0xACE394, 0x51596E))
        ));

        this.values.put("016pidgey", new CardIconEntry("cobblemon.species.pidgey.name", Map.of(
                "*", new Entry(id("card/icon/016pidgey"), 0xEAC69A, 0x915734),
                CardRarity.SHINY.name(), new Entry(id("card/icon/016pidgeyshiny"), 0xEDEF7F, 0x9CA13E))
        ));

        this.values.put("017pidgeotto", new CardIconEntry("cobblemon.species.pidgeotto.name", Map.of(
                "*", new Entry(id("card/icon/017pidgeotto"), 0xECCC91, 0xBE6349),
                CardRarity.SHINY.name(), new Entry(id("card/icon/017pidgeottoshiny"), 0xF7E99B, 0xD5B844))
        ));

        this.values.put("018pidgeot", new CardIconEntry("cobblemon.species.pidgeot.name", Map.of(
                "*", new Entry(id("card/icon/018pidgeot"), 0xC14024, 0xE7B071),
                CardRarity.SHINY.name(), new Entry(id("card/icon/018pidgeotshiny"), 0xFCD05F, 0xC68931))
        ));

        this.values.put("019rattata", new CardIconEntry("cobblemon.species.rattata.name", Map.of(
                "*", new Entry(id("card/icon/019rattata"), 0x722A63, 0xAB7997),
                CardRarity.SHINY.name(), new Entry(id("card/icon/019rattatashiny"), 0xD1EDA1, 0xCCC796))
        ));

        this.values.put("020ratticate", new CardIconEntry("cobblemon.species.ratticate.name", Map.of(
                "*", new Entry(id("card/icon/020ratticate"), 0xBA8D5E, 0xE6CB9E),
                CardRarity.SHINY.name(), new Entry(id("card/icon/020ratticateshiny"), 0xEBD4AF, 0xB9502F))
        ));

        this.values.put("021spearow", new CardIconEntry("cobblemon.species.spearow.name", Map.of(
                "*", new Entry(id("card/icon/021spearow"), 0xEAB090, 0xB65144),
                CardRarity.SHINY.name(), new Entry(id("card/icon/021spearowshiny"), 0xE6DD75, 0xAE9648))
        ));

        this.values.put("022fearow", new CardIconEntry("cobblemon.species.fearow.name", Map.of(
                "*", new Entry(id("card/icon/022fearow"), 0xB56641, 0xE5B899),
                CardRarity.SHINY.name(), new Entry(id("card/icon/022fearowshiny"), 0xD2D2A1, 0xADA153))
        ));

        this.values.put("023ekans", new CardIconEntry("cobblemon.species.ekans.name", Map.of(
                "*", new Entry(id("card/icon/023ekans"), 0xDE8390, 0xA53E87),
                CardRarity.SHINY.name(), new Entry(id("card/icon/023ekansshiny"), 0xA1B869, 0x6A7E3E))
        ));

        this.values.put("024arbok", new CardIconEntry("cobblemon.species.arbok.name", Map.of(
                "*", new Entry(id("card/icon/024arbok"), 0xCA5EAB, 0x773263),
                CardRarity.SHINY.name(), new Entry(id("card/icon/024arbokshiny"), 0xFFDB77, 0x9D7038))
        ));

        this.values.put("025pikachu", new CardIconEntry("cobblemon.species.pikachu.name", Map.of(
                "*", new Entry(id("card/icon/025pikachu"), 0xFFD74A, 0x804924),
                CardRarity.SHINY.name(), new Entry(id("card/icon/025pikachushiny"), 0xFFA847, 0x884334))
        ));

        this.values.put("026raichu", new CardIconEntry("cobblemon.species.raichu.name", Map.of(
                "*", new Entry(id("card/icon/026raichu"), 0xFBC451, 0x82542A),
                CardRarity.SHINY.name(), new Entry(id("card/icon/026raichushiny"), 0xEBC0AC, 0x925330))
        ));

        this.values.put("027sandshrew", new CardIconEntry("cobblemon.species.sandshrew.name", Map.of(
                "*", new Entry(id("card/icon/027sandshrew"), 0xFDE27F, 0xCAB05E),
                CardRarity.SHINY.name(), new Entry(id("card/icon/027sandshrewshiny"), 0x547C39, 0xBCDA9C))
        ));

        this.values.put("028sandslash", new CardIconEntry("cobblemon.species.sandslash.name", Map.of(
                "*", new Entry(id("card/icon/028sandslash"), 0x3F2D1A, 0xD0B87A),
                CardRarity.SHINY.name(), new Entry(id("card/icon/028sandslashshiny"), 0x952A2C, 0xF1C4A8))
        ));

        this.values.put("029nidoranf", new CardIconEntry("cobblemon.species.nidoranf.name", Map.of(
                "*", new Entry(id("card/icon/029nidoranf"), 0xBDD3FF, 0x5A83A5),
                CardRarity.SHINY.name(), new Entry(id("card/icon/029nidoranfshiny"), 0xEAADF3, 0x9E4FA1))
        ));

        this.values.put("030nidorina", new CardIconEntry("cobblemon.species.nidorina.name", Map.of(
                "*", new Entry(id("card/icon/030nidorina"), 0xBAFFF9, 0x5FA6BA),
                CardRarity.SHINY.name(), new Entry(id("card/icon/030nidorinashiny"), 0xE59FD1, 0xA3628A))
        ));

        this.values.put("031nidoqueen", new CardIconEntry("cobblemon.species.nidoqueen.name", Map.of(
                "*", new Entry(id("card/icon/031nidoqueen"), 0x4899C1, 0xA5CFCC),
                CardRarity.SHINY.name(), new Entry(id("card/icon/031nidoqueenshiny"), 0x93C08E, 0xBF7885))
        ));

        this.values.put("032nidoranm", new CardIconEntry("cobblemon.species.nidoranm.name", Map.of(
                "*", new Entry(id("card/icon/032nidoranm"), 0xC277B4, 0x6B3165),
                CardRarity.SHINY.name(), new Entry(id("card/icon/032nidoranmshiny"), 0xB6E1E3, 0x869388))
        ));

        this.values.put("033nidorino", new CardIconEntry("cobblemon.species.nidorino.name", Map.of(
                "*", new Entry(id("card/icon/033nidorino"), 0xA6679A, 0x904178),
                CardRarity.SHINY.name(), new Entry(id("card/icon/033nidorinoshiny"), 0x92C1C4, 0x77857B))
        ));

        this.values.put("034nidoking", new CardIconEntry("cobblemon.species.nidoking.name", Map.of(
                "*", new Entry(id("card/icon/034nidoking"), 0xB394C1, 0x805690),
                CardRarity.SHINY.name(), new Entry(id("card/icon/034nidokingshiny"), 0x294E9C, 0x597ECA))
        ));

        this.values.put("035clefairy", new CardIconEntry("cobblemon.species.clefairy.name", Map.of(
                "*", new Entry(id("card/icon/035clefairy"), 0xFFB4B4, 0x865050),
                CardRarity.SHINY.name(), new Entry(id("card/icon/035clefairyshiny"), 0xE6CBBC, 0xB27374))
        ));

        this.values.put("036clefable", new CardIconEntry("cobblemon.species.clefable.name", Map.of(
                "*", new Entry(id("card/icon/036clefable"), 0xFFAEAE, 0xBD676A),
                CardRarity.SHINY.name(), new Entry(id("card/icon/036clefableshiny"), 0xF6D9D4, 0xD18E90))
        ));

        this.values.put("037vulpix", new CardIconEntry("cobblemon.species.vulpix.name", Map.of(
                "*", new Entry(id("card/icon/037vulpix"), 0xD0511C, 0xED7E51),
                CardRarity.SHINY.name(), new Entry(id("card/icon/037vulpixshiny"), 0xC6841D, 0xE9BD49))
        ));

        this.values.put("038ninetales", new CardIconEntry("cobblemon.species.ninetales.name", Map.of(
                "*", new Entry(id("card/icon/038ninetales"), 0xFFE58B, 0xDEAB4C),
                CardRarity.SHINY.name(), new Entry(id("card/icon/038ninetalesshiny"), 0xCBCBCB, 0x8C8D97))
        ));

        this.values.put("039jigglypuff", new CardIconEntry("cobblemon.species.jigglypuff.name", Map.of(
                "*", new Entry(id("card/icon/039jigglypuff"), 0xEBB6CE, 0x73677D),
                CardRarity.SHINY.name(), new Entry(id("card/icon/039jigglypuffshiny"), 0xEBB9D8, 0x7B696A))
        ));

        this.values.put("040wigglytuff", new CardIconEntry("cobblemon.species.wigglytuff.name", Map.of(
                "*", new Entry(id("card/icon/040wigglytuff"), 0xE38DA9, 0xEECDDB),
                CardRarity.SHINY.name(), new Entry(id("card/icon/040wigglytuffshiny"), 0xDB92DE, 0xEACFEF))
        ));

        this.values.put("041zubat", new CardIconEntry("cobblemon.species.zubat.name", Map.of(
                "*", new Entry(id("card/icon/041zubat"), 0x5683DD, 0x7C3A86),
                CardRarity.SHINY.name(), new Entry(id("card/icon/041zubatshiny"), 0x9FE469, 0x98A656))
        ));

        this.values.put("042golbat", new CardIconEntry("cobblemon.species.golbat.name", Map.of(
                "*", new Entry(id("card/icon/042golbat"), 0x648FE4, 0x52315E),
                CardRarity.SHINY.name(), new Entry(id("card/icon/042golbatshiny"), 0x80E464, 0x95615A))
        ));

        this.values.put("043oddish", new CardIconEntry("cobblemon.species.oddish.name", Map.of(
                "*", new Entry(id("card/icon/043oddish"), 0x259C2E, 0x2D436E),
                CardRarity.SHINY.name(), new Entry(id("card/icon/043oddishshiny"), 0x64A429, 0x3F6B27))
        ));

        this.values.put("044gloom", new CardIconEntry("cobblemon.species.gloom.name", Map.of(
                "*", new Entry(id("card/icon/044gloom"), 0x563038, 0x7C5463),
                CardRarity.SHINY.name(), new Entry(id("card/icon/044gloomshiny"), 0x879B3C, 0x57611D))
        ));

        this.values.put("045vileplume", new CardIconEntry("cobblemon.species.vileplume.name", Map.of(
                "*", new Entry(id("card/icon/045vileplume"), 0xE17573, 0x1F3961),
                CardRarity.SHINY.name(), new Entry(id("card/icon/045vileplumeshiny"), 0x959C5B, 0x866A3C))
        ));

        this.values.put("046paras", new CardIconEntry("cobblemon.species.paras.name", Map.of(
                "*", new Entry(id("card/icon/046paras"), 0xFEB06E, 0xB86646),
                CardRarity.SHINY.name(), new Entry(id("card/icon/046parasshiny"), 0xF28B79, 0xAB4134))
        ));

        this.values.put("047parasect", new CardIconEntry("cobblemon.species.parasect.name", Map.of(
                "*", new Entry(id("card/icon/047parasect"), 0xF6B46F, 0xCE6563),
                CardRarity.SHINY.name(), new Entry(id("card/icon/047parasectshiny"), 0xF6D29A, 0xD58B85))
        ));

        this.values.put("048venonat", new CardIconEntry("cobblemon.species.venonat.name", Map.of(
                "*", new Entry(id("card/icon/048venonat"), 0x7B316A, 0xBE799F),
                CardRarity.SHINY.name(), new Entry(id("card/icon/048venonatshiny"), 0x374C7A, 0x828FBE))
        ));

        this.values.put("049venomoth", new CardIconEntry("cobblemon.species.venomoth.name", Map.of(
                "*", new Entry(id("card/icon/049venomoth"), 0xEBD3E4, 0xC1A6BA),
                CardRarity.SHINY.name(), new Entry(id("card/icon/049venomothshiny"), 0x8DB8F8, 0x6693CE))
        ));

        this.values.put("050diglet", new CardIconEntry("cobblemon.species.diglet.name", Map.of(
                "*", new Entry(id("card/icon/050diglet"), 0xAB6F4F, 0x54413B),
                CardRarity.SHINY.name(), new Entry(id("card/icon/050digletshiny"), 0x978486, 0x564B42))
        ));

        this.values.put("051dugtrio", new CardIconEntry("cobblemon.species.dugtrio.name", Map.of(
                "*", new Entry(id("card/icon/051dugtrio"), 0xB17150, 0x583E34),
                CardRarity.SHINY.name(), new Entry(id("card/icon/051dugtrioshiny"), 0xA48A89, 0x56483E))
        ));

        this.values.put("052meowth", new CardIconEntry("cobblemon.species.meowth.name", Map.of(
                "*", new Entry(id("card/icon/052meowth"), 0xF9ECB3, 0xA88255),
                CardRarity.SHINY.name(), new Entry(id("card/icon/052meowthshiny"), 0xF2EAC1, 0xAE7F71))
        ));

        this.values.put("053persian", new CardIconEntry("cobblemon.species.persian.name", Map.of(
                "*", new Entry(id("card/icon/053persian"), 0xF8EBB3, 0xB29C66),
                CardRarity.SHINY.name(), new Entry(id("card/icon/053persianshiny"), 0xF1EACE, 0xAC987E))
        ));

        this.values.put("054psyduck", new CardIconEntry("cobblemon.species.psyduck.name", Map.of(
                "*", new Entry(id("card/icon/054psyduck"), 0xFEE7A0, 0xCAA661),
                CardRarity.SHINY.name(), new Entry(id("card/icon/054psyduckshiny"), 0x9AE0E7, 0x68B3C1))
        ));

        this.values.put("055golduck", new CardIconEntry("cobblemon.species.golduck.name", Map.of(
                "*", new Entry(id("card/icon/055golduck"), 0x3778A8, 0x67BDE4),
                CardRarity.SHINY.name(), new Entry(id("card/icon/055golduckshiny"), 0x36AFEC, 0x786A95))
        ));

        this.values.put("056mankey", new CardIconEntry("cobblemon.species.mankey.name", Map.of(
                "*", new Entry(id("card/icon/056mankey"), 0xF1DFB4, 0xC18A65),
                CardRarity.SHINY.name(), new Entry(id("card/icon/056mankeyshiny"), 0xD2E993, 0xB0A856))
        ));

        this.values.put("057primeape", new CardIconEntry("cobblemon.species.primeape.name", Map.of(
                "*", new Entry(id("card/icon/057primeape"), 0xE6C8A0, 0x8D6349),
                CardRarity.SHINY.name(), new Entry(id("card/icon/057primeapeshiny"), 0xE0C09C, 0x84744D))
        ));

        this.values.put("058growlithe", new CardIconEntry("cobblemon.species.growlithe.name", Map.of(
                "*", new Entry(id("card/icon/058growlithe"), 0xEDD295, 0xBA703D),
                CardRarity.SHINY.name(), new Entry(id("card/icon/058growlitheshiny"), 0xECF1B5, 0xBDA953))
        ));

        this.values.put("059arcanine", new CardIconEntry("cobblemon.species.arcanine.name", Map.of(
                "*", new Entry(id("card/icon/059arcanine"), 0xF0D392, 0xB7794C),
                CardRarity.SHINY.name(), new Entry(id("card/icon/059arcanineshiny"), 0xF4F7A6, 0xBDA657))
        ));

        this.values.put("060poliwag", new CardIconEntry("cobblemon.species.poliwag.name", Map.of(
                "*", new Entry(id("card/icon/060poliwag"), 0x384F82, 0xCCDAF7),
                CardRarity.SHINY.name(), new Entry(id("card/icon/060poliwagshiny"), 0xCCEFF7, 0x387383))
        ));

        this.values.put("061poliwhirl", new CardIconEntry("cobblemon.species.poliwhirl.name", Map.of(
                "*", new Entry(id("card/icon/061poliwhirl"), 0x223A58, 0xB4C5E7),
                CardRarity.SHINY.name(), new Entry(id("card/icon/061poliwhirlshiny"), 0xB4D9E7, 0x224C54))
        ));

        this.values.put("062poliwrath", new CardIconEntry("cobblemon.species.poliwrath.name", Map.of(
                "*", new Entry(id("card/icon/062poliwrath"), 0x99B1E1, 0x254068),
                CardRarity.SHINY.name(), new Entry(id("card/icon/062poliwrathshiny"), 0xB8DC9E, 0x45632A))
        ));

        this.values.put("063abra", new CardIconEntry("cobblemon.species.abra.name", Map.of(
                "*", new Entry(id("card/icon/063abra"), 0xFFC971, 0xB58C4D),
                CardRarity.SHINY.name(), new Entry(id("card/icon/063abrashiny"), 0xF4E4A2, 0xB2A46A))
        ));

        this.values.put("064kadabra", new CardIconEntry("cobblemon.species.kadabra.name", Map.of(
                "*", new Entry(id("card/icon/064kadabra"), 0xFECD80, 0xA17F59),
                CardRarity.SHINY.name(), new Entry(id("card/icon/064kadabrashiny"), 0xFAECB0, 0xA99A72))
        ));

        this.values.put("065alakazam", new CardIconEntry("cobblemon.species.alakazam.name", Map.of(
                "*", new Entry(id("card/icon/065alakazam"), 0xFFE383, 0x906955),
                CardRarity.SHINY.name(), new Entry(id("card/icon/065alakazamshiny"), 0xF9F7B0, 0xAB777E))
        ));

        this.values.put("066machop", new CardIconEntry("cobblemon.species.machop.name", Map.of(
                "*", new Entry(id("card/icon/066machop"), 0xD2FFEE, 0xA7ACA0),
                CardRarity.SHINY.name(), new Entry(id("card/icon/066machopshiny"), 0xF6E4B5, 0xB39488))
        ));

        this.values.put("067machoke", new CardIconEntry("cobblemon.species.machoke.name", Map.of(
                "*", new Entry(id("card/icon/067machoke"), 0xC4C5F0, 0x8B7CAB),
                CardRarity.SHINY.name(), new Entry(id("card/icon/067machokeshiny"), 0xA4EEA2, 0x88AB8B))
        ));

        this.values.put("068machamp", new CardIconEntry("cobblemon.species.machamp.name", Map.of(
                "*", new Entry(id("card/icon/068machamp"), 0xD8DFFB, 0xB4ABBD),
                CardRarity.SHINY.name(), new Entry(id("card/icon/068machampshiny"), 0xA7FF9F, 0xA8C28C))
        ));

        this.values.put("069bellsprout", new CardIconEntry("cobblemon.species.bellsprout.name", Map.of(
                "*", new Entry(id("card/icon/069bellsprout"), 0xCEDD75, 0x78714C),
                CardRarity.SHINY.name(), new Entry(id("card/icon/069bellsproutshiny"), 0xF2DD7D, 0x8A794C))
        ));

        this.values.put("070weepinbell", new CardIconEntry("cobblemon.species.weepinbell.name", Map.of(
                "*", new Entry(id("card/icon/070weepinbell"), 0xDBE090, 0x716D4E),
                CardRarity.SHINY.name(), new Entry(id("card/icon/070weepinbellshiny"), 0xC5F297, 0x6A7A53))
        ));

        this.values.put("071victreebel", new CardIconEntry("cobblemon.species.victreebel.name", Map.of(
                "*", new Entry(id("card/icon/071victreebel"), 0xB7E581, 0x998856),
                CardRarity.SHINY.name(), new Entry(id("card/icon/071victreebelshiny"), 0xD6E691, 0x87856C))
        ));

        this.values.put("072tentacool", new CardIconEntry("cobblemon.species.tentacool.name", Map.of(
                "*", new Entry(id("card/icon/072tentacool"), 0x8EC2DB, 0x854E5A),
                CardRarity.SHINY.name(), new Entry(id("card/icon/072tentacoolshiny"), 0x96BF97, 0x856DA4))
        ));

        this.values.put("073tentacruel", new CardIconEntry("cobblemon.species.tentacruel.name", Map.of(
                "*", new Entry(id("card/icon/073tentacruel"), 0x83CCF2, 0x8A4348),
                CardRarity.SHINY.name(), new Entry(id("card/icon/073tentacruelshiny"), 0x75BDD7, 0x4D607F))
        ));

        this.values.put("074geodude", new CardIconEntry("cobblemon.species.geodude.name", Map.of(
                "*", new Entry(id("card/icon/074geodude"), 0xCBC0B5, 0x9E9282),
                CardRarity.SHINY.name(), new Entry(id("card/icon/074geodudeshiny"), 0xF5DE63, 0xC38B27))
        ));

        this.values.put("075graveller", new CardIconEntry("cobblemon.species.graveller.name", Map.of(
                "*", new Entry(id("card/icon/075graveller"), 0xD1C5B6, 0xAA8980),
                CardRarity.SHINY.name(), new Entry(id("card/icon/075gravellershiny"), 0xD29B5B, 0xA26C43))
        ));

        this.values.put("076golem", new CardIconEntry("cobblemon.species.golem.name", Map.of(
                "*", new Entry(id("card/icon/076golem"), 0x695A4E, 0x9B8F81),
                CardRarity.SHINY.name(), new Entry(id("card/icon/076golemshiny"), 0x815629, 0xBCA283))
        ));

        this.values.put("077ponyta", new CardIconEntry("cobblemon.species.ponyta.name", Map.of(
                "*", new Entry(id("card/icon/077ponyta"), 0xFBE593, 0xE67442),
                CardRarity.SHINY.name(), new Entry(id("card/icon/077ponytashiny"), 0x85B9D8, 0xBDFAE2))
        ));

        this.values.put("078rapidash", new CardIconEntry("cobblemon.species.rapidash.name", Map.of(
                "*", new Entry(id("card/icon/078rapidash"), 0xFFE997, 0xDD7F4F),
                CardRarity.SHINY.name(), new Entry(id("card/icon/078rapidashshiny"), 0x666788, 0xCDC8B8))
        ));

        this.values.put("079slowpoke", new CardIconEntry("cobblemon.species.slowpoke.name", Map.of(
                "*", new Entry(id("card/icon/079slowpoke"), 0xE38E85, 0xFAE4D5),
                CardRarity.SHINY.name(), new Entry(id("card/icon/079slowpokeshiny"), 0xDDA7A2, 0xF8ECE4))
        ));

        this.values.put("080slowbro", new CardIconEntry("cobblemon.species.slowbro.name", Map.of(
                "*", new Entry(id("card/icon/080slowbro"), 0xBC8D96, 0xEEDDCD),
                CardRarity.SHINY.name(), new Entry(id("card/icon/080slowbroshiny"), 0xAB8DA8, 0xE7DCD5))
        ));

        this.values.put("081magnemite", new CardIconEntry("cobblemon.species.magnemite.name", Map.of(
                "*", new Entry(id("card/icon/081magnemite"), 0xE0E2F7, 0x8989A7),
                CardRarity.SHINY.name(), new Entry(id("card/icon/081magnemiteshiny"), 0xEEF0D7, 0x84858C))
        ));

        this.values.put("082magneton", new CardIconEntry("cobblemon.species.magneton.name", Map.of(
                "*", new Entry(id("card/icon/082magneton"), 0xD4D6EF, 0x7F7D9C),
                CardRarity.SHINY.name(), new Entry(id("card/icon/082magnetonshiny"), 0xDDDFCE, 0x696B76))
        ));

        this.values.put("083farfetchd", new CardIconEntry("cobblemon.species.farfetchd.name", Map.of(
                "*", new Entry(id("card/icon/083farfetchd"), 0xBAA66E, 0x766144),
                CardRarity.SHINY.name(), new Entry(id("card/icon/083farfetchdshiny"), 0xD0B49F, 0xA2615C))
        ));

        this.values.put("084doduo", new CardIconEntry("cobblemon.species.doduo.name", Map.of(
                "*", new Entry(id("card/icon/084doduo"), 0xF3C686, 0xB38547),
                CardRarity.SHINY.name(), new Entry(id("card/icon/084doduoshiny"), 0xE0F3BA, 0x9DB468))
        ));

        this.values.put("085dodrio", new CardIconEntry("cobblemon.species.dodrio.name", Map.of(
                "*", new Entry(id("card/icon/085dodrio"), 0xF1C793, 0x5E3B2F),
                CardRarity.SHINY.name(), new Entry(id("card/icon/085dodrioshiny"), 0xE6F0B7, 0x6E5343))
        ));

        this.values.put("086seel", new CardIconEntry("cobblemon.species.seel.name", Map.of(
                "*", new Entry(id("card/icon/086seel"), 0xEDEEFA, 0xC9BBC5),
                CardRarity.SHINY.name(), new Entry(id("card/icon/086seelshiny"), 0xFBFFEC, 0xE0C7B8))
        ));

        this.values.put("087dewgong", new CardIconEntry("cobblemon.species.dewgong.name", Map.of(
                "*", new Entry(id("card/icon/087dewgong"), 0xE9ECFF, 0xB6BAD7),
                CardRarity.SHINY.name(), new Entry(id("card/icon/087dewgongshiny"), 0xF8FCDE, 0xCCD1B0))
        ));

        this.values.put("088grimer", new CardIconEntry("cobblemon.species.grimer.name", Map.of(
                "*", new Entry(id("card/icon/088grimer"), 0xCE91C0, 0x7F4E72),
                CardRarity.SHINY.name(), new Entry(id("card/icon/088grimershiny"), 0x9BCEAC, 0x567C64))
        ));

        this.values.put("089muk", new CardIconEntry("cobblemon.species.muk.name", Map.of(
                "*", new Entry(id("card/icon/089muk"), 0x9F8197, 0x64455C),
                CardRarity.SHINY.name(), new Entry(id("card/icon/089mukshiny"), 0x80A089, 0x44664E))
        ));

        this.values.put("090shellder", new CardIconEntry("cobblemon.species.shellder.name", Map.of(
                "*", new Entry(id("card/icon/090shellder"), 0x9A8BC9, 0x3D3C70),
                CardRarity.SHINY.name(), new Entry(id("card/icon/090shelldershiny"), 0xE8B066, 0x684B29))
        ));

        this.values.put("091cloyster", new CardIconEntry("cobblemon.species.cloyster.name", Map.of(
                "*", new Entry(id("card/icon/091cloyster"), 0x8386C0, 0x434071),
                CardRarity.SHINY.name(), new Entry(id("card/icon/091cloystershiny"), 0x84A4C2, 0x415671))
        ));

        this.values.put("092gastly", new CardIconEntry("cobblemon.species.gastly.name", Map.of(
                "*", new Entry(id("card/icon/092gastly"), 0x986D98, 0x242234),
                CardRarity.SHINY.name(), new Entry(id("card/icon/092gastlyshiny"), 0x727D9C, 0x2B353A))
        ));

        this.values.put("093haunter", new CardIconEntry("cobblemon.species.haunter.name", Map.of(
                "*", new Entry(id("card/icon/093haunter"), 0x66336C, 0x915983),
                CardRarity.SHINY.name(), new Entry(id("card/icon/093hauntershiny"), 0x33356C, 0x55619D))
        ));

        this.values.put("094gengar", new CardIconEntry("cobblemon.species.gengar.name", Map.of(
                "*", new Entry(id("card/icon/094gengar"), 0x12082B, 0x4D335A),
                CardRarity.SHINY.name(), new Entry(id("card/icon/094gengarshiny"), 0x181536, 0x594D74))
        ));

        this.values.put("095onix", new CardIconEntry("cobblemon.species.onix.name", Map.of(
                "*", new Entry(id("card/icon/095onix"), 0xBABEC7, 0x6E737F),
                CardRarity.SHINY.name(), new Entry(id("card/icon/095onixshiny"), 0xB8C78C, 0x747E4F))
        ));

        this.values.put("096drowzee", new CardIconEntry("cobblemon.species.drowzee.name", Map.of(
                "*", new Entry(id("card/icon/096drowzee"), 0xFCDE80, 0x685942),
                CardRarity.SHINY.name(), new Entry(id("card/icon/096drowzeeshiny"), 0xFCCEEF, 0x874C69))
        ));

        this.values.put("097hypno", new CardIconEntry("cobblemon.species.hypno.name", Map.of(
                "*", new Entry(id("card/icon/097hypno"), 0xFCE8A3, 0xD2BA75),
                CardRarity.SHINY.name(), new Entry(id("card/icon/097hypnoshiny"), 0xF6B6D0, 0xDB739C))
        ));

        this.values.put("098krabby", new CardIconEntry("cobblemon.species.krabby.name", Map.of(
                "*", new Entry(id("card/icon/098krabby"), 0xE17E3F, 0xFBC695),
                CardRarity.SHINY.name(), new Entry(id("card/icon/098krabbyshiny"), 0xFBE595, 0xE1AF3F))
        ));

        this.values.put("099kingler", new CardIconEntry("cobblemon.species.kingler.name", Map.of(
                "*", new Entry(id("card/icon/099kingler"), 0xFBC08B, 0xC67039),
                CardRarity.SHINY.name(), new Entry(id("card/icon/099kinglershiny"), 0xC9E8AB, 0x8BAC5E))
        ));

        this.values.put("100voltorb", new CardIconEntry("cobblemon.species.voltorb.name", Map.of(
                "*", new Entry(id("card/icon/100voltorb"), 0xCB343E, 0xF39DA3),
                CardRarity.SHINY.name(), new Entry(id("card/icon/100voltorbshiny"), 0x3355CB, 0x9DB0F3))
        ));

        this.values.put("101electrode", new CardIconEntry("cobblemon.species.electrode.name", Map.of(
                "*", new Entry(id("card/icon/101electrode"), 0xFAFAFA, 0xB64048),
                CardRarity.SHINY.name(), new Entry(id("card/icon/101electrodeshiny"), 0xFAFAFA, 0x4048B6))
        ));

        this.values.put("102exeggcute", new CardIconEntry("cobblemon.species.exeggcute.name", Map.of(
                "*", new Entry(id("card/icon/102exeggcute"), 0xBF7974, 0xFFC7BB),
                CardRarity.SHINY.name(), new Entry(id("card/icon/102exeggcuteshiny"), 0xFDE5B7, 0xC79E69))
        ));

        this.values.put("103exeggutor", new CardIconEntry("cobblemon.species.exeggutor.name", Map.of(
                "*", new Entry(id("card/icon/103exeggutor"), 0xB7DE94, 0x8A7D5B),
                CardRarity.SHINY.name(), new Entry(id("card/icon/103exeggutorshiny"), 0xDDC092, 0x8F694B))
        ));

        this.values.put("104cubone", new CardIconEntry("cobblemon.species.cubone.name", Map.of(
                "*", new Entry(id("card/icon/104cubone"), 0xF2F0E7, 0xC79960),
                CardRarity.SHINY.name(), new Entry(id("card/icon/104cuboneshiny"), 0xE9EDDF, 0x6CA065))
        ));

        this.values.put("105marowak", new CardIconEntry("cobblemon.species.marowak.name", Map.of(
                "*", new Entry(id("card/icon/105marowak"), 0xF7EDDD, 0xBA905E),
                CardRarity.SHINY.name(), new Entry(id("card/icon/105marowakshiny"), 0xE2ECC6, 0xA6AB6A))
        ));

        this.values.put("106hitmonlee", new CardIconEntry("cobblemon.species.hitmonlee.name", Map.of(
                "*", new Entry(id("card/icon/106hitmonlee"), 0xE9D1A2, 0x976F5E),
                CardRarity.SHINY.name(), new Entry(id("card/icon/106hitmonleeshiny"), 0x8A9767, 0xD6EAAB))
        ));

        this.values.put("107hitmonchan", new CardIconEntry("cobblemon.species.hitmonchan.name", Map.of(
                "*", new Entry(id("card/icon/107hitmonchan"), 0xB2A2A7, 0xB13B3F),
                CardRarity.SHINY.name(), new Entry(id("card/icon/107hitmonchanshiny"), 0xA7B7B3, 0x4141AA))
        ));

        this.values.put("108lickitung", new CardIconEntry("cobblemon.species.lickitung.name", Map.of(
                "*", new Entry(id("card/icon/108lickitung"), 0xFFC5B3, 0xDF7E7F),
                CardRarity.SHINY.name(), new Entry(id("card/icon/108lickitungshiny"), 0xFEE8C1, 0xE49380))
        ));

        this.values.put("109koffing", new CardIconEntry("cobblemon.species.koffing.name", Map.of(
                "*", new Entry(id("card/icon/109koffing"), 0xA45BA0, 0xBE94B3),
                CardRarity.SHINY.name(), new Entry(id("card/icon/109koffingshiny"), 0x8DC2B2, 0x7F96A1))
        ));

        this.values.put("110weezing", new CardIconEntry("cobblemon.species.weezing.name", Map.of(
                "*", new Entry(id("card/icon/110weezing"), 0xBF99B0, 0xA35891),
                CardRarity.SHINY.name(), new Entry(id("card/icon/110weezingshiny"), 0x92BAC3, 0x8A7FA3))
        ));

        this.values.put("111rhyhorn", new CardIconEntry("cobblemon.species.rhyhorn.name", Map.of(
                "*", new Entry(id("card/icon/111rhyhorn"), 0xA8B0CA, 0x6E7390),
                CardRarity.SHINY.name(), new Entry(id("card/icon/111rhyhornshiny"), 0xDBB397, 0xA87759))
        ));

        this.values.put("112rhydon", new CardIconEntry("cobblemon.species.rhydon.name", Map.of(
                "*", new Entry(id("card/icon/112rhydon"), 0xB1B7CB, 0x8C8A97),
                CardRarity.SHINY.name(), new Entry(id("card/icon/112rhydonshiny"), 0xCED3AD, 0xA6A17C))
        ));

        this.values.put("113chansey", new CardIconEntry("cobblemon.species.chansey.name", Map.of(
                "*", new Entry(id("card/icon/113chansey"), 0xFDCFCA, 0xE69496),
                CardRarity.SHINY.name(), new Entry(id("card/icon/113chanseyshiny"), 0xE6E3CB, 0xC5A499))
        ));

        this.values.put("114tangela", new CardIconEntry("cobblemon.species.tangela.name", Map.of(
                "*", new Entry(id("card/icon/114tangela"), 0x7881AF, 0x3C1C1A),
                CardRarity.SHINY.name(), new Entry(id("card/icon/114tangelashiny"), 0x68C55E, 0x69322A))
        ));

        this.values.put("115kangaskhan", new CardIconEntry("cobblemon.species.kangaskhan.name", Map.of(
                "*", new Entry(id("card/icon/115kangaskhan"), 0xE9D7B4, 0x997C65),
                CardRarity.SHINY.name(), new Entry(id("card/icon/115kangaskhanshiny"), 0xE6DFD2, 0x958E78))
        ));

        this.values.put("116horsea", new CardIconEntry("cobblemon.species.horsea.name", Map.of(
                "*", new Entry(id("card/icon/116horsea"), 0xA3E9E6, 0x5DBCD1),
                CardRarity.SHINY.name(), new Entry(id("card/icon/116horseashiny"), 0x7BE7CF, 0xBAAAA6))
        ));

        this.values.put("117seadra", new CardIconEntry("cobblemon.species.seadra.name", Map.of(
                "*", new Entry(id("card/icon/117seadra"), 0x9EEDF4, 0x57CBDF),
                CardRarity.SHINY.name(), new Entry(id("card/icon/117seadrashiny"), 0xAAA9EC, 0x6476D0))
        ));

        this.values.put("118goldeen", new CardIconEntry("cobblemon.species.goldeen.name", Map.of(
                "*", new Entry(id("card/icon/118goldeen"), 0xF8F8F5, 0xDD5941),
                CardRarity.SHINY.name(), new Entry(id("card/icon/118goldeenshiny"), 0xF8F8F5, 0xDE863C))
        ));

        this.values.put("119seaking", new CardIconEntry("cobblemon.species.seaking.name", Map.of(
                "*", new Entry(id("card/icon/119seaking"), 0xF6F6F4, 0x8E3E2D),
                CardRarity.SHINY.name(), new Entry(id("card/icon/119seakingshiny"), 0xF6F6F4, 0x8E662A))
        ));

        this.values.put("120staryu", new CardIconEntry("cobblemon.species.staryu.name", Map.of(
                "*", new Entry(id("card/icon/120staryu"), 0xDFAA52, 0xA35A3C),
                CardRarity.SHINY.name(), new Entry(id("card/icon/120staryushiny"), 0xF2E1B2, 0xA18795))
        ));

        this.values.put("121starmie", new CardIconEntry("cobblemon.species.starmie.name", Map.of(
                "*", new Entry(id("card/icon/121starmie"), 0xC1AB93, 0x833E5B),
                CardRarity.SHINY.name(), new Entry(id("card/icon/121starmieshiny"), 0x9F95B9, 0x82498D))
        ));

        this.values.put("122mrmime", new CardIconEntry("cobblemon.species.mrmime.name", Map.of(
                "*", new Entry(id("card/icon/122mrmime"), 0xF1C5C6, 0x544667),
                CardRarity.SHINY.name(), new Entry(id("card/icon/122mrmimeshiny"), 0xC9EAB7, 0x6A6680))
        ));

        this.values.put("123scyther", new CardIconEntry("cobblemon.species.scyther.name", Map.of(
                "*", new Entry(id("card/icon/123scyther"), 0xA2CF4F, 0xD5EEA8),
                CardRarity.SHINY.name(), new Entry(id("card/icon/123scythershiny"), 0xA3C04C, 0xD4EEA8))
        ));

        this.values.put("124jynx", new CardIconEntry("cobblemon.species.jynx.name", Map.of(
                "*", new Entry(id("card/icon/124jynx"), 0xF9E384, 0xCC4749),
                CardRarity.SHINY.name(), new Entry(id("card/icon/124jynxshiny"), 0xC65880, 0xECD6BD))
        ));

        this.values.put("125electabuzz", new CardIconEntry("cobblemon.species.electabuzz.name", Map.of(
                "*", new Entry(id("card/icon/125electabuzz"), 0xFFDC5F, 0x775635),
                CardRarity.SHINY.name(), new Entry(id("card/icon/125electabuzzshiny"), 0xF78A67, 0x72393C))
        ));

        this.values.put("126magmar", new CardIconEntry("cobblemon.species.magmar.name", Map.of(
                "*", new Entry(id("card/icon/126magmar"), 0xF5CE65, 0xC03D1F),
                CardRarity.SHINY.name(), new Entry(id("card/icon/126magmarshiny"), 0xAC4469, 0xEBABC0))
        ));

        this.values.put("127pinsir", new CardIconEntry("cobblemon.species.pinsir.name", Map.of(
                "*", new Entry(id("card/icon/127pinsir"), 0xEFE7D9, 0xA58F6E),
                CardRarity.SHINY.name(), new Entry(id("card/icon/127pinsirshiny"), 0xD9D5F3, 0x6D65AD))
        ));

        this.values.put("128tauros", new CardIconEntry("cobblemon.species.tauros.name", Map.of(
                "*", new Entry(id("card/icon/128tauros"), 0xBB9966, 0x453D3D),
                CardRarity.SHINY.name(), new Entry(id("card/icon/128taurosshiny"), 0xC4B85B, 0x555055))
        ));

        this.values.put("129magikarp", new CardIconEntry("cobblemon.species.magikarp.name", Map.of(
                "*", new Entry(id("card/icon/129magikarp"), 0xF58C51, 0xF6E093),
                CardRarity.SHINY.name(), new Entry(id("card/icon/129magikarpshiny"), 0xEBC651, 0xFFEE99))
        ));

        this.values.put("130gyarados", new CardIconEntry("cobblemon.species.gyarados.name", Map.of(
                "*", new Entry(id("card/icon/130gyarados"), 0xC5E1E0, 0x6D99A9),
                CardRarity.SHINY.name(), new Entry(id("card/icon/130gyaradosshiny"), 0xEDE5CC, 0xDB4650))
        ));

        this.values.put("131lapras", new CardIconEntry("cobblemon.species.lapras.name", Map.of(
                "*", new Entry(id("card/icon/131lapras"), 0xA3D9E1, 0x6493B8),
                CardRarity.SHINY.name(), new Entry(id("card/icon/131laprasshiny"), 0x8763C0, 0xD2C6C7))
        ));

        this.values.put("132ditto", new CardIconEntry("cobblemon.species.ditto.name", Map.of(
                "*", new Entry(id("card/icon/132ditto"), 0xF2A2CC, 0xC6698F),
                CardRarity.SHINY.name(), new Entry(id("card/icon/132dittoshiny"), 0x9BE4F9, 0x5FAACF))
        ));

        this.values.put("133eevee", new CardIconEntry("cobblemon.species.eevee.name", Map.of(
                "*", new Entry(id("card/icon/133eevee"), 0xBF9D79, 0x8E6041),
                CardRarity.SHINY.name(), new Entry(id("card/icon/133eeveeshiny"), 0xE6E4DF, 0xB9B2AC))
        ));

        this.values.put("134vaporeon", new CardIconEntry("cobblemon.species.vaporeon.name", Map.of(
                "*", new Entry(id("card/icon/134vaporeon"), 0x98F1F1, 0x5D739D),
                CardRarity.SHINY.name(), new Entry(id("card/icon/134vaporeonshiny"), 0xD3BCE1, 0x985290))
        ));

        this.values.put("135jolteon", new CardIconEntry("cobblemon.species.jolteon.name", Map.of(
                "*", new Entry(id("card/icon/135jolteon"), 0xF9E394, 0xBD8437),
                CardRarity.SHINY.name(), new Entry(id("card/icon/135jolteonshiny"), 0xCCF87E, 0x8F8A7D))
        ));

        this.values.put("136flareon", new CardIconEntry("cobblemon.species.flareon.name", Map.of(
                "*", new Entry(id("card/icon/136flareon"), 0xEFCF7A, 0xB85414),
                CardRarity.SHINY.name(), new Entry(id("card/icon/136flareonshiny"), 0xDFE582, 0xAC861F))
        ));

        this.values.put("137porygon", new CardIconEntry("cobblemon.species.porygon.name", Map.of(
                "*", new Entry(id("card/icon/137porygon"), 0x71EDE2, 0xAF6C75),
                CardRarity.SHINY.name(), new Entry(id("card/icon/137porygonshiny"), 0xDB84DA, 0x6650D1))
        ));

        this.values.put("138omanyte", new CardIconEntry("cobblemon.species.omanyte.name", Map.of(
                "*", new Entry(id("card/icon/138omanyte"), 0xBFF2EF, 0xA5BA98),
                CardRarity.SHINY.name(), new Entry(id("card/icon/138omanyteshiny"), 0xE2D9AD, 0xD174E6))
        ));

        this.values.put("139omastar", new CardIconEntry("cobblemon.species.omastar.name", Map.of(
                "*", new Entry(id("card/icon/139omastar"), 0xB8EEDE, 0x95B191),
                CardRarity.SHINY.name(), new Entry(id("card/icon/139omastarshiny"), 0xB85ECE, 0xE7C7AE))
        ));

        this.values.put("140kabuto", new CardIconEntry("cobblemon.species.kabuto.name", Map.of(
                "*", new Entry(id("card/icon/140kabuto"), 0xCE823F, 0x3D1B26),
                CardRarity.SHINY.name(), new Entry(id("card/icon/140kabutoshiny"), 0x9AC941, 0x462326))
        ));

        this.values.put("141kabutops", new CardIconEntry("cobblemon.species.kabutops.name", Map.of(
                "*", new Entry(id("card/icon/141kabutops"), 0x955327, 0xECD8CA),
                CardRarity.SHINY.name(), new Entry(id("card/icon/141kabutopsshiny"), 0xCFE0AB, 0x617835))
        ));

        this.values.put("142aerodactyl", new CardIconEntry("cobblemon.species.aerodactyl.name", Map.of(
                "*", new Entry(id("card/icon/142aerodactyl"), 0xAAACCD, 0x7B69A0),
                CardRarity.SHINY.name(), new Entry(id("card/icon/142aerodactylshiny"), 0xC1A7CD, 0x7076A6))
        ));

        this.values.put("143snorlax", new CardIconEntry("cobblemon.species.snorlax.name", Map.of(
                "*", new Entry(id("card/icon/143snorlax"), 0xF5ECC1, 0x2E76A4),
                CardRarity.SHINY.name(), new Entry(id("card/icon/143snorlaxshiny"), 0xF5ECC1, 0x2B59A6))
        ));

        this.values.put("144articuno", new CardIconEntry("cobblemon.species.articuno.name", Map.of(
                "*", new Entry(id("card/icon/144articuno"), 0x9FDAF8, 0x2B94CB),
                CardRarity.SHINY.name(), new Entry(id("card/icon/144articunoshiny"), 0xC6E4F3, 0x5299BF))
        ));

        this.values.put("145zapdos", new CardIconEntry("cobblemon.species.zapdos.name", Map.of(
                "*", new Entry(id("card/icon/145zapdos"), 0xFFDD66, 0xB57E24),
                CardRarity.SHINY.name(), new Entry(id("card/icon/145zapdosshiny"), 0xFCD342, 0xB3740F))
        ));

        this.values.put("146moltres", new CardIconEntry("cobblemon.species.moltres.name", Map.of(
                "*", new Entry(id("card/icon/146moltres"), 0xFCC03F, 0xF28539),
                CardRarity.SHINY.name(), new Entry(id("card/icon/146moltresshiny"), 0xFCA045, 0xD66D79))
        ));

        this.values.put("147dratini", new CardIconEntry("cobblemon.species.dratini.name", Map.of(
                "*", new Entry(id("card/icon/147dratini"), 0xF4F4F4, 0x8787E5),
                CardRarity.SHINY.name(), new Entry(id("card/icon/147dratinishiny"), 0xF4F4F4, 0xE387E4))
        ));

        this.values.put("148dragonair", new CardIconEntry("cobblemon.species.dragonair.name", Map.of(
                "*", new Entry(id("card/icon/148dragonair"), 0x58AFED, 0xDDEEF7),
                CardRarity.SHINY.name(), new Entry(id("card/icon/148dragonairshiny"), 0xEC80C4, 0xF9E7F4))
        ));

        this.values.put("149dragonite", new CardIconEntry("cobblemon.species.dragonite.name", Map.of(
                "*", new Entry(id("card/icon/149dragonite"), 0xD19A38, 0xEACF7F),
                CardRarity.SHINY.name(), new Entry(id("card/icon/149dragoniteshiny"), 0x85946E, 0xB0DC9F))
        ));

        this.values.put("150mewtwo", new CardIconEntry("cobblemon.species.mewtwo.name", Map.of(
                "*", new Entry(id("card/icon/150mewtwo"), 0xCCC0E6, 0xA5689B),
                CardRarity.SHINY.name(), new Entry(id("card/icon/150mewtwoshiny"), 0xF3F2E2, 0x87BF78))
        ));

        this.values.put("151mew", new CardIconEntry("cobblemon.species.mew.name", Map.of(
                "*", new Entry(id("card/icon/151mew"), 0xFFBFB8, 0xBB8E91),
                CardRarity.SHINY.name(), new Entry(id("card/icon/151mewshiny"), 0xB8E6FF, 0x6DB8DF))
        ));
    }

}
