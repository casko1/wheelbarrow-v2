package com.casko1.wheelbarrow.bot.utils;

import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.io.IOException;
import java.util.function.Consumer;

public final class FaceUtil {

    public static void getLandmarks(String url, Consumer<JSONObject> callback) throws IOException {
        Unirest.post("https://wheelbarrow.cognitiveservices.azure.com/face/v1.0/detect")
                .contentType("application/json")
                .header("Ocp-Apim-Subscription-Key", PropertiesUtil.getProperty("azureFaceApiToken"))
                .queryString("returnFaceLandmarks", "true")
                .queryString("recognitionMode", "detection_03")
                .body("{\"url\": \"" + url + "\"}")
                .asJsonAsync(response -> response.ifSuccess(r -> {
                    if(r.getBody().getArray().isEmpty()) {
                        callback.accept(null);
                    }
                    else {
                        callback.accept(r.getBody().getArray().getJSONObject(0).getJSONObject("faceLandmarks"));
                    }
                }).ifFailure(e -> callback.accept(null)));
    }
}
