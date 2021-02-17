package com.casko1.wheelbarrow.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class WeatherCommand extends Command {

    private final String weatherToken;
    private String location;

    public WeatherCommand(String weatherToken){
        this.name = "weather";
        this.help = "Displays 5 day weather forecast for specified location";
        this.arguments = "<name of location>";
        this.guildOnly = false;
        this.weatherToken = weatherToken;
    }

    @Override
    protected void execute(CommandEvent event) throws ArrayIndexOutOfBoundsException{

        this.location = event.getArgs();

        if(location.isEmpty()){
            event.reply("You must provide a location.");
        }
        else{
            Unirest.get("https://api.openweathermap.org/data/2.5/forecast?q={location}&appid={token}")
                    .routeParam("location", location)
                    .routeParam("token", weatherToken)
                    .asJsonAsync(response -> response.ifSuccess(r -> {
                        MessageEmbed em = parseWeatherData(r);
                        event.reply(em);
                    })
                            .ifFailure(e -> event.reply("An error occurred. Please try again.")));
        }

    }

    private MessageEmbed parseWeatherData(HttpResponse<JsonNode> data){
        EmbedBuilder eb = new EmbedBuilder();
        int timezone = data.getBody().getObject().getJSONObject("city").getInt("timezone") / 3600;

        eb.setTitle("Weather in " + location.toUpperCase());
        eb.setDescription("Timezone: " + (timezone < 0 ? "GMT-" : "GMT+") + Math.abs(timezone));

        JSONArray parsed = data.getBody().getObject().getJSONArray("list");

        //split array by date

        ArrayList<ArrayList<JSONObject>> split = new ArrayList<>();

        //initialize empty arraylists
        for(int i=0; i < 5; i++){
            ArrayList<JSONObject> tmp = new ArrayList<>();
            for(int j = 0; j < 3; j++){
                tmp.add(null);
            }
            split.add(tmp);
        }

        //counter for keeping current arraylist
        int day = 0;

        ArrayList<JSONObject> currentDayArray = split.get(0);

        JSONObject initialObject = parsed.getJSONObject(0);

        //fail safe for special case of first day being almost over
        String firstDay = initialObject.getString("dt_txt").split("\\s+")[0];

        String initialDay = getDayFromDate(initialObject);
        String initialTime = getTimeFromDate(initialObject);

        int tmp = timeCheck(initialTime);
        if(tmp >= 0){
            currentDayArray.set(tmp, parsed.getJSONObject(0));
        }


        for(int i = 1; i < parsed.length(); i++){
            JSONObject current = parsed.getJSONObject(i);
            String nextDay = getDayFromDate(current);
            String nextDayTime = getTimeFromDate(current);

            tmp = timeCheck(nextDayTime);
            if(tmp >= 0){
                if(nextDay.equals(initialDay)){
                    currentDayArray.set(tmp, current);
                }
                else{
                    if(day == 4) break;
                    day++;
                    currentDayArray = split.get(day);
                    currentDayArray.set(tmp, current);
                    initialDay = nextDay;
                }
            }
        }

        //adding fields to embed

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 5; i++){
            sb.setLength(0);
            ArrayList<JSONObject> currentDay = split.get(i);
            String dayOfWeek = "";

            for(int j = 0; j < 3; j++){
                JSONObject currentHour = currentDay.get(j);
                if(currentHour == null){
                    sb.append(9 + j * 6).append(":00: :x:");
                }
                else{
                    sb.append(9 + j * 6)
                            .append(":00: ")
                            .append(getEmoji(currentHour.getJSONArray("weather").getJSONObject(0).getString("main")))
                            .append(" ");

                    if(dayOfWeek.equals("")){
                        dayOfWeek = getDayOfWeek(currentHour.getString("dt_txt").split("\\s+")[0]);
                    }
                }
            }

            //special case where first day is almost over
            if(dayOfWeek.equals("")){
                dayOfWeek = getDayOfWeek(firstDay);
            }

            eb.addField(dayOfWeek, sb.toString(), false);

        }



        return eb.build();
    }

    private int timeCheck(String time){
        return switch (time) {
            case "09" -> 0;
            case "15" -> 1;
            case "21" -> 2;
            default -> -1;
        };
    }

    private String getTimeFromDate(JSONObject input){
        return input.getString("dt_txt").split("\\s+")[1].split(":")[0];
    }

    private String getDayFromDate(JSONObject input){
        return  input.getString("dt_txt").split("\\s+")[0].split("-")[2];
    }

    private String getDayOfWeek(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("uuuu-MM-dd"))
                .getDayOfWeek()
                .getDisplayName(TextStyle.SHORT_STANDALONE, Locale.US)
                .toUpperCase();
    }

    private String getEmoji(String weather){
        return switch (weather) {
            case "Clear" -> ":sunny:";
            case "Clouds" -> ":cloud:";
            case "Snow" -> ":cloud_snow:";
            case "Rain", "Drizzle" -> ":cloud_rain:";
            case "Thunderstorm" -> ":thunder_cloud_rain:";
            default -> ":x:";
        };
    }
}
