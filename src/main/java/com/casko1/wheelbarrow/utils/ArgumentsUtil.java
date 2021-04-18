package com.casko1.wheelbarrow.utils;

import java.net.URI;
import java.net.URISyntaxException;

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
        try{
            new URI(url);
            return true;
        } catch (URISyntaxException e){
            return false;
        }
    }
}
