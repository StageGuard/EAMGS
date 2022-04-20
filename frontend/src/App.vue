<template>
  <nav>
    <router-link to="/">Dashboard</router-link>
    <router-link to="/profile">Profile</router-link>
    <router-link to="/ranking">Ranking</router-link>
    <router-link to="/settings">Settings</router-link>
    <router-link to="/about">About</router-link>
    <span class="user-info">user name</span>
  </nav>
  <div class="root-content">
    <router-view/>
  </div>
</template>

<script setup lang="ts">
import { provide, ref } from 'vue'
import config from '@/config'
import { ServerStatus } from '@/props/server-status'
import { GameInfo } from '@/props/game-info'

const contentWidth = 1200
const sidePadding = ref<string>('40px')

function __onWindowResize () {
  if (document.body.clientWidth > contentWidth) {
    sidePadding.value = `${Math.max((document.body.clientWidth - contentWidth) / 2, 40)}px`
  } else {
    sidePadding.value = '40px'
  }
}

window.onresize = __onWindowResize
__onWindowResize()

interface _ServerStatus { $delegate: ServerStatus }
interface _GameInfo { $delegate: GameInfo }

const status = ref<_ServerStatus>({
  $delegate: {
    online: null,
    dbStatus: null,
    startupEpochSecond: null,
    profileCount: null
  }
})
const games = ref<Map<string, _GameInfo>>(new Map())

fetch(`${config.host}/status`).then(r => r.json()).then(r => {
  status.value.$delegate.online = true
  if (r.result !== -1) {
    status.value.$delegate.dbStatus = r.dbStatus
    status.value.$delegate.startupEpochSecond = r.startupEpochSecond
    status.value.$delegate.profileCount = r.profileCount

    for (const gid in r.games) {
      games.value.set(gid, {
        $delegate: {
          id: gid,
          name: r.games[gid].name,
          supportedVersions: r.games[gid].supportedVersions,
          api: {
            info: r.games[gid].api.info,
            ranking: r.games[gid].api.ranking,
            profile: r.games[gid].api.profile,
            customize: {
              get: r.games[gid].api.customize_get,
              update: r.games[gid].api.customize_update
            }
          },
          otherApi: (() => {
            const api = r.games[gid].api
            const commonApi = ['info', 'ranking', 'profile', 'customize_get', 'customize_update']
            const oa = new Map<string, string>()
            for (const k in api) {
              if (commonApi.indexOf(k) === -1) oa.set(k, api[k])
            }
            return oa
          })()
        }
      })
    }
  } else {
    console.log('Failed to fetch server status: ' + r.message) // TODO: show error
    status.value.$delegate.startupEpochSecond = -1
    status.value.$delegate.profileCount = -1
  }
}).catch(reason => {
  status.value.$delegate.online = false
  status.value.$delegate.dbStatus = false
  status.value.$delegate.startupEpochSecond = -1
  status.value.$delegate.profileCount = -1
  console.log(reason) // TODO: show error
})
provide<_ServerStatus>('server-status', status.value)
provide('games', games.value)
</script>

<style>
@font-face {
  font-family: 'Gilroy Medium';
  src: url('./assets/Gilroy-Medium.otf') format('opentype');
  font-weight: normal;
}

@font-face {
  font-family: 'Gilroy Bold';
  src: url('assets/Gilroy-Bold.otf') format('opentype');
  font-weight: bold;
}

/*noinspection ALL*/
#app {
  font-family: 'Gilroy Medium', 'Gilroy Bold', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.user-info {
  display: flex;
  float: right;
}

nav {
  padding: 35px v-bind(sidePadding);
  border-width: 0 0 1px 0;
  border-style: solid;
  border-color: rgba(169, 169, 169, 0.85);
  background-color: white;
  box-shadow: darkgrey 0 3px 20px 0;
}

nav a {
  align-self: center;
  font-weight: normal;
  padding: 15px 30px;
  margin: 0 10px;
  color: rgb(179, 184, 194);
  border-radius: 90px;
  text-decoration-line: none;
  transition: all 0.2s ease-in-out;
}

nav a:hover {
  color: rgb(255, 255, 255);
  background-color: rgb(199, 204, 213);
}

/*noinspection ALL*/
nav .router-link-exact-active {
  font-weight: bold;
  color: white;
  background-color: rgb(20, 85, 254);
  background-clip: padding-box;
  border-radius: 90px;
  filter: drop-shadow(2px 2px 15px rgba(0, 0, 0, 0.3));
  transition: all 0.2s ease-in-out;
}

.root-content {
  padding-left: v-bind(sidePadding);
  padding-right: v-bind(sidePadding);
}

</style>
