package org.revo.Service;

import lombok.extern.slf4j.Slf4j;
import org.revo.Config.Processor;
import org.revo.Domain.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.messaging.Message;

import java.io.IOException;

/**
 * Created by ashraf on 23/04/17.
 */
@MessageEndpoint
@Slf4j
public class Receiver {
    @Autowired
    private TorrentService torrentService;
    @Autowired
    private TempFileService tempFileService;

    @StreamListener(value = Processor.torrent_queue)
    public void queue(Message<File> file) throws IOException {
        tempFileService.clear("torrent");
        log.info("receive torrent_queue " + file.getPayload().getId());
        torrentService.process(file.getPayload());
    }
}