package org.revo.Service.Impl;

import bt.BtClientBuilder;
import bt.data.file.FileSystemStorage;
import bt.runtime.BtClient;
import bt.runtime.BtRuntime;
import bt.torrent.TorrentSessionState;
import lombok.extern.slf4j.Slf4j;
import org.revo.Domain.File;
import org.revo.Service.TempFileService;
import org.revo.Service.TorrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class TorrentServiceImpl implements TorrentService {
    @Autowired
    private BtClientBuilder clientBuilder;
    @Autowired
    private BtRuntime runtime;
    @Autowired
    private TempFileService tempFileService;

    @Override
    public void process(File file) throws IOException {
        Path torrent = tempFileService.tempDir("torrent");
        BtClient client = clientBuilder.storage(new FileSystemStorage(torrent)).magnet(file.getUrl()).build();
        log.info("will start");
        long start = System.currentTimeMillis();
        client.startAsync(it -> {
            peak(start, it);
            if (it.getPiecesRemaining() == 0) {
                log.info("will end");
                client.stop();
                runtime.shutdown();
                try {
                    Files.walk(torrent).forEach(itw -> {
                        log.info("found " + itw);
                    });
                } catch (IOException e) {

                }


            }
        }, 1000).join();
        log.info("done");


    }


    private static void peak(long start, TorrentSessionState state) {
        log.info("info " + ((System.currentTimeMillis() - start) / 1000) + "'s  getDownloaded " + state.getDownloaded() + " from " + state.getConnectedPeers().size());
    }

}
