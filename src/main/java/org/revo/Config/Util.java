package org.revo.Config;

import bt.Bt;
import bt.BtClientBuilder;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.runtime.BtRuntime;
import bt.runtime.Config;
import bt.torrent.selector.SequentialSelector;
import org.bson.types.ObjectId;
import org.revo.Domain.File;
import org.revo.Service.TorrentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Util {
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
}
