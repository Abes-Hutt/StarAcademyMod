package abeshutt.staracademy.api;

import abeshutt.staracademy.api.twitch.TwitchStream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TwitchManager {

    private final List<TwitchStream> streams;
    private boolean dirty;
    private int iteration;

    public TwitchManager() {
        this.streams = new ArrayList<>();
    }

    public List<TwitchStream> getStreams() {
        return this.streams;
    }

    public int getIteration() {
        return this.iteration;
    }

    public void update(List<TwitchStream> streams) {
        this.streams.clear();
        this.streams.addAll(streams);
        this.iteration++;
        this.dirty = true;
    }

    public void tick(AcademyClient client) {
        for (TwitchStream stream : this.streams) {
            CompletableFuture.supplyAsync(() -> {
                stream.getProfilePicture().fetch();
                return null;
            }).join();
        }

        this.dirty = false;
    }

}
