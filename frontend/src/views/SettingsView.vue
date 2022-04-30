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
  <div class="settings-view">
    <div class="select-games">
      <h2 style="margin-right: 20px;">Current game selection</h2>
      <select-box
        :options="games.map(g => g.name)"
        :current="games.findIndex(g => g.id === getCookie('sel', 'sdvx6'))" :show-items-count="2"
        @on-select="handleSelectGame"
      />
    </div>
    <div id="settings-panel">
      <router-view/>
    </div>
  </div>
</template>

<script setup lang="ts">
import SelectBox from '@/components/SelectBox.vue'
import { GameInfo } from '@/props/game-info'
import { computed, inject, onMounted } from 'vue'
import getCookie from '@/utils/cookie'
import router from '@/router'

interface _GameInfo {
  $delegate: GameInfo
}

const games = (() => {
  const _games = inject<Map<string, _GameInfo>>('games')
  if (_games === undefined) throw new Error('games is not injected.')
  return computed<GameInfo[]>(() => {
    const r: GameInfo[] = []
    _games.forEach((v: _GameInfo) => r.push(v.$delegate))
    return r
  })
})()

onMounted(() => {
  const currentSelection = getCookie('sel', 'sdvx6')
  router.push({ name: `settings-${currentSelection}` })
})

function handleSelectGame (index: number) {
  document.cookie = `sel=${games.value[index].id}`
  router.push({ name: `settings-${games.value[index].id}` })
}
</script>

<style scoped>
.settings-view {
  padding: 20px 50px;
}

.select-games {
  margin-left: 20px;
  display: flex;
  align-items: center;
}
</style>
