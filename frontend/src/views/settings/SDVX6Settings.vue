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
  <div>sdvx6 settings</div>
</template>

<script setup lang="ts">

import { computed, inject, reactive, Ref } from 'vue'
import config from '@/config'
import { GameInfo } from '@/props/game-info'
import bgmData from '@/assets/sdvx/bgm.json'

interface AppealCard {
  name: string,
  texture: string
}

interface _GameInfo {
  $delegate: GameInfo
}

const globalPool = inject<Ref<Map<string, unknown>>>('globals')
if (globalPool === undefined) throw new Error('Cannot inject global pool.')

const game = computed(() => {
  const _games = inject<Ref<Map<string, _GameInfo>>>('games')
  if (_games === undefined) throw new Error('games is not injected.')
  return _games.value.get('sdvx6')?.$delegate
})
if (!game.value) throw new Error('Game SDVX6 is not found in server.')

const refId = computed(() => {
  const _refId = inject<Ref<string | null>>('refId')
  if (_refId === undefined) throw new Error('refId is not injected.')
  return _refId.value
})

const data = reactive({
  appealCards: (() => {
    const m = globalPool.value.get('sdvx_appeal_cards') as Map<string, AppealCard> || new Map()

    const api = game.value.otherApi.get('get_appeal_cards')
    if (!api) console.warn('SDVX6 get_appeal_cards api is not found.')

    if (!m.size && api && refId.value) {
      fetch(`${config.host}/${api}?refId=${refId.value}`).then(r => r.json()).then(r => {
        if (r.result !== -1) {
          for (const id in r.data) {
            m.set(id, {
              name: r.data[Number(id)].name,
              texture: r.data[Number(id)].texture
            })
          }
          globalPool.value.set('sdvx_appeal_cards', m)
        } else {
          console.warn(`Error while getting appeal cards: ${r.message}`)
        }
      }).catch(r => {
        console.warn(`Error while getting appeal cards: ${r}`)
      })
    }
    return m
  })(),
  chatStamps: (() => {
    const m = globalPool.value.get('sdvx_chat_stamps') as Map<number, string> || new Map()

    const api = game.value.otherApi.get('get_chat_stamps')
    if (!api) console.warn('SDVX6 get_chat_stamps api is not found.')

    if (!m.size && api && refId.value) {
      fetch(`${config.host}/${api}?refId=${refId.value}`).then(r => r.json()).then(r => {
        if (r.result !== -1) {
          for (const id in r.data) {
            m.set(Number(id), r.data[id])
          }
          globalPool.value.set('sdvx_chat_stamps', m)
        } else {
          console.warn(`Error while getting chat stamps: ${r.message}`)
        }
      }).catch(r => {
        console.warn(`Error while getting chat stamps: ${r}`)
      })
    }
    return m
  })(),
  akaName: (() => {
    const m = globalPool.value.get('sdvx_akanames') as Map<number, string> || new Map()

    const api = game.value.otherApi.get('get_akaname')
    if (!api) console.warn('SDVX6 get_akaname api is not found.')

    if (!m.size && api && refId.value) {
      fetch(`${config.host}/${api}?refId=${refId.value}`).then(r => r.json()).then(r => {
        if (r.result !== -1) {
          for (const id in r.data) {
            m.set(Number(id), r.data[id])
          }
          globalPool.value.set('sdvx_akanames', m)
        } else {
          console.warn(`Error while getting akanames: ${r.message}`)
        }
      }).catch(r => {
        console.warn(`Error while getting akanames: ${r}`)
      })
    }
    return m
  })(),
  nemsys: (() => {
    const m = globalPool.value.get('sdvx_nemsys') as Map<number, string> || new Map()

    const api = game.value.otherApi.get('get_nemsys')
    if (!api) console.warn('SDVX6 get_nemsys api is not found.')

    if (!m.size && api && refId.value) {
      fetch(`${config.host}/${api}?refId=${refId.value}`).then(r => r.json()).then(r => {
        if (r.result !== -1) {
          for (const id in r.data) {
            m.set(Number(id), r.data[id])
          }
          globalPool.value.set('sdvx_nemsys', m)
        } else {
          console.warn(`Error while getting nemsys: ${r.message}`)
        }
      }).catch(r => {
        console.warn(`Error while getting nemsys: ${r}`)
      })
    }
    return m
  })(),
  bgm: new Map(bgmData.bgm.map(v => [v.value, v.name]))
})

const profileSettings = reactive({
  name: '',
  appealCard: 0,
  chatStamp: new Array<number>(4).fill(0),
  akaName: 0,
  nemsys: 0,
  subScreenBackground: 0
})

const customizeGetApi = game.value.api.customize.get
if (customizeGetApi !== null) {
  fetch(`${config.host}/${customizeGetApi}?refId=${refId.value}`).then(r => r.json()).then(r => {
    if (r.result !== -1) {
      profileSettings.name = r.name
      profileSettings.appealCard = r.appeal
      profileSettings.nemsys = r.nemsys
      profileSettings.akaName = r.akaName
      profileSettings.subScreenBackground = r.subbg
      profileSettings.chatStamp = r.stamp
    } else {
      console.warn(`Error while getting profile custom settings: ${r.message}`)
    }
  }).catch(r => {
    console.warn(`Error while getting profile custom settings: ${r}`)
  })
}

</script>

<style scoped>

</style>
