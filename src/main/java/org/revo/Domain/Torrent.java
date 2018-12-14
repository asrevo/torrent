package org.revo.Domain;

import bt.BtClientBuilder;
import bt.data.file.FileSystemStorage;
import bt.runtime.BtClient;
import bt.runtime.BtRuntime;
import bt.torrent.TorrentSessionState;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Torrent {
    private final BtClient btClient;
    private final BtRuntime runtime;
    private Path path;
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
    @Setter
    private Consumer<List<Path>> onComplete;
    private boolean fireOnComplete = false;

    public Torrent(BtRuntime runtime, BtClientBuilder clientBuilder, Path path, File file) {
        this.runtime = runtime;
        this.path = path;
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
            determiner(state);
            consumer.accept(state);
        }, 10000);
    }

    private void determiner(TorrentSessionState state) {
        if (state.getPiecesRemaining() == 0) {
            btClient.stop();
            runtime.shutdown();
            if (this.onComplete != null && !this.fireOnComplete) {
                this.fireOnComplete = true;
                try {
                    this.onComplete.accept(Files.walk(path).filter(Files::isRegularFile).collect(Collectors.toList()));
                } catch (IOException e) {
                }
            }
        }
    }
}
