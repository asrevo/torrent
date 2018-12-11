package org.revo.Service.Impl;

import bt.BtClientBuilder;
import bt.data.file.FileSystemStorage;
import bt.runtime.BtClient;
import bt.runtime.BtRuntime;
import bt.torrent.TorrentSessionState;
import lombok.extern.slf4j.Slf4j;
import org.revo.Domain.File;
import org.revo.Service.TorrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Service
@Slf4j
public class TorrentServiceImpl implements TorrentService {
    @Autowired
    private BtClientBuilder clientBuilder;
    @Autowired
    private BtRuntime runtime;

    @Override
    public void process(File file) throws IOException {

        BtClient client = clientBuilder
                .storage(new FileSystemStorage(Files.createTempDirectory("temp").resolve(UUID.randomUUID().toString().replace("-", ""))))
                .magnet(file.getUrl())
                .afterTorrentFetched(torrent -> {
                    log.info("size of " + torrent.getSize());
                })
                .build();

        log.info("will start");
        long start = System.currentTimeMillis();
        client.startAsync(it -> {
            peak(start, it);
            if (it.getPiecesRemaining() == 0) {
                client.stop();
                runtime.shutdown();
            }
        }, 1000).join();


    }


    private static void peak(long start, TorrentSessionState state) {
        log.info("info " + ((System.currentTimeMillis() - start) / 1000) + "'s  getDownloaded " + state.getDownloaded() + " from " + state.getConnectedPeers().size());
    }

}
