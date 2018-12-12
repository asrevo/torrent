package org.revo.Service.Impl;

import bt.BtClientBuilder;
import bt.runtime.BtRuntime;
import lombok.extern.slf4j.Slf4j;
import org.revo.Domain.File;
import org.revo.Domain.Torrent;
import org.revo.Service.TempFileService;
import org.revo.Service.TorrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

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
        Torrent torrent = new Torrent(runtime, clientBuilder, tempFileService.tempDir("torrent"), file);
        torrent.setOnComplete(path -> {
            try {
                log.info("walk");
                Files.walk(path).forEach(it -> {
                    log.info(it.toString());
                });
            } catch (IOException e) {

            }
        });
        torrent.start(it -> peak(torrent)).join();
    }


    private static void peak(Torrent torrent) {
        log.info("info about " + torrent.getName() + "  " + ((System.currentTimeMillis() - torrent.getStart()) / 1000) + "'s  getDownloaded " + torrent.getDownloaded() + " from " + torrent.getPeers());
    }

}
