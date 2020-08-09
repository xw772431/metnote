package com.metnote.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.metnote.config.properties.MetnoteProperties;
import com.metnote.event.options.OptionUpdatedEvent;
import com.metnote.event.theme.ThemeUpdatedEvent;
import com.metnote.exception.NotFoundException;
import com.metnote.exception.ServiceException;
import com.metnote.model.dto.BackupDTO;
import com.metnote.model.dto.post.BasePostDetailDTO;
import com.metnote.model.entity.Attachment;
import com.metnote.model.entity.Category;
import com.metnote.model.entity.CommentBlackList;
import com.metnote.model.entity.Journal;
import com.metnote.model.entity.JournalComment;
import com.metnote.model.entity.Link;
import com.metnote.model.entity.Log;
import com.metnote.model.entity.Menu;
import com.metnote.model.entity.Option;
import com.metnote.model.entity.Photo;
import com.metnote.model.entity.Post;
import com.metnote.model.entity.PostCategory;
import com.metnote.model.entity.PostComment;
import com.metnote.model.entity.PostMeta;
import com.metnote.model.entity.PostTag;
import com.metnote.model.entity.Sheet;
import com.metnote.model.entity.SheetComment;
import com.metnote.model.entity.SheetMeta;
import com.metnote.model.entity.Tag;
import com.metnote.model.entity.ThemeSetting;
import com.metnote.model.support.MetnoteConst;
import com.metnote.security.service.OneTimeTokenService;
import com.metnote.service.AttachmentService;
import com.metnote.service.BackupService;
import com.metnote.service.CategoryService;
import com.metnote.service.CommentBlackListService;
import com.metnote.service.JournalCommentService;
import com.metnote.service.JournalService;
import com.metnote.service.LinkService;
import com.metnote.service.LogService;
import com.metnote.service.MenuService;
import com.metnote.service.OptionService;
import com.metnote.service.PhotoService;
import com.metnote.service.PostCategoryService;
import com.metnote.service.PostCommentService;
import com.metnote.service.PostMetaService;
import com.metnote.service.PostService;
import com.metnote.service.PostTagService;
import com.metnote.service.SheetCommentService;
import com.metnote.service.SheetMetaService;
import com.metnote.service.SheetService;
import com.metnote.service.TagService;
import com.metnote.service.ThemeSettingService;
import com.metnote.service.UserService;
import com.metnote.utils.DateTimeUtils;
import com.metnote.utils.FileUtils;
import com.metnote.utils.JsonUtils;
import com.metnote.utils.MetnoteUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Backup service implementation.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-26
 */
@Service
@Slf4j
public class BackupServiceImpl implements BackupService {

    private static final String BACKUP_RESOURCE_BASE_URI = "/api/admin/backups/work-dir";

    private static final String DATA_EXPORT_BASE_URI = "/api/admin/backups/data";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final Type MAP_TYPE = new TypeToken<Map<String, ?>>() {
    }.getType();

    private static final Type JSON_OBJECT_TYPE = new TypeToken<List<JsonObject>>() {
    }.getType();

    private final AttachmentService attachmentService;

    private final CategoryService categoryService;

    private final CommentBlackListService commentBlackListService;

    private final JournalService journalService;

    private final JournalCommentService journalCommentService;

    private final LinkService linkService;

    private final LogService logService;

    private final MenuService menuService;

    private final OptionService optionService;

    private final PhotoService photoService;

    private final PostService postService;

    private final PostCategoryService postCategoryService;

    private final PostCommentService postCommentService;

    private final PostMetaService postMetaService;

    private final PostTagService postTagService;

    private final SheetService sheetService;

    private final SheetCommentService sheetCommentService;

    private final SheetMetaService sheetMetaService;

    private final TagService tagService;

    private final ThemeSettingService themeSettingService;

    private final UserService userService;

    private final OneTimeTokenService oneTimeTokenService;

    private final MetnoteProperties metnoteProperties;

    private final ApplicationEventPublisher eventPublisher;

