package com.casko1.wheelbarrow.utils;


import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.validator.routines.UrlValidator;

public final class ArgumentsUtil {

    public static boolean isFloat(String arg){
        try {
            Float.parseFloat(arg);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String arg){
        try {
            Double.parseDouble(arg);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String arg){
        try {
            Integer.parseInt(arg);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }

    public static boolean isUrl(String url){
        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes);

        return urlValidator.isValid(url);
    }

    public static String parseURL(String url){
        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes);

        if(urlValidator.isValid(url)){
            try{
                URI link = new URI(url);
                return link.getHost().toLowerCase();
            } catch (URISyntaxException e){
                return "";
            }
        }

        return "";
    }

    public static String parseSpotifyUrl(String url){
        String[] split = url.split("/");

        return split[3];
    }
}
