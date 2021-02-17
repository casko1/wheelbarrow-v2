package com.casko1.wheelbarrow.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.JsonResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;

import java.util.List;

public class WeatherCommand extends Command {

    private final String weatherToken;

    public WeatherCommand(String weatherToken){
        this.name = "weather";
        this.help = "Displays 5 day weather forecast for specified location";
        this.arguments = "<name of location>";
        this.guildOnly = false;
        this.weatherToken = weatherToken;
    }

    @Override
    protected void execute(CommandEvent event) throws ArrayIndexOutOfBoundsException{

        if(event.getArgs().isEmpty()){
            event.reply("You must provide a location.");
        }
        else{
            Unirest.get("https://api.openweathermap.org/data/2.5/forecast?q={location}&appid={token}")
                    .routeParam("location", event.getArgs())
                    .routeParam("token", weatherToken)
                    .asJsonAsync(response -> response.ifSuccess(this::parseWeatherData)
                            .ifFailure(e -> event.reply("An error occurred. Please try again.")));
        }

    }

    private void parseWeatherData(HttpResponse<JsonNode> data){

    }
}