    public BackupServiceImpl(AttachmentService attachmentService, CategoryService categoryService, CommentBlackListService commentBlackListService, JournalService journalService, JournalCommentService journalCommentService, LinkService linkService, LogService logService, MenuService menuService, OptionService optionService, PhotoService photoService, PostService postService, PostCategoryService postCategoryService, PostCommentService postCommentService, PostMetaService postMetaService, PostTagService postTagService, SheetService sheetService, SheetCommentService sheetCommentService, SheetMetaService sheetMetaService, TagService tagService, ThemeSettingService themeSettingService, UserService userService, OneTimeTokenService oneTimeTokenService, MetnoteProperties metnoteProperties, ApplicationEventPublisher eventPublisher) {
        this.attachmentService = attachmentService;
        this.categoryService = categoryService;
        this.commentBlackListService = commentBlackListService;
        this.journalService = journalService;
        this.journalCommentService = journalCommentService;
        this.linkService = linkService;
        this.logService = logService;
        this.menuService = menuService;
        this.optionService = optionService;
        this.photoService = photoService;
        this.postService = postService;
        this.postCategoryService = postCategoryService;
        this.postCommentService = postCommentService;
        this.postMetaService = postMetaService;
        this.postTagService = postTagService;
        this.sheetService = sheetService;
        this.sheetCommentService = sheetCommentService;
        this.sheetMetaService = sheetMetaService;
        this.tagService = tagService;
        this.themeSettingService = themeSettingService;
        this.userService = userService;
        this.oneTimeTokenService = oneTimeTokenService;
        this.metnoteProperties = metnoteProperties;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Sanitizes the specified file name.
     *
     * @param unSanitized the specified file name
     * @return sanitized file name
     */
    public static String sanitizeFilename(final String unSanitized) {
        return unSanitized.
                replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5\\.)]", "").
                replaceAll("[\\?\\\\/:|<>\\*\\[\\]\\(\\)\\$%\\{\\}@~\\.]", "").
                replaceAll("\\s", "");
    }

    @Override
    public BasePostDetailDTO importMarkdown(MultipartFile file) throws IOException {

        // Read markdown content.
        String markdown = IoUtil.read(file.getInputStream(), StandardCharsets.UTF_8);

        // TODO sheet import

        return postService.importMarkdown(markdown, file.getOriginalFilename());
    }

    @Override
    public BackupDTO backupWorkDirectory() {
        // Zip work directory to temporary file
        try {
            // Create zip path for halo zip
            String haloZipFileName = MetnoteConst.METNOTE_BACKUP_PREFIX +
                    DateTimeUtils.format(LocalDateTime.now(), DateTimeUtils.HORIZONTAL_LINE_DATETIME_FORMATTER) +
                    IdUtil.simpleUUID().hashCode() + ".zip";
            // Create halo zip file
            Path haloZipPath = Files.createFile(Paths.get(metnoteProperties.getBackupDir(), haloZipFileName));

            // Zip halo
            FileUtils.zip(Paths.get(this.metnoteProperties.getWorkDir()), haloZipPath);

            // Build backup dto
            return buildBackupDto(BACKUP_RESOURCE_BASE_URI, haloZipPath);
        } catch (IOException e) {
            throw new ServiceException("Failed to backup halo", e);
        }
    }

    @Override
    public List<BackupDTO> listWorkDirBackups() {
        // Ensure the parent folder exist
        Path backupParentPath = Paths.get(metnoteProperties.getBackupDir());
        if (Files.notExists(backupParentPath)) {
            return Collections.emptyList();
        }

        // Build backup dto
        try (Stream<Path> subPathStream = Files.list(backupParentPath)) {
            return subPathStream
                    .filter(backupPath -> StringUtils.startsWithIgnoreCase(backupPath.getFileName().toString(), MetnoteConst.METNOTE_BACKUP_PREFIX))
                    .map(backupPath -> buildBackupDto(BACKUP_RESOURCE_BASE_URI, backupPath))
                    .sorted(Comparator.comparingLong(BackupDTO::getUpdateTime).reversed())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ServiceException("Failed to fetch backups", e);
        }
    }

    @Override
    public void deleteWorkDirBackup(String fileName) {
        Assert.hasText(fileName, "File name must not be blank");

        Path backupRootPath = Paths.get(metnoteProperties.getBackupDir());

        // Get backup path
        Path backupPath = backupRootPath.resolve(fileName);

        // Check directory traversal
        FileUtils.checkDirectoryTraversal(backupRootPath, backupPath);

        try {
            // Delete backup file
            Files.delete(backupPath);
        } catch (NoSuchFileException e) {
            throw new NotFoundException("The file " + fileName + " was not found", e);
        } catch (IOException e) {
            throw new ServiceException("Failed to delete backup", e);
        }
    }

    @Override
    public Resource loadFileAsResource(String basePath, String fileName) {
        Assert.hasText(basePath, "Base path must not be blank");
        Assert.hasText(fileName, "Backup file name must not be blank");

        Path backupParentPath = Paths.get(basePath);

        try {
            if (Files.notExists(backupParentPath)) {
                // Create backup parent path if it does not exists
                Files.createDirectories(backupParentPath);
            }

            // Get backup file path
            Path backupFilePath = Paths.get(basePath, fileName).normalize();

            // Check directory traversal
            FileUtils.checkDirectoryTraversal(backupParentPath, backupFilePath);

            // Build url resource
            Resource backupResource = new UrlResource(backupFilePath.toUri());
            if (!backupResource.exists()) {
                // If the backup resource is not exist
                throw new NotFoundException("The file " + fileName + " was not found");
            }
            // Return the backup resource
            return backupResource;
        } catch (MalformedURLException e) {
            throw new NotFoundException("The file " + fileName + " was not found", e);
        } catch (IOException e) {
            throw new ServiceException("Failed to create backup parent path: " + backupParentPath, e);
        }
    }

    @Override
    public BackupDTO exportData() {
        Map<String, Object> data = new HashMap<>();
        data.put("version", MetnoteConst.METNOTE_VERSION);
        data.put("export_date", DateUtil.now());
        data.put("attachments", attachmentService.listAll());
        data.put("categories", categoryService.listAll());
        data.put("comment_black_list", commentBlackListService.listAll());
        data.put("journals", journalService.listAll());
        data.put("journal_comments", journalCommentService.listAll());
        data.put("links", linkService.listAll());
        data.put("logs", logService.listAll());
        data.put("menus", menuService.listAll());
        data.put("options", optionService.listAll());
        data.put("photos", photoService.listAll());
        data.put("posts", postService.listAll());
        data.put("post_categories", postCategoryService.listAll());
        data.put("post_comments", postCommentService.listAll());
        data.put("post_metas", postMetaService.listAll());
        data.put("post_tags", postTagService.listAll());
        data.put("sheets", sheetService.listAll());
        data.put("sheet_comments", sheetCommentService.listAll());
        data.put("sheet_metas", sheetMetaService.listAll());
        data.put("tags", tagService.listAll());
        data.put("theme_settings", themeSettingService.listAll());
        data.put("user", userService.listAll());

        try {
            String haloDataFileName = MetnoteConst.METNOTE_DATA_EXPORT_PREFIX +
                    DateTimeUtils.format(LocalDateTime.now(), DateTimeUtils.HORIZONTAL_LINE_DATETIME_FORMATTER) +
                    IdUtil.simpleUUID().hashCode() + ".json";

            Path haloDataPath = Files.createFile(Paths.get(metnoteProperties.getDataExportDir(), haloDataFileName));

            FileWriter fileWriter = new FileWriter(haloDataPath.toFile(), CharsetUtil.UTF_8);
            fileWriter.write(JsonUtils.objectToJson(data));

            return buildBackupDto(DATA_EXPORT_BASE_URI, haloDataPath);
        } catch (IOException e) {
            throw new ServiceException("导出数据失败", e);
        }
    }

    @Override
    public List<BackupDTO> listExportedData() {

        Path exportedDataParentPath = Paths.get(metnoteProperties.getDataExportDir());
        if (Files.notExists(exportedDataParentPath)) {
            return Collections.emptyList();
        }

        try (Stream<Path> subPathStream = Files.list(exportedDataParentPath)) {
            return subPathStream
                    .filter(backupPath -> StringUtils.startsWithIgnoreCase(backupPath.getFileName().toString(), MetnoteConst.METNOTE_DATA_EXPORT_PREFIX))
                    .map(backupPath -> buildBackupDto(DATA_EXPORT_BASE_URI, backupPath))
                    .sorted(Comparator.comparingLong(BackupDTO::getUpdateTime).reversed())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ServiceException("Failed to fetch exported data", e);
        }
    }

    @Override
    public void deleteExportedData(String fileName) {
        Assert.hasText(fileName, "File name must not be blank");

        Path dataExportRootPath = Paths.get(metnoteProperties.getDataExportDir());

        Path backupPath = dataExportRootPath.resolve(fileName);

        FileUtils.checkDirectoryTraversal(dataExportRootPath, backupPath);

        try {
            // Delete backup file
            Files.delete(backupPath);
        } catch (NoSuchFileException e) {
            throw new NotFoundException("The file " + fileName + " was not found", e);
        } catch (IOException e) {
            throw new ServiceException("Failed to delete backup", e);
        }
    }

    @Override
    public void importData(MultipartFile file) throws IOException {
        String jsonContent = IoUtil.read(file.getInputStream(), StandardCharsets.UTF_8);

        ObjectMapper mapper = JsonUtils.createDefaultJsonMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };
        HashMap<String, Object> data = mapper.readValue(jsonContent, typeRef);

        List<Attachment> attachments = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("attachments")), Attachment[].class));
        attachmentService.createInBatch(attachments);

        List<Category> categories = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("categories")), Category[].class));
        categoryService.createInBatch(categories);

        List<Tag> tags = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("tags")), Tag[].class));
        tagService.createInBatch(tags);

        List<CommentBlackList> commentBlackList = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("comment_black_list")), CommentBlackList[].class));
        commentBlackListService.createInBatch(commentBlackList);

        List<Journal> journals = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("journals")), Journal[].class));
        journalService.createInBatch(journals);

        List<JournalComment> journalComments = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("journal_comments")), JournalComment[].class));
        journalCommentService.createInBatch(journalComments);

        List<Link> links = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("links")), Link[].class));
        linkService.createInBatch(links);

        List<Log> logs = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("logs")), Log[].class));
        logService.createInBatch(logs);

        List<Menu> menus = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("menus")), Menu[].class));
        menuService.createInBatch(menus);

        List<Option> options = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("options")), Option[].class));
        optionService.createInBatch(options);

        eventPublisher.publishEvent(new OptionUpdatedEvent(this));

        List<Photo> photos = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("photos")), Photo[].class));
        photoService.createInBatch(photos);

        List<Post> posts = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("posts")), Post[].class));
        postService.createInBatch(posts);

        List<PostCategory> postCategories = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("post_categories")), PostCategory[].class));
        postCategoryService.createInBatch(postCategories);

        List<PostComment> postComments = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("post_comments")), PostComment[].class));
        postCommentService.createInBatch(postComments);

        List<PostMeta> postMetas = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("post_metas")), PostMeta[].class));
        postMetaService.createInBatch(postMetas);

        List<PostTag> postTags = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("post_tags")), PostTag[].class));
        postTagService.createInBatch(postTags);

        List<Sheet> sheets = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("sheets")), Sheet[].class));
        sheetService.createInBatch(sheets);

        List<SheetComment> sheetComments = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("sheet_comments")), SheetComment[].class));
        sheetCommentService.createInBatch(sheetComments);

        List<SheetMeta> sheetMetas = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("sheet_metas")), SheetMeta[].class));
        sheetMetaService.createInBatch(sheetMetas);

        List<ThemeSetting> themeSettings = Arrays.asList(mapper.readValue(mapper.writeValueAsString(data.get("theme_settings")), ThemeSetting[].class));
        themeSettingService.createInBatch(themeSettings);

        eventPublisher.publishEvent(new ThemeUpdatedEvent(this));
    }

    /**
     * Builds backup dto.
     *
     * @param backupPath backup path must not be null
     * @return backup dto
     */
    private BackupDTO buildBackupDto(@NonNull String basePath, @NonNull Path backupPath) {
        Assert.notNull(basePath, "Base path must not be null");
        Assert.notNull(backupPath, "Backup path must not be null");

        String backupFileName = backupPath.getFileName().toString();
        BackupDTO backup = new BackupDTO();
        try {
            backup.setDownloadLink(buildDownloadUrl(basePath, backupFileName));
            backup.setFilename(backupFileName);
            backup.setUpdateTime(Files.getLastModifiedTime(backupPath).toMillis());
            backup.setFileSize(Files.size(backupPath));
        } catch (IOException e) {
            throw new ServiceException("Failed to access file " + backupPath, e);
        }

        return backup;
    }

    /**
     * Builds download url.
     *
     * @param filename filename must not be blank
     * @return download url
     */
    @NonNull
    private String buildDownloadUrl(@NonNull String basePath, @NonNull String filename) {
        Assert.notNull(basePath, "Base path must not be null");
        Assert.hasText(filename, "File name must not be blank");

        // Composite http url
        String backupUri = basePath + MetnoteUtils.URL_SEPARATOR + filename;

        // Get a one-time token
        String oneTimeToken = oneTimeTokenService.create(backupUri);

        // Build full url
        return MetnoteUtils.compositeHttpUrl(optionService.getBlogBaseUrl(), backupUri)
                + "?"
                + MetnoteConst.ONE_TIME_TOKEN_QUERY_NAME
                + "=" + oneTimeToken;
    }

}
