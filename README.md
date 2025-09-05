# AtlasCivs API
AC-API is a dumbed-down fork of [TAPI](https://github.com/besser435/TEAW-API) for AtlasCivs. It is a super simple plugin that provides a web-server which exposes data about the AtlasCivs Minecraft server. 
It shows things like online players, PvP kills, player statistics, and more.

The Spigot API is very simple, so this can easily be expanded.

## Programmer Notes & To Do
Try to actually be compliant with the [json:api spec](https://jsonapi.org/).

Store player statistics locally so that offline players can still be queried.

## Configuration
In the `config.yml` file, there are a few options.

AC-API implements a nano sized HTTP server for replying to requests, and as such
needs to live on a port. The default port for the server is 9007.

## Endpoints
Most endpoint fields are self-explanatory. Where they are not, there will be a note.


### `/api/online_players` GET

Returns a list of online players.

`afk_duration` Is the AFK duration for a player in milliseconds if they are AFK.
If the player has moved within the configured AFK threshold, this will be 0.

Players can set a biography with the `/atlas bio set` command.

Example response:
```json
{
  "online_players": {
    "75418e9c-34ef-4926-af64-96d98d10954c": {
      "name": "brandonusa",
      "online_duration": 5424,
      "afk_duration": 0,
      "bio": "Money or else!"
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
  "acapi_build": "2025-09-04T04:59:48Z",
  "system_time": 1756962872365,
  "world_time_24h": "06:08",
  "acapi_version": "0.0.1",
  "weather": "Clear",
  "loaded_chunks": 625,
  "world_time_ticks": 141,
  "server_version": "1.21.8-50-51706e5 (MC: 1.21.8)",
  "day": 3
}

```

### `/api/kill_history` GET
Returns info about PvP kills.

Returns a list of the last 50 Player vs. Player kills. An optional `time` argument can be provided, where only kills after
the timestamp are provided. The `time` argument is a Unix epoch in milliseconds.
Ex: `/api/kill_history?time=1756962020017`

Example response:
```json
[
  {
    "killer_uuid": "75418e9c-34ef-4926-af64-96d98d10954c",
    "killer_name": "bessyusa",
    "victim_uuid": "75418e9c-34ef-4926-af64-96d98d10954c",
    "victim_name": "bessyusa",
    "death_message": "bessyusa was killed by magic while trying to escape bessyusa",
    "weapon": {
      "type": "bow",
      "enchantments": [
        {
          "id": "infinity",
          "level": 1
        },
        {
          "id": "power",
          "level": 5
        },
        {
          "id": "flame",
          "level": 1
        }
      ]
    },
    "timestamp": 1756962038085
  }
]
```



### API Errors
Any error in the response will be returned as a JSON object along with its HTTP status.

Example: `{"error": "Not found"}` for a 404, or `{"error": "UUID malformed"}` for a 400.

## Building
The plugin is built with Maven, and is edited with IntelliJ IDEA. It is free for students.
[This video](https://www.youtube.com/watch?v=s1xg9eJeP3E) is helpful for getting started.
