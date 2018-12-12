package org.revo.Domain;

import bt.BtClientBuilder;
import bt.data.file.FileSystemStorage;
import bt.runtime.BtClient;
import bt.runtime.BtRuntime;
import bt.torrent.TorrentSessionState;
import lombok.Getter;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Torrent {
    private File file;
    private final BtClient btClient;
    private final BtRuntime runtime;
    @Getter
    private long start;
    @Getter
    private long size;
    @Getter
    private long downloaded;
    @Getter
    private String name;
    @Getter
    private int peers;

    public Torrent(BtRuntime runtime, BtClientBuilder clientBuilder, Path path, File file) {
        this.file = file;
        this.runtime = runtime;
        this.btClient = clientBuilder.storage(new FileSystemStorage(path)).magnet(file.getUrl())
                .afterTorrentFetched(it -> {
                    size = it.getSize();
                    name = it.getName();
                })
                .build();
    }

    public CompletableFuture<?> start(Consumer<TorrentSessionState> consumer) {
        start = System.currentTimeMillis();
        return this.btClient.startAsync(state -> {
            downloaded = state.getDownloaded();
            peers = state.getConnectedPeers().size();
            determiner();
            consumer.accept(state);
        }, 60000);
    }

    private void determiner() {
        if (false) {
            btClient.stop();
            runtime.shutdown();
        }
    }
}
