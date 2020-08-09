package com.metnote.config.properties;

import com.metnote.model.enums.Mode;
import com.metnote.model.support.HaloConst;
import com.metnote.utils.HaloUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;


/**
 * Halo configuration properties.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-15
 */
@Data
@ConfigurationProperties("halo")
public class HaloProperties {

    /**
     * Doc api disabled. (Default is true)
     */
    private boolean docDisabled = true;

    /**
     * Production env. (Default is true)
     */
    private boolean productionEnv = true;

    /**
     * Authentication enabled
     */
    private boolean authEnabled = true;

    /**
     * Halo startup mode.
     */
    private Mode mode = Mode.PRODUCTION;

    /**
     * Admin path.
     */
    private String adminPath = "admin";

    /**
     * Work directory.
     */
    private String workDir = HaloUtils.ensureSuffix(HaloConst.USER_HOME, HaloConst.FILE_SEPARATOR) + ".halo" + HaloConst.FILE_SEPARATOR;

    /**
     * Halo backup directory.(Not recommended to modify this config);
     */
    private String backupDir = HaloUtils.ensureSuffix(HaloConst.TEMP_DIR, HaloConst.FILE_SEPARATOR) + "halo-backup" + HaloConst.FILE_SEPARATOR;

    /**
     * Halo data export directory.
     */
    private String dataExportDir = HaloUtils.ensureSuffix(HaloConst.TEMP_DIR, HaloConst.FILE_SEPARATOR) + "halo-data-export" + HaloConst.FILE_SEPARATOR;

    /**
     * Upload prefix.
     */
    private String uploadUrlPrefix = "upload";

    /**
     * Download Timeout.
     */
    private Duration downloadTimeout = Duration.ofSeconds(30);

    /**
     * cache store impl
     * memory
     * level
     */
    private String cache = "memory";

    private ArrayList<String> cacheRedisNodes = new ArrayList<>();

    private String cacheRedisPassword = "";


}
