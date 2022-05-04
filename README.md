# EAMGS

## Project structure

* **server** : server, written with Kotlin and Java.
* **game-*** : game module, loaded by server.
* **frontend** : webui, written in Vue with TypeScript.

## Build

```shell
# build server
./gradlew :server:build
./gradlew :game-iidx:build
./gradlew :game-sdvx:build
./gradlew :server:shadowJar
# build webui
cd frontend
npm run build
```

The server executable jar program is located in `server/build/libs/server-*.*-all.jar`

Game module is located in `game-*/build/libs/game-*-*.*.jar`

Webui distribution is located in `frontend/dist`

## Deploy

You should first prepare a mysql database.

### server

You should put all files like this:

```
<root dir>
├─── server-*.*-all.jar  <---- server executable jar program
├─── plugins  <---- game module folder
│   ├─── game-iidx-*.*.jar
│   ├─── game-sdvx-*.*.jar
│   └─── ...
└─── config.json <---- server config, generate at first launch.
```

Run

```shell
java -jar server-*.*-all.jar
```

### webui

Check out deployment instructions at https://cli.vuejs.org/guide/deployment.html

### Development

You should add VM options `-Dme.stageguard.eamuse.dev=1` to `ApplicationMainKt` in Run/Debug Configurations
in order to change server plugins folder to `game-*/build/libs/` for developing purpose.

See more
at: [server/src/main/kotlin/me/stageguard/eamuse/ApplicationMain.kt](server/src/main/kotlin/me/stageguard/eamuse/ApplicationMain.kt)

# Disclaimer

**All media resources in [frontend/src/assets/sdvx](frontend/src/assets/sdvx) and game data
in [game-sdvx/src/main/resources/sdvx6](game-sdvx/src/main/resources/sdvx6) are copyrighted by © Konami Amusement and
don't obey the [AGPLv3](LICENSE) open-source license.**

Some source code is transmitted from [asphyxia-core/plugins](https://github.com/asphyxia-core/plugins).

**This project is developed only for educational and test purpose and for full stack programming practice.**
    
