package org.revo.Util;

import bt.data.Storage;
import bt.data.StorageUnit;
import bt.metainfo.Torrent;
import bt.metainfo.TorrentFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
@Slf4j
public class FileSystemStorage implements Storage {

    private final Path rootDirectory;
    private final PathNormalizer pathNormalizer;

    /**
     * Create a file-system based storage inside a given directory.
     *
     * @param rootDirectory Root directory for this storage. All torrent files will be stored inside this directory.
     * @since 1.0
     * @deprecated since 1.3 in favor of more generic {@link #FileSystemStorage(Path)}
     */
    @Deprecated
    public FileSystemStorage(File rootDirectory) {
        this(rootDirectory.toPath());
    }

    public FileSystemStorage(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.pathNormalizer = new PathNormalizer(rootDirectory.getFileSystem());
    }

    @Override
    public StorageUnit getUnit(Torrent torrent, TorrentFile torrentFile) {
log.info("getUnit "+torrentFile);
        Path torrentDirectory;
        if (torrent.getFiles().size() == 1) {
            torrentDirectory = rootDirectory;
        } else {
            String normalizedName = pathNormalizer.normalize(torrent.getName());
            torrentDirectory = rootDirectory.resolve(normalizedName);
        }
        String normalizedPath = pathNormalizer.normalize(torrentFile.getPathElements());
        log.info("getPathElements "+torrentFile.getPathElements());
        log.info("normalized "+normalizedPath);
        return new FileSystemStorageUnit(torrentDirectory, normalizedPath, torrentFile.getSize());
    }
}
