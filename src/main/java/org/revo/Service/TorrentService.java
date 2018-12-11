package org.revo.Service;

import org.revo.Domain.File;

import java.io.IOException;

public interface TorrentService {
    void process(File file) throws IOException;
}
