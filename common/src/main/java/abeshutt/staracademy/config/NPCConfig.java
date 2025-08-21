package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;

public class NPCConfig extends FileConfig {

    @Expose private String partnerNpcName;
    @Expose private String cardGraderNpcName;
    @Expose private int gradingCurrencyCost;
    @Expose private int gradingTimeMillis;

    @Override
    public String getPath() {
        return "npc";
    }

    public String getPartnerNPCName() {
        return this.partnerNpcName;
    }

    public String getCardGraderNpcName() {
        return this.cardGraderNpcName;
    }

    public int getGradingCurrencyCost() {
        return this.gradingCurrencyCost;
    }

    public int getGradingTimeMillis() {
        return this.gradingTimeMillis;
    }

    @Override
    protected void reset() {
        this.partnerNpcName = "Professor";
        this.cardGraderNpcName = "Hatsune Miku";
        this.gradingCurrencyCost = 100;
        this.gradingTimeMillis = 1000 * 5;
    }

}
