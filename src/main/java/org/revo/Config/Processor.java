package org.revo.Config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface Processor {
    String torrent_queue = "torrent_queue";

    @Input("torrent_queue")
    SubscribableChannel torrent_queue();
}
