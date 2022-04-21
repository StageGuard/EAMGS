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
  <div class="home">
    <h1>
      <font-awesome-icon icon="server"/>
      <i class="space20px"/>Server Status
    </h1>
    <div id="status-card">
      <div class="status-item-row">
        <div class="status-item-name">Status</div>
        <div class="status-item-value">
          <div class="status-lamp" :style="{
            'background-color': status.$injected.online !== null ? (status.$injected.online ? 'limegreen' : 'darkgray') : 'transparent'
          }"/>
          {{ status.online }}
        </div>
      </div>
      <div class="status-item-row">
        <div class="status-item-name">Database</div>
        <div class="status-item-value">
          <div class="status-lamp" :style="{
            'background-color': status.$injected.dbStatus !== null ? (status.$injected.dbStatus ? 'limegreen' : 'darkgray') : 'transparent'
          }"/>
          {{ status.dbStatus }}
        </div>
      </div>
      <div class="status-item-row">
        <div class="status-item-name">Uptime</div>
        <div class="status-item-value">{{ status.startupTime }}</div>
      </div>
      <div class="status-item-row">
        <div class="status-item-name">Profiles</div>
        <div class="status-item-value">{{ status.profileCount }}</div>
      </div>
    </div>
    <h1>
      <font-awesome-icon icon="user"/>
      <i class="space20px"/>Online Players
    </h1>
    <div id="online-players-card-row" style="display: flex; transition: all 0.2s ease-in-out;">
      <online-player-graph v-for="g in gameInfo" v-bind:key="g.id" :id="g.id" :name="g.name"
                           style="margin: 15px;"/>
    </div>
    <h1>
      <font-awesome-icon icon="info-circle"/>
      <i class="space20px"/>Server Information
    </h1>
    <div id="server-info">
      <div>
        <h3 style="display: inline">Server url :</h3>
        <span class="space20px"/>
        <code>{{ status.$injected.serverUrl }}</code>
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">
import { library } from '@fortawesome/fontawesome-svg-core'
import { faInfoCircle, faServer, faUser } from '@fortawesome/free-solid-svg-icons'
import { computed, inject, reactive, ref } from 'vue'
import { ServerStatus } from '@/props/server-status'
import { GameInfo } from '@/props/game-info'
import OnlinePlayerGraph from '@/components/OnlinePlayersGraph.vue'

library.add(faServer, faUser, faInfoCircle)

interface _ServerStatus {
  $delegate: ServerStatus
}

interface _GameInfo {
  $delegate: GameInfo
}

const _status = inject<_ServerStatus>('server-status')
if (_status === undefined) throw new Error('server-status is not injected.')

const status = reactive({
  $injected: _status.$delegate,
  online: computed<string>(() => {
    return _status.$delegate.online !== null ? (_status.$delegate.online ? 'Online' : 'Offline') : 'Fetching...'
  }),
  dbStatus: computed<string>(() => {
    return _status.$delegate.dbStatus !== null ? (_status.$delegate.dbStatus ? 'Online' : 'Offline') : 'Fetching...'
  }),
  startupTime: (() => {
    const now = ref<number>(new Date().getTime() / 1000)
    if (_status.$delegate.startupEpochSecond !== -1) {
      setInterval(() => {
        now.value = new Date().getTime() / 1000
      }, 1000)
    }
    return computed<string>(() => {
      const sec = _status.$delegate.startupEpochSecond
      return sec ? (sec !== -1 ? calculateTimeDifference(now.value, sec) : '-') : 'Fetching...'
    })
  })(),
  profileCount: computed(() => {
    return _status.$delegate.profileCount ? (_status.$delegate.profileCount !== -1 ? _status.$delegate.profileCount : '-') : 'Fetching...'
  })
})

const games = inject<Map<string, _GameInfo>>('games')
if (games === undefined) throw new Error('games is not injected.')
const gameInfo = computed<{ id: string, name: string }[]>(() => {
  const r: { id: string, name: string }[] = []
  games.forEach((v: _GameInfo) => r.push({ id: v.$delegate.id, name: v.$delegate.name }))
  return r
})

function calculateTimeDifference (now: number, startEpochMilli: number) {
  const diff = now - startEpochMilli / 1000
  const days = Math.floor(diff / 86400)
  const hours = Math.floor((diff % 86400) / 3600)
  const minutes = Math.floor((diff % 3600) / 60)
  const seconds = Math.floor(diff % 60)
  return `${days}d ${hours}h ${minutes}m ${seconds}s`
}
</script>

<style>
.home {
  padding: 20px 50px
}

.space20px {
  margin-left: 10px;
  margin-right: 10px;
}

#status-card {
  background-color: white;
  border-radius: 15px;
  border-style: none;
  width: 400px;
  height: fit-content;
  padding: 20px;
  margin-top: 40px;
  margin-left: 20px;
  margin-bottom: 40px;
}

.status-item-row {
  padding: 15px;
  width: auto;
  height: auto;
  display: flex;
  justify-content: space-between;
}

.status-item-name {
  color: gray;
  margin-right: 50px;
}

.status-item-value {
  display: flex;
  justify-content: center;
  margin-left: 100px;
  align-items: center;
  text-indent: 2px;
}

.status-lamp {
  position: center;
  border-radius: 90px;
  width: 10px;
  height: 10px;
  margin-right: 5px;
  transform: translate(0, -10%);
  transition: all 0.2s ease-in-out;
}

#online-players-card-row {
  margin-left: 5px;
  margin-right: 5px;
}

#server-info {
  margin-left: 35px;
  margin-top: 30px;
}
</style>
