package com.metnote.controller.admin.api;

import com.metnote.annotation.DisableOnCondition;
import com.metnote.config.properties.MetnoteProperties;
import com.metnote.model.dto.BackupDTO;
import com.metnote.model.dto.post.BasePostDetailDTO;
import com.metnote.service.BackupService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Backup controller
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-26
 */
@RestController
@RequestMapping("/api/admin/backups")
@Slf4j
public class BackupController {

    private final BackupService backupService;

    private final MetnoteProperties metnoteProperties;

    public BackupController(BackupService backupService,
                            MetnoteProperties metnoteProperties) {
        this.backupService = backupService;
        this.metnoteProperties = metnoteProperties;
    }

    @PostMapping("work-dir")
    @ApiOperation("Backups work directory")
    @DisableOnCondition
    public BackupDTO backupHalo() {
        return backupService.backupWorkDirectory();
    }

    @GetMapping("work-dir")
    @ApiOperation("Gets all work directory backups")
    public List<BackupDTO> listBackups() {
        return backupService.listWorkDirBackups();
    }

    @GetMapping("work-dir/{fileName:.+}")
    @ApiOperation("Downloads a work directory backup file")
    @DisableOnCondition
    public ResponseEntity<Resource> downloadBackup(@PathVariable("fileName") String fileName, HttpServletRequest request) {
        log.info("Try to download backup file: [{}]", fileName);

        // Load file as resource
        Resource backupResource = backupService.loadFileAsResource(metnoteProperties.getBackupDir(), fileName);

        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        // Try to determine file's content type
        try {
            contentType = request.getServletContext().getMimeType(backupResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            log.warn("Could not determine file type", e);
            // Ignore this error
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + backupResource.getFilename() + "\"")
                .body(backupResource);
    }

    @DeleteMapping("work-dir")
    @ApiOperation("Deletes a work directory backup")
    @DisableOnCondition
    public void deleteBackup(@RequestParam("filename") String filename) {
        backupService.deleteWorkDirBackup(filename);
    }

    @PostMapping("markdown")
    @ApiOperation("Imports markdown")
    public BasePostDetailDTO backupMarkdowns(@RequestPart("file") MultipartFile file) throws IOException {
        return backupService.importMarkdown(file);
    }

    @PostMapping("data")
    @ApiOperation("Exports all data")
    @DisableOnCondition
    public BackupDTO exportData() {
        return backupService.exportData();
    }

    @GetMapping("data")
    @ApiOperation("Lists all exported data")
    public List<BackupDTO> listExportedData() {
        return backupService.listExportedData();
    }

    @DeleteMapping("data")
    @ApiOperation("Deletes a exported data")
    @DisableOnCondition
    public void deleteExportedData(@RequestParam("filename") String filename) {
        backupService.deleteExportedData(filename);
    }

    @GetMapping("data/{fileName:.+}")
    @ApiOperation("Downloads a exported data")
    @DisableOnCondition
    public ResponseEntity<Resource> downloadExportedData(@PathVariable("fileName") String fileName, HttpServletRequest request) {
        log.info("Try to download exported data file: [{}]", fileName);

        // Load exported data as resource
        Resource exportDataResource = backupService.loadFileAsResource(metnoteProperties.getDataExportDir(), fileName);

        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        // Try to determine file's content type
        try {
            contentType = request.getServletContext().getMimeType(exportDataResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            log.warn("Could not determine file type", e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + exportDataResource.getFilename() + "\"")
                .body(exportDataResource);
    }
}
