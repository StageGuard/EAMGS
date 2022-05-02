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
  <div v-if="refId !== null && game">sdvx6 settings</div>
</template>

<script setup lang="ts">

import { computed, inject, reactive, Ref } from 'vue'
import config from '@/config'
import { GameInfo } from '@/props/game-info'

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

const refId = computed(() => {
  const _refId = inject<Ref<string | null>>('refId')
  if (_refId === undefined) throw new Error('refId is not injected.')
  return _refId.value
})

const data = reactive({
  appealCards: (() => {
    const m = globalPool.value.get('sdvx_appeal_cards') as Map<string, AppealCard> || new Map()
    if (!game.value) {
      console.error('Game SDVX6 is not found in server.')
      return m
    }

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
    const m = globalPool.value.get('sdvx_chat_stamps') as Map<string, string> || new Map()
    if (!game.value) {
      console.error('Game SDVX6 is not found in server.')
      return m
    }

    const api = game.value.otherApi.get('get_chat_stamps')
    if (!api) console.warn('SDVX6 get_chat_stamps api is not found.')

    if (!m.size && api && refId.value) {
      fetch(`${config.host}/${api}?refId=${refId.value}`).then(r => r.json()).then(r => {
        if (r.result !== -1) {
          for (const id in r.data) {
            m.set(id, r.data[id])
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
    const m = globalPool.value.get('sdvx_akanames') as Map<string, string> || new Map()
    if (!game.value) {
      console.error('Game SDVX6 is not found in server.')
      return m
    }

    const api = game.value.otherApi.get('get_akaname')
    if (!api) console.warn('SDVX6 get_akaname api is not found.')

    if (!m.size && api && refId.value) {
      fetch(`${config.host}/${api}?refId=${refId.value}`).then(r => r.json()).then(r => {
        if (r.result !== -1) {
          for (const id in r.data) {
            m.set(id, r.data[id])
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
    const m = globalPool.value.get('sdvx_nemsys') as Map<string, string> || new Map()
    if (!game.value) {
      console.error('Game SDVX6 is not found in server.')
      return m
    }

    const api = game.value.otherApi.get('get_nemsys')
    if (!api) console.warn('SDVX6 get_nemsys api is not found.')

    if (!m.size && api && refId.value) {
      fetch(`${config.host}/${api}?refId=${refId.value}`).then(r => r.json()).then(r => {
        if (r.result !== -1) {
          for (const id in r.data) {
            m.set(id, r.data[id])
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
  })()
})

</script>

<style scoped>

</style>
