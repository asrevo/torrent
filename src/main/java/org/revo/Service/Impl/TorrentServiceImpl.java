package org.revo.Service.Impl;

import bt.BtClientBuilder;
import bt.runtime.BtRuntime;
import bt.torrent.TorrentSessionState;
import lombok.extern.slf4j.Slf4j;
import org.revo.Domain.File;
import org.revo.Domain.Torrent;
import org.revo.Service.TempFileService;
import org.revo.Service.TorrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
        torrent.start(it -> peak(torrent.getStart(), it)).join();
    }


    private static void peak(long start, TorrentSessionState state) {
        log.info("info " + ((System.currentTimeMillis() - start) / 1000) + "'s  getDownloaded " + state.getDownloaded() + " from " + state.getConnectedPeers().size());
    }

}
