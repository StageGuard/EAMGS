<!--
  - Copyright (c) 2022 StageGuard
  - This program is free software: you can redistribute it and/or modify
  - it under the terms of the GNU Affero General Public License as published
  - by the Free Software Foundation, either version 3 of the License, or
  - (at your option) any later version.
  -
  - This program is distributed in the hope that it will be useful,
  - but WITHOUT ANY WARRANTY; without even the implied warranty of
  - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  - GNU Affero General Public License for more details.
  -
  - You should have received a copy of the GNU Affero General Public License
  - along with this program.  If not, see <https://www.gnu.org/licenses/>.
  -->

<template>
  <nav>
    <router-link to="/">Dashboard</router-link>
    <router-link to="/profile">Profile</router-link>
    <router-link to="/ranking">Ranking</router-link>
    <router-link to="/settings">Settings</router-link>
    <router-link to="/about">About</router-link>
    <span class="card-info" @click="handleClickCardInfo()">{{ refId ? refId : "[UNVERIFIED]" }}</span>
  </nav>
  <div id="root-content">
    <router-view/>
  </div>
  <full-screen-dialog
    ref="verificationDialog"
    type="input"
    title="Verification"
    desc="Please input card id and pin to verify your profile"
    :input="[
      { name: 'Card Id', desc: 'Your EAM card id, start with E004', default: cardId },
      { name: 'Pin', desc: 'Password of your card, 4 digit', default: cardPin }
    ]"
    @on-select="handleVerificationDialog"
  ></full-screen-dialog>
</template>

<script setup lang="ts">
import { computed, provide, ref } from 'vue'
import config from '@/config'
import { ServerStatus } from '@/props/server-status'
import { GameInfo } from '@/props/game-info'
import { getCookie, setCookie } from '@/utils/cookie'
import FullScreenDialog from '@/components/FullScreenDialog.vue'

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

const verificationDialog = ref<InstanceType<typeof FullScreenDialog> | null>(null)

interface _ServerStatus {
  $delegate: ServerStatus
}

interface _GameInfo {
  $delegate: GameInfo
}

const status = ref<_ServerStatus>({
  $delegate: {
    online: null,
    dbStatus: null,
    startupEpochSecond: null,
    profileCount: null,
    serverUrl: null,
    pcbIdRequired: null
  }
})
const games = ref<Map<string, _GameInfo>>(new Map())

fetch(`${config.host}/status`).then(r => r.json()).then(r => {
  status.value.$delegate.online = true
  if (r.result !== -1) {
    status.value.$delegate.dbStatus = r.dbStatus
    status.value.$delegate.startupEpochSecond = r.startupEpochSecond
    status.value.$delegate.profileCount = r.profileCount
    status.value.$delegate.serverUrl = r.serverUrl
    status.value.$delegate.pcbIdRequired = r.pcbIdRequired

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
    alert('Failed to fetch server status: ' + r.message) // TODO: show error
    status.value.$delegate.startupEpochSecond = -1
    status.value.$delegate.profileCount = -1
  }
}).catch(reason => {
  status.value.$delegate.online = false
  status.value.$delegate.dbStatus = false
  status.value.$delegate.startupEpochSecond = -1
  status.value.$delegate.profileCount = -1
  status.value.$delegate.serverUrl = '-'
  console.error(reason) // TODO: show error
})
provide<_ServerStatus>('server-status', status.value)
provide('games', games.value)

const cardId = computed<string>(() => getCookie('cid'))
const cardPin = computed<string>(() => getCookie('p'))
const refId = ref<string | null>(null)

if (cardId.value && cardPin.value) {
  verify(cardId.value, Number(cardPin.value)).then(r => {
    if (r.result !== -1) {
      refId.value = r.refId
    } else {
      alert('Verification failed: ' + r.message) // TODO: show error
    }
  }).catch(e => {
    alert('Verification failed: ' + e) // TODO: show error
  })
}

function handleClickCardInfo () {
  verificationDialog.value?.show()
}

function handleVerificationDialog (index: number, input?: string[], shouldClose?: (r: boolean) => void) {
  if (index === 0 && input) {
    verify(input[0], Number(input[1])).then(r => {
      if (r.result !== -1) {
        refId.value = r.refId
        setCookie('cid', input[0], 365)
        setCookie('p', input[1], 365)
        if (shouldClose) shouldClose(true)
      } else {
        if (refId.value === null) {
          setCookie('cid', input[0], 365)
          setCookie('p', input[1], 365)
        }
        alert('Verification failed: ' + r.message) // TODO: show error
        if (shouldClose) shouldClose(false)
      }
    }).catch(e => {
      alert('Verification failed: ' + e) // TODO: show error
      if (shouldClose) shouldClose(false)
    })
  } else if (shouldClose) shouldClose(true)
}

async function verify (cardId: string, pin: number) {
  return fetch(`${config.host}/verify`, {
    method: 'POST',
    body: JSON.stringify({ cardId, pin })
  }).then(r => r.json())
}

</script>

<style>
@font-face {
  font-family: '_Gilroy Medium';
  src: url('./assets/Gilroy-Medium.otf') format('opentype');
  font-weight: normal;
}

@font-face {
  font-family: '_Gilroy Bold';
  src: url('assets/Gilroy-Bold.otf') format('opentype');
  font-weight: bold;
}

@font-face {
  font-family: '_JetBrains Mono Bold';
  src: url('assets/JetBrainsMono-Bold.ttf') format('truetype');
  font-weight: bold;
}

/*noinspection ALL*/
#app {
  font-family: '_Gilroy Medium', '_Gilroy Bold';
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.card-info {
  display: flex;
  float: right;
  color: gray;
  user-select: none;
  transition: all 0.2s linear;
}

.card-info:hover {
  color: black;
}

nav {
  padding: 35px v-bind(sidePadding);
  border-width: 0 0 1px 0;
  border-style: solid;
  border-color: rgba(169, 169, 169, 0.85);
  background-color: white;
  box-shadow: darkgrey 0 3px 20px 0;
  user-select: none;
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

#root-content {
  padding-left: v-bind(sidePadding);
  padding-right: v-bind(sidePadding);
}

code {
  font-family: '_JetBrains Mono Bold', monospace;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: rgb(20, 85, 254);
  background-color: rgb(144, 202, 249);
  padding: 5px;
  border-width: 1px;
  border-style: solid;
  border-color: #1455fe;
  border-radius: 6px;
}

a {
  color: rgb(20, 114, 254);
  text-decoration-line: underline;
  text-decoration-style: solid;
  text-decoration-color: rgb(144, 202, 249);
  text-decoration-thickness: 2px;
}

</style>
