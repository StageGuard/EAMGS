<template>
  <div class="home">
    <h1><font-awesome-icon icon="server"/><i class="space20px"/>Server Status</h1>
    <div id="status-card">
      <div class="status-item-row">
        <div class="status-item-name">Status</div>
        <div class="status-item-value">
          <div class="status-lamp" :style="{
            'background-color': status.online !== null ? (status.online ? 'limegreen' : 'darkgray') : 'transparent'
          }"/>{{ status.online !== null ? (status.online ? "Online" : "Offline") : "Fetching..." }}
        </div>
      </div>
      <div class="status-item-row">
        <div class="status-item-name">Uptime</div>
        <div class="status-item-value">{{
            status.startupEpochSecond ? (status.startupEpochSecond !== -1 ? startupFormatted : "-") : "Fetching..."
          }}</div>
      </div>
      <div class="status-item-row">
        <div class="status-item-name">Profiles</div>
        <div class="status-item-value">{{ status.profileCount ? (status.profileCount !== -1 ? status.profileCount : "-") : "Fetching..." }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { library } from '@fortawesome/fontawesome-svg-core'
import { faServer } from '@fortawesome/free-solid-svg-icons'
import { ref } from "vue";
import config from "../config"
import {setup} from "vue-class-component";

library.add(faServer)

const status = ref<{
  online: Boolean | null,
  startupEpochSecond: number | null,
  profileCount: number | null
}>({ online: null, startupEpochSecond: null, profileCount: null })
const startupFormatted = ref<string>("")
const games = ref()

function calculateTimeDifference(startEpochMilli: number) {
  const now = new Date().getTime() / 1000
  const diff = now - startEpochMilli / 1000
  const days = Math.floor(diff / 86400)
  const hours = Math.floor((diff % 86400) / 3600)
  const minutes = Math.floor((diff % 3600) / 60)
  const seconds = Math.floor(diff % 60)
  return `${days}d ${hours}h ${minutes}m ${seconds}s`
}

fetch(`${config.host}/status`).then(r => r.json()).then(r => {
  games.value = r.games
  status.value.online = true
  if (r.result !== -1) {
    status.value.startupEpochSecond = r.startupEpochSecond;
    status.value.profileCount = r.profileCount;
    startupFormatted.value = calculateTimeDifference(status.value.startupEpochSecond!)
    setInterval(() => startupFormatted.value = calculateTimeDifference(status.value.startupEpochSecond!), 1000)
  } else {
    status.value.startupEpochSecond = -1
    status.value.profileCount = -1
  }
}).catch(reason => {
  status.value.online = false
  status.value.startupEpochSecond = -1
  status.value.profileCount = -1
  console.log(reason)
})

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
  box-shadow: darkgray 0 0 10px 0;
  border-style: none;
  width: 400px;
  height: fit-content;
  padding: 20px;
  margin-top: 40px;
  margin-left: 20px;
  transition: all 0.2s ease-in-out;
}

#status-card:hover {
  box-shadow: darkgray 0 0 25px 0;
  transition: all 0.2s ease-in-out;
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
</style>
