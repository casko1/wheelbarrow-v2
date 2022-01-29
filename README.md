# wheelbarrow-v2
##### Discord bot written in Java using JDA. It can do bunch of different stuff such as:
- Play music and apply different filters to it
- Load playlist from various sources such as YouTube and Spotify
- Offers support for displaying track thumbnails
- Recognize music from video URLs
- Image effects
- Display weather forecast

#### Setting up the bot

This bot is best used for self hosted as it is not designed for large scale deployment 
(would need to implement sharding and further code optimizations). To host the bot on 
your local machine you'll first need to grab the JAR under releases (soon) or compile the project
yourself using maven (`mvn package app`).

Next start the bot up for the first time in order to generate the `wheelbarrow.properties` file.
The bot can be launched using `java -jar wheelbarrow-bot-xx.jar`. After the properties file is
generated the following needs to be configured:
- botToken (required, without it the bot won't work)

All the other fields are optional and can be left as they are as they require you to get your own API keys. Here is a short
explanation of what each API enables:

- spotifyId, spotifySecret: [Spotify's API](https://developer.spotify.com/) which is used for fetching thumbnails and loading spotify playlists 
- azureFaceApiToken: [Microsoft's face API](https://azure.microsoft.com/en-us/pricing/details/cognitive-services/face-api/) used for face recognition which is used for image effects
- shazamCoreApi: An API used for the song recognition command which can be obtained at the [RapidAPI website](https://rapidapi.com/tipsters/api/shazam-core/)
- weatherToken: used for the weather forecast. Uses [OpenWeather's API](https://openweathermap.org/api).

Functionalities are toggleable through the `.properties` file and are all disabled by default.

After the config is set-up the bot can again be started with the previously mentioned command.



