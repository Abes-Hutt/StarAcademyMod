package abeshutt.staracademy.api;

import abeshutt.staracademy.api.twitch.TwitchStream;
import abeshutt.staracademy.util.threading.ThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TwitchManager {

    private final List<TwitchStream> streams;
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

        for (TwitchStream stream : this.streams) {
            CompletableFuture.supplyAsync(() -> {
                stream.getProfilePicture().fetch();
                return null;
            });
        }

        this.iteration++;
    }

}
