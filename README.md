# AtlasCivs API
ACAPI is a dumbed-down fork of [TAPI](https://github.com/besser435/TEAW-API) for AtlasCivs. It is a super simple plugin that provides a web-server which exposes data about the AtlasCivs Minecraft server. 
It shows things like online players, Towny info, player statistics, and more.

The Spigot and dependency APIs are very simple, so this can easily be expanded.

## Programmer Notes & To Do
Spark isn't updated anymore, transition to something else like Javalin. <br>
Try to actually be compliant with the [json:api spec](https://jsonapi.org/). <br>
Store player statistics locally so that offline players can still be queried.

Data is **not** normalized. For example, we have Towny UUIDs and names in several endpoints. 
In the `online_players` endpoint, we have `town`, `town_name`, `nation`,`nation_name`. And in the towny endpoint, 
we have a `residents` array with player UUIDs. This is nice as we don't have to query `online_players`, then query `towny`
to get the name of a Town a player is in. But it is bad, as we repeat a lot of information. 

When it comes to building the database (external project that relies on ACAPI), it will be larger than it has to as the data is not normalized. This is bad,
and should be addressed in the future.

## Configuration
In the `config.yml` file, there are a few options. `enable`, `port`, `discord_channel_id`, & `afk_timeout`.

ACAPI implements a nano sized HTTP server for replying to requests, and as such
needs to live on a port. The default port for the server is 1850.


## Endpoints
  Most endpoint fields are self-explanatory. Where they are not, there will be a note.


### `/api/online_players` GET

Returns a list of online players.

`afk_duration` Is the AFK duration for a player in milliseconds if they are AFK.
If the player has moved within the configured AFK threshold, this will be 0.


Example response:
```json
{
  "online_players": {
    "75418e9c-34ef-4926-af64-96d98d10954c": {
      "name": "brandonusa",
      "online_duration": 5424,
      "afk_duration": 0
    }
  }
}
```

### `/api/full_player_stats/:uuid` GET

Returns the three [statistics](https://minecraft.wiki/w/Statistics) categories for a given player UUID. Stats with 
a zero value will not be returned. The player must be online for success, otherwise it will return 404.

Example response:
```json
{
  "general": {
    "DAMAGE_TAKEN": 230,
    "LEAVE_GAME": 243,
    "FALL_ONE_CM": 6911
  },
  "mob": {
    "KILL_ENTITY": {
      "FROG": 9,
      "SALMON": 1,
      "TRADER_LLAMA": 2
    }
  },
  "item": {
    "PICKUP": {
      "DIAMOND_BLOCK": 1,
      "DIAMOND": 3420,
      "FISHING_ROD": 2
    },
    "USE_ITEM": {
      "NETHERITE_BLOCK": 2,
      "MANGROVE_LEAVES": 6,
      "CREATE_MECHANICAL_BEARING": 9
    },
    "DROP": {
      "CREATE_CREATIVE_FLUID_TANK": 1,
      "DIAMOND": 340,
      "FISHING_ROD": 1
    }
  }
}
```

### `/api/server_info` GET

Returns some info about the server and world.

Example response:
```json
{
  "acapi_version": "1.0.0",
  "system_time": 1733635909945,
  "world_time_24h": "06:10",
  "weather": "Thunderstorms",
  "acapi_build": "2024-12-08T05:29:19Z",
  "world_time_ticks": 180,
  "server_version": "arclight-1.20.1-1.0.5-1a8925b (MC: 1.20.1)",
  "day": 756,
  "loaded_chunks": 1504
}

```

### API Errors
Any error in the response will be returned as a JSON object along with its HTTP status.

Example: `{"error": "Not found"}` for a 404, or `{"error": "UUID malformed"}` for a 400.

## Building
The plugin is built with Maven, and is edited with IntelliJ IDEA. It is free for students.
[This video](https://www.youtube.com/watch?v=s1xg9eJeP3E) is helpful for getting started.
