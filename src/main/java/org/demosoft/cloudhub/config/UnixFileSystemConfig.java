package org.demosoft.cloudhub.config;

import org.demosoft.cloudhub.infra.Windows;
import org.springframework.stereotype.Component;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
@Windows(false)
@Component
public class UnixFileSystemConfig implements FileSystemConfig {
    @Override
    public String getRepositoryBasePath() {
        return "/Users/andrii_korkoshko/development/space";
    }
}
