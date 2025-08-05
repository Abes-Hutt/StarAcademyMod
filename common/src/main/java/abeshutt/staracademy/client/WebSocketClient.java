package abeshutt.staracademy.client;

import abeshutt.staracademy.StarAcademyMod;
import okhttp3.*;
import okio.ByteString;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WebSocketClient extends WebSocketListener {

    private final String url;
    private boolean connected;
    private final Consumer<String> listener;

    private WebSocket socket;

    public WebSocketClient(String url, Consumer<String> listener) {
        this.url = url;
        this.connected = false;
        this.listener = listener;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void close() {
        if(this.connected) {
            this.socket.close(1000, null);
            this.connected = false;
        }

        this.socket = null;
    }

    public void connect() {
        this.close();
        OkHttpClient client = new OkHttpClient.Builder()
                .pingInterval(20, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(this.url).build();
        this.socket = client.newWebSocket(request, this);
        this.connected = true;
    }

    public void send(String message) {
        if(this.socket != null) {
            this.socket.send(message);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        this.listener.accept(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        // Ignore
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {

    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        StarAcademyMod.LOGGER.warn("Socket connection closed: [{}] {}", code, reason);
        this.close();
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable error, Response response) {
        StarAcademyMod.LOGGER.error("Failed to read from or write to the web socket.", error);
        this.close();
    }
    
}
