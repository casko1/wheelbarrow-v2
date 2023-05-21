# wheelbarrow-v2
##### Discord bot written in Java using JDA. It can do bunch of different stuff such as:
- Play music and apply different filters to it
- Load playlist from various sources
- Offers support for displaying track thumbnails
- Recognize music from embeded videos by right clicking on the video and selecting `detect song`
- Display weather forecast

#### Setting up the bot
##### FFmpeg is needed for some of the functionallities so make sure it is installed and reachable from the command propts on you system

To host the bot on your local machine you'll first need to grab the JAR from the releases sidebar or compile the project
yourself by cloning the repository and building the project using gradle (`gradle build`).

Next start the bot up for the first time in order to generate the `wheelbarrow.properties` file.
The bot can be launched using `java -jar wheelbarrow-bot-xx.jar`. After the properties file is
generated the following needs to be configured:
- botToken (required, without it the bot won't work)

All the other fields are optional and can be left as they are as they require you to get your own API keys. Here is a short
explanation of what each API enables:

- spotifyId, spotifySecret: [Spotify's API](https://developer.spotify.com/) which is used for fetching thumbnails and loading spotify playlists
- shazamCoreApi: An API used for the song recognition command which can be obtained at the [RapidAPI website](https://rapidapi.com/tipsters/api/shazam-core/)
- weatherToken: used for the weather forecast. Uses [OpenWeather's API](https://openweathermap.org/api).

Functionalities are toggleable through the `.properties` file and are all disabled by default.

After the config is set-up the bot can again be started with the previously mentioned command.

#### Basic commands (default prefix is `--`)

`help` - return a direct message explaining the usage of different command  
`ping` - reports latency to the API  
`join` - makes bot join the voice channel you are currently in  
`stop` - makes the bot stop playing music  
`skip` - skips the current track  
`nowplaying` - displays information about current track  
`queue` - return the current music queue  
`filters` - displays enabled filters  
`loop` - loops current track  
`seek` <timestamp in seconds> - seeks current track to specified timestamp (in seconds)  
`remove <position in queue>` - removes a track from the queue  
`shuffle` - shuffles current queue  
`clear` - clears the entire queue  
`play <url or search term>` - plays a song or playlist from specified url or query  

#### Slash commands
Wheelbarrow also supports slash commands. The help for these comments will automatically show up when entering the command

`/play url <url>` - plays specified url  
`/play search <search term>` - plays the selected track. The command autocompletes the search results, enabling you to search for tracks/videos inside Discord itself  
`/filter` - used for applying audio filters to currently playing and any susequent tracks

