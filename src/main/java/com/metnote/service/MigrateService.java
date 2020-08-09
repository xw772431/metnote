package com.metnote.service;

import com.metnote.model.enums.MigrateType;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * Migrate service interface.
 *
 * @author ryanwang
 * @date 2019-10-29
 */
public interface MigrateService {

    /**
     * Migrate.
     *
     * @param file        multipart file must not be null
     * @param migrateType migrate type
     */
    void migrate(@NonNull MultipartFile file, @NonNull MigrateType migrateType);
}
