package com.metnote.config.properties;

import com.metnote.model.enums.Mode;
import com.metnote.model.support.MetnoteConst;
import com.metnote.utils.MetnoteUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;


/**
 * Metnote configuration properties.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-15
 */
@Data
@ConfigurationProperties("metnote")
public class MetnoteProperties {

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
     * Metnote startup mode.
     */
    private Mode mode = Mode.PRODUCTION;

    /**
     * Admin path.
     */
    private String adminPath = "admin";

    /**
     * Work directory.
     */
    private String workDir = MetnoteConst.DATA_BASE;

    /**
     * Metnote backup directory.(Not recommended to modify this config);
     */
    private String backupDir = MetnoteUtils.ensureSuffix(MetnoteConst.TEMP_DIR, MetnoteConst.FILE_SEPARATOR) + "backup" + MetnoteConst.FILE_SEPARATOR;

    /**
     * Metnote data export directory.
     */
    private String dataExportDir = MetnoteUtils.ensureSuffix(MetnoteConst.TEMP_DIR, MetnoteConst.FILE_SEPARATOR) + "data-export" + MetnoteConst.FILE_SEPARATOR;

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
