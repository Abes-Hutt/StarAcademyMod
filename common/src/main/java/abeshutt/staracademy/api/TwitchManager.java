package abeshutt.staracademy.api;

import abeshutt.staracademy.api.twitch.TwitchStream;
import abeshutt.staracademy.util.threading.ThreadPool;

import java.util.ArrayList;
import java.util.List;

public class TwitchManager {

    private static final ThreadPool IMAGE_DOWNLOADER = new ThreadPool(Runtime.getRuntime().availableProcessors());

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
        if (this.dirty) {
            for (TwitchStream stream : this.streams) {
                IMAGE_DOWNLOADER.execute(() -> {
                    stream.getProfilePicture().fetch();
                });
            }

            this.dirty = false;
        }
    }

}
