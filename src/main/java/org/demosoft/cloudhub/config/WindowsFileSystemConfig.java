package org.demosoft.cloudhub.config;

import org.demosoft.cloudhub.infra.Windows;
import org.springframework.stereotype.Component;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
@Windows(true)
@Component
public class WindowsFileSystemConfig implements FileSystemConfig {
    @Override
    public String getRepositoryBasePath() {
        return "F:\\\\demosoft\\\\cloudDrivesHub\\\\space";
    }
}
