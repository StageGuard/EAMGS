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
  <div id="settings-view">
    <div id="select-games" ref="selectGameSidebar">
      <div class="game-selection" v-for="(g, index) in games" :key="index"
           @click="handleSelectGame(index)">{{ g.name }}
      </div>
    </div>
    <div id="settings-panel">
      <router-view/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { GameInfo } from '@/props/game-info'
import { computed, inject, onMounted, ref } from 'vue'
import { getCookie, setCookie } from '@/utils/cookie'
import router from '@/router'

interface _GameInfo {
  $delegate: GameInfo
}

const games = (() => {
  const _games = inject<Map<string, _GameInfo>>('games')
  if (_games === undefined) throw new Error('games is not injected.')
  return computed<GameInfo[]>(() => {
    const r: GameInfo[] = []
    _games.forEach(v => r.push(v.$delegate))
    return r
  })
})()

const currentSelection = ref<string>(getCookie('sel', 'sdvx6'))
const selectGameSidebar = ref<HTMLDivElement | null>(null)

function activeSidebarItem (index: number) {
  if (index === -1) return
  if (selectGameSidebar.value === null) throw new Error('Cannot handle select game sidebar')
  const sidebarChildren = selectGameSidebar.value.children
  for (let i = 0; i < games.value.length; i++) {
    const item = sidebarChildren.item(i)
    if (index === i) {
      item?.classList.add('game-selection-active')
    } else {
      item?.classList.remove('game-selection-active')
    }
  }
}

onMounted(() => {
  router.push({ name: `settings-${currentSelection.value}` })
  activeSidebarItem(games.value.findIndex(g => g.id === currentSelection.value))
})

function handleSelectGame (index: number) {
  const selected = games.value[index]
  currentSelection.value = selected.id
  setCookie('sel', selected.id, 365)
  activeSidebarItem(index)
  router.push({ name: `settings-${selected.id}` })
}
</script>

<style scoped>
#settings-view {
  padding: 20px 50px 20px 0;
  display: flex;
}

#select-games {
  margin-left: 20px;;
  margin-right: 20px;
}

.game-selection {
  display: block;
  padding: 15px;
  margin-top: 6px;
  margin-bottom: 6px;
  border-radius: 10px;
  text-decoration: none;
  color: rgb(179, 184, 194);
  user-select: none;
  transition: all 0.15s ease-in-out;
}

.game-selection:hover {
  background-color: rgb(199, 204, 213);
  color: white;
}

/*noinspection CssUnusedSymbol*/
.game-selection-active {
  background-color: rgb(20, 85, 254);
  color: white;
}

/*noinspection CssUnusedSymbol*/
.game-selection-active:hover {
  background-color: rgb(20, 85, 254);
  color: white;
}

</style>
