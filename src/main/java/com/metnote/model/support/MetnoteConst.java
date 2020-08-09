package com.metnote.model.support;

import org.springframework.http.HttpHeaders;

import java.io.File;
import java.util.Optional;

/**
 * <pre>
 *     公共常量
 * </pre>
 *
 * @author ryanwang
 * @date 2017/12/29
 */
public class MetnoteConst {

    /**
     * User home directory.
     */
//    public final static String USER_HOME = System.getProperties().getProperty("user.home");

    public final static String DATA_BASE = "/var/metnote";

    /**
     * Temporary directory.
     */
    public final static String TEMP_DIR = "/tmp/metnote";

    public final static String PROTOCOL_HTTPS = "https://";

    public final static String PROTOCOL_HTTP = "http://";

    public final static String URL_SEPARATOR = "/";

    /**
     * Metnote backup prefix.
     */
    public final static String METNOTE_BACKUP_PREFIX = "metnote-backup-";

    /**
     * Metnote data export prefix.
     */
    public final static String METNOTE_DATA_EXPORT_PREFIX = "metnote-data-export-";

    /**
     * Static pages pack prefix.
     */
    public final static String STATIC_PAGE_PACK_PREFIX = "static-pages-";

    /**
     * Default theme name.
     */
    public final static String DEFAULT_THEME_ID = "caicai_anatole";

    /**
     * Default error path.
     */
    public static final String DEFAULT_ERROR_PATH = "common/error/error";

    /**
     * Path separator.
     */
    public static final String FILE_SEPARATOR = File.separator;
    /**
     * Suffix of freemarker template file
     */
    public static final String SUFFIX_FTL = ".ftl";
    /**
     * Custom freemarker tag method key.
     */
    public static final String METHOD_KEY = "method";
    /**
     * 网易云音乐短代码前缀
     */
    public static final String NETEASE_MUSIC_PREFIX = "[music:";
    /**
     * 网易云音乐 iframe 代码
     */
    public static final String NETEASE_MUSIC_IFRAME = "<iframe frameborder=\"no\" border=\"0\" marginwidth=\"0\" marginheight=\"0\" width=330 height=86 src=\"//music.163.com/outchain/player?type=2&id=$1&auto=1&height=66\"></iframe>";
    /**
     * 网易云音乐短代码正则表达式
     */
    public static final String NETEASE_MUSIC_REG_PATTERN = "\\[music:(\\d+)\\]";
    /**
     * 哔哩哔哩视频短代码前缀
     */
    public static final String BILIBILI_VIDEO_PREFIX = "[bilibili:";
    /**
     * 哔哩哔哩视频 iframe 代码
     */
    public static final String BILIBILI_VIDEO_IFRAME = "<iframe height=$3 width=$2 src=\"//player.bilibili.com/player.html?aid=$1\" scrolling=\"no\" border=\"0\" frameborder=\"no\" framespacing=\"0\" allowfullscreen=\"true\"> </iframe>";
    /**
     * 哔哩哔哩视频正则表达式
     */
    public static final String BILIBILI_VIDEO_REG_PATTERN = "\\[bilibili:(\\d+)\\,(\\d+)\\,(\\d+)\\]";
    /**
     * YouTube 视频短代码前缀
     */
    public static final String YOUTUBE_VIDEO_PREFIX = "[youtube:";
    /**
     * YouTube 视频 iframe 代码
     */
    public static final String YOUTUBE_VIDEO_IFRAME = "<iframe width=$2 height=$3 src=\"https://www.youtube.com/embed/$1\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
    /**
     * YouTube 视频正则表达式
     */
    public static final String YOUTUBE_VIDEO_REG_PATTERN = "\\[youtube:(\\w+)\\,(\\d+)\\,(\\d+)\\]";
    /**
     * Github Api url for metnote-admin release.
     */
    public final static String METNOTE_ADMIN_RELEASES_LATEST = "https://api.github.com/repos/metnote-dev/metnote-admin/releases/latest";
    /**
     * Metnote admin version regex.
     */
    public final static String METNOTE_ADMIN_VERSION_REGEX = "metnote-admin-\\d+\\.\\d+(\\.\\d+)?(-\\S*)?\\.zip";
    public final static String METNOTE_ADMIN_RELATIVE_PATH = "templates/admin/";
    public final static String METNOTE_ADMIN_RELATIVE_BACKUP_PATH = "templates/admin-backup/";
    /**
     * Content token header name.
     */
    public final static String API_ACCESS_KEY_HEADER_NAME = "API-" + HttpHeaders.AUTHORIZATION;
    /**
     * Admin token header name.
     */
    public final static String ADMIN_TOKEN_HEADER_NAME = "ADMIN-" + HttpHeaders.AUTHORIZATION;
    /**
     * Admin token param name.
     */
    public final static String ADMIN_TOKEN_QUERY_NAME = "admin_token";
    /**
     * Temporary token.
     */
    public final static String TEMP_TOKEN = "temp_token";
    /**
     * Content api token param name
     */
    public final static String API_ACCESS_KEY_QUERY_NAME = "api_access_key";
    public final static String ONE_TIME_TOKEN_QUERY_NAME = "ott";
    public final static String ONE_TIME_TOKEN_HEADER_NAME = "ott";
    /**
     * Version constant. (Available in production environment)
     */
    public static final String METNOTE_VERSION;

    /**
     * Unknown version: unknown
     */
    public static final String UNKNOWN_VERSION = "unknown";

    /**
     * Database product name.
     */
    public static String DATABASE_PRODUCT_NAME = null;
    /**
     * user_session
     */
    public static String USER_SESSION_KEY = "user_session";

    static {
        // Set version
        METNOTE_VERSION = Optional.ofNullable(MetnoteConst.class.getPackage().getImplementationVersion()).orElse(UNKNOWN_VERSION);
    }
}
