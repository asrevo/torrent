package org.revo.Config;

import bt.Bt;
import bt.BtClientBuilder;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.runtime.BtClient;
import bt.runtime.BtRuntime;
import bt.runtime.Config;
import bt.torrent.TorrentSessionState;
import bt.torrent.selector.SequentialSelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Configuration
@Slf4j
public class Util {

    private String magnetUri = "magnet:?xt=urn:btih:3E9917F29215B809B1A7B06063EFBC227CAC2B76";

    @Bean
    public Config config() {
        return new Config() {
            @Override
            public int getNumOfHashingThreads() {
                return Runtime.getRuntime().availableProcessors();
            }
        };

    }

    @Bean
    public BtRuntime btRuntime(Config config, DHTModule dhtModule) {
        return BtRuntime.builder(config)
                .module(dhtModule)
                .autoLoadModules()
                .disableAutomaticShutdown()
                .build();
    }

    @Bean
    public BtClientBuilder clientBuilder(BtRuntime runtime) {
        return Bt.client(runtime)
                .selector(SequentialSelector.sequential());
    }

    @Bean
    public DHTModule dhtModule() {
        return new DHTModule(new DHTConfig() {
            @Override
            public boolean shouldUseRouterBootstrap() {
                return true;
            }
        });
    }

    @Bean
    public Path path() {
        try {
            return Files.createTempDirectory("temp");
        } catch (IOException e) {
            return null;
        }
    }

    @Bean
    public CommandLineRunner runner(BtClientBuilder clientBuilder, BtRuntime runtime, Path path) {
        return args -> {
            BtClient client = clientBuilder
                    .storage(new FileSystemStorage(path.resolve(UUID.randomUUID().toString().replace("-", ""))))

                    .magnet(magnetUri)
                    .afterTorrentFetched(torrent -> {
                        log.info("size of " + torrent.getSize());
                    })
                    .build();

            log.info("will start");
            long start = System.currentTimeMillis();
/*
            client.startAsync(it -> {
                peak(start, it);
                if (it.getPiecesRemaining() == 0) {
                    client.stop();
                    runtime.shutdown();
                }
            }, 1000).join();
*/


            client.startAsync();
            log.info("end");

        };
    }

    private static void peak(long start, TorrentSessionState state) {
        log.info("info " + ((System.currentTimeMillis() - start) / 1000) + "'s  getDownloaded " + state.getDownloaded() + " from " + state.getConnectedPeers().size());
    }
}
