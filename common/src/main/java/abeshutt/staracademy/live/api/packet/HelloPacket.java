package abeshutt.staracademy.live.api.packet;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import abeshutt.staracademy.live.api.dto.agent.Agent;
import abeshutt.staracademy.live.api.registry.Registries;
import com.google.gson.JsonObject;

public class HelloPacket extends Packet {

    private int protocol;
    private Agent agent;

    public HelloPacket() {

    }

    public HelloPacket(int protocol, Agent agent) {
        this.protocol = protocol;
        this.agent = agent;
    }

    public int getProtocol() {
        return this.protocol;
    }

    public Agent getAgent() {
        return this.agent;
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        json.add("protocol", adapter.writeInt(this.protocol));
        json.add("agent", adapter.writeTypeObject(Registries.AGENT, this.agent));
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        this.protocol = adapter.readInt(json.get("protocol")).orElse(0);
        this.agent = adapter.readTypeObject(Registries.AGENT, json.get("agent")).orElse(null);
    }

}
