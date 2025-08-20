package abeshutt.staracademy.config;

import com.google.gson.annotations.Expose;

public class NPCConfig extends FileConfig {

    @Expose private String partnerNpcName;
    @Expose private String cardGraderNpcName;

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

    @Override
    protected void reset() {
        this.partnerNpcName = "Professor";
        this.cardGraderNpcName = "Hatsune Miku";
    }

}
