package com.casko1.wheelbarrow.bot.utils;

import kong.unirest.Unirest;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ArgumentsUtil {

    private static final Logger logger = LoggerFactory.getLogger(ArgumentsUtil.class);

    static Set<String> allowedVideoTypes = new HashSet<>(Arrays.asList("video/quicktime", "video/mp4", "audio/mpeg",
            "video/webm", "video/mpeg", "audio/ogg", "video/ogg", "audio/opus"));

    public static boolean isFloat(String arg) {
        try {
            Float.parseFloat(arg);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String arg) {
        try {
            Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isUrl(String url) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);

        return urlValidator.isValid(url);
    }

    public static String parseURL(String url) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);

        if (urlValidator.isValid(url)) {
            try {
                URI link = new URI(url);
                return link.getHost().toLowerCase();
            } catch (URISyntaxException e) {
                logger.error("An error occurred while parsing track url: {}", e.toString());
                return "";
            }
        }

        return "";
    }

    public static String parseSpotifyUrl(String url) {
        String[] split = url.split("/");

        return split[3];
    }

    public static String getContentUrl(Message message) {
        String url = null;

        if (!message.getAttachments().isEmpty()) url = message.getAttachments().get(0).getProxyUrl();
        if (!message.getEmbeds().isEmpty()) url = message.getEmbeds().get(0).getVideoInfo().getProxyUrl();

        return url;
    }

    public static String getUrlContentType(String url) {
        return Unirest.head(url).asString().getHeaders().get("Content-Type").get(0);
    }

    public static boolean isValidVideoType(String contentType) {
        return allowedVideoTypes.contains(contentType);
    }
}
