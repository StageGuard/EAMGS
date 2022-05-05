`<!--
 0 - Copyright (c) 2022 StageGuard
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
  <div id="profilePreview" ref="divProfileBg">
    <div id="profilePanel" ref="divProfilePanel">
      <div id="blasterPassBg"/>
      <div id="profileCrew" v-if="data.crew.size && customizeData.crew !== null" ref="divProfileCrew"/>
      <div id="blasterPass"></div>
      <div id="appealCard" v-if="data.appealCards.size && customizeData.appeal !== null" ref="divProfileAppealCard"/>
      <div id="profileBox1">
        <span id="akaName" class="profileFont" v-if="data.akaName.size && customizeData.akaName !== null" ref="divProfileAkaName">{{ computedTexture.akaName }}</span>
        <span id="profileName" class="profileFont" v-if="customizeData.name && customizeData.name !== null" ref="divProfileName">{{ customizeData.name }}</span>
        <span id="shopName" class="profileFont">SG EAG</span>
        <div id="skillBanner" ref="divProfileSkillBanner"/>
        <div id="skillFrame" v-if="skill.level !== 0" ref="divProfileSkillFrame"/>
        <div id="volForceBanner" ref="divProfileVolForceBanner"></div>
        <div id="volForceStarGroup" ref="divProfileVolForceStarGroup">
          <div id="volForceStar" v-for="(_, i) in computedTexture.volForceStar.count" :key="i"/>
        </div>
        <span id="volForceName" class="profileFont" v-html="computedTexture.volForceName" ref="divProfileVolForceName"></span>
        <span id="volForce" class="profileFont" ref="divProfileVolForce">{{ skill.volForce.toFixed(3) }}</span>
      </div>
    </div>
  </div>
  <div id="gamePreview">
    <div id="nemsysPreview" ref="divNemsys"/>
    <div id="chatStampGroup">
      <div v-for="(s, index) in ['A', 'B', 'C', 'D']" :key="index">
        <div style="font-size: 18px; text-align: center; margin: auto auto 15px;">stamp{{ s }}</div>
        <div class="chatStamp" :style="{ 'background-image': computedTexture.stamp[index] }"/>
      </div>
    </div>
  </div>
  <div id="settings">
    <div class="settings-title">Profile name</div>
    <input type="text" class="input" name="input"
           ref="profileNameInput" :value="customizeData.name"
           onchange = "value=value.replace(/[^A-Za-z\d!?#$&*-.\s]{1,8}/g,'')"
           @input="customizeData.name = profileNameInput?.value.replace(/[^A-Za-z\d!?#$&*-.\s]{1,8}/g,'')">
    <div class="settings-title"  v-if="data.appealCards.size && customizeData.appeal !== null">Appeal card</div>
    <select-box v-if="data.appealCards.size && customizeData.appeal !== null"
                :width="580"
                :options="Array.from(data.appealCards.entries())"
                :display="([_, card]) => card.name"
                :current="([id, _]) => id === customizeData.appeal"
                @on-select="(_, v) => customizeData.appeal = v[0]"
    />
    <div class="settings-title" v-if="data.akaName.size && customizeData.akaName !== null">Aka name</div>
    <select-box v-if="data.akaName.size && customizeData.akaName !== null"
                :width="580"
                :options="Array.from(data.akaName.entries())"
                :display="([_, akaName]) => akaName"
                :current="([id, _]) => id === customizeData.akaName"
                @on-select="(_, v) => customizeData.akaName = v[0]"/>
    <div class="settings-title" v-if="data.crew.size && customizeData.crew !== null">Crew (Navigator)</div>
    <select-box v-if="data.crew.size && customizeData.crew !== null"
                :width="580"
                :options="Array.from(data.crew.entries())"
                :display="([_, c]) => c.name"
                :current="([id, _]) => id === customizeData.crew"
                @on-select="(_, v) => customizeData.crew = v[0]"/>
    <div class="settings-title" v-if="customizeData.subbg !== null">Sub monitor background</div>
    <select-box v-if="customizeData.subbg !== null"
                :width="580"
                :options="Array.from(Array(data.subbg).keys())"
                :current="b => b === customizeData.subbg"
                @on-select="b => customizeData.subbg = b"/>
    <div class="settings-title" v-if="data.nemsys.size && customizeData.nemsys !== null">Nemsys</div>
    <select-box v-if="data.nemsys.size && customizeData.nemsys !== null"
                :width="580"
                :options="Array.from(data.nemsys.entries())"
                :display="([_, n]) => n"
                :current="([id, _]) => id === customizeData.nemsys"
                @on-select="(_, v) => customizeData.nemsys = v[0]"/>
    <div v-for="(s, index) in ['A', 'B', 'C', 'D']" :key="index">
      <div class="settings-title" v-if="data.chatStamps.size && customizeData.stamp !== null">Stamp BT-{{ s }}</div>
      <select-box v-if="data.chatStamps.size && customizeData.stamp !== null"
                  :width="580"
                  :options="(() => {
                    const r = Array.from(data.chatStamps.entries())
                    r.unshift([0, 'no_stamp'])
                    return r
                  })()"
                  :display="([i, n]) => i === 0 ? n : n.split('/')[1]"
                  :current="([id, _]) => id === customizeData.stamp[index]"
                  @on-select="(_, v) => customizeData.stamp[index] = v[0]"/>
    </div>
  </div>
  <button class="apply-button" @click="handleApplySettings">Save</button>
  <div style="height: 300px"/>
  <full-screen-dialog
    ref="confirmDialog"
    type="confirm"
    :title="dialogProperty.title"
    :desc="dialogProperty.desc"
    :selection="['Close']"
    @on-select="handleCloseDialog"
  ></full-screen-dialog>
</template>

<script setup lang="ts">

import { computed, inject, reactive, ref, Ref } from 'vue'
import config from '@/config'
import { GameInfo } from '@/props/game-info'
import bgmData from '@/assets/sdvx/bgm.json'
import SelectBox from '@/components/SelectBox.vue'
import FullScreenDialog from '@/components/FullScreenDialog.vue'

interface AppealCard {
  name: string,
  texture: string
}
interface Crew {
  name: string,
  texture: string
}

interface _GameInfo {
  $delegate: GameInfo
}

interface Skill {
  volForce: number,
  level: number,
  passedAllSkillSeason: boolean
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

const divProfileBg = ref<HTMLDivElement | null>(null)
const divProfilePanel = ref<HTMLDivElement | null>(null)
const divProfileCrew = ref<HTMLDivElement | null>(null)
const divProfileAppealCard = ref<HTMLDivElement | null>(null)
const divProfileName = ref<HTMLSpanElement | null>(null)
const divProfileAkaName = ref<HTMLSpanElement | null>(null)
const divProfileSkillBanner = ref<HTMLDivElement | null>(null)
const divProfileSkillFrame = ref<HTMLDivElement | null>(null)
const divProfileVolForceBanner = ref<HTMLDivElement | null>(null)
const divProfileVolForceStarGroup = ref<HTMLDivElement | null>(null)
const divProfileVolForceName = ref<HTMLSpanElement | null>(null)
const divProfileVolForce = ref<HTMLSpanElement | null>(null)
const divNemsys = ref<HTMLSpanElement | null>(null)
const profileNameInput = ref<HTMLInputElement | null>(null)
const confirmDialog = ref<InstanceType<typeof FullScreenDialog> | null>(null)
const dialogProperty = reactive({
  title: '',
  desc: ''
})

const data = reactive({
  appealCards: (() => {
    const m = globalPool.value.get('sdvx_appeal_cards') as Map<number, AppealCard> || new Map()

    const api = game.value.otherApi.get('get_appeal_cards')
    if (!api) console.warn('SDVX6 get_appeal_cards api is not found.')

    if (!m.size && api && refId.value) {
      fetch(`${config.host}/${api}?refId=${refId.value}`).then(r => r.json()).then(r => {
        if (r.result !== -1) {
          for (const id in r.data) {
            m.set(Number(id), {
              name: r.data[Number(id)].name,
              texture: r.data[Number(id)].texture
            })
          }
          globalPool.value.set('sdvx_appeal_cards', m)
          data.appealCards = m
        } else {
          console.warn(`Failed to fetch appeal cards: ${r.message}`)
        }
      }).catch(r => {
        console.warn(`Failed to fetch appeal cards: ${r}`)
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
          data.chatStamps = m
        } else {
          console.warn(`Failed to fetch chat stamps: ${r.message}`)
        }
      }).catch(r => {
        console.warn(`Failed to fetch chat stamps: ${r}`)
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
          data.akaName = m
        } else {
          console.warn(`Failed to fetch akanames: ${r.message}`)
        }
      }).catch(r => {
        console.warn(`Failed to fetch akanames: ${r}`)
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
          data.nemsys = m
        } else {
          console.warn(`Failed to fetch nemsys: ${r.message}`)
        }
      }).catch(r => {
        console.warn(`Failed to fetch nemsys: ${r}`)
      })
    }
    return m
  })(),
  bgm: new Map(bgmData.bgm.map(v => [v.value, v.name])),
  subbg: 129,
  crew: (() => {
    const m = globalPool.value.get('sdvx_crew') as Map<number, Crew> || new Map()

    const api = game.value.otherApi.get('get_crews')
    if (!api) console.warn('SDVX6 get_crews api is not found.')

    if (!m.size && api && refId.value) {
      fetch(`${config.host}/${api}?refId=${refId.value}`).then(r => r.json()).then(r => {
        if (r.result !== -1) {
          for (const id in r.data) {
            m.set(Number(id), {
              name: r.data[id].name,
              texture: r.data[id].texture_id
            })
          }
          globalPool.value.set('sdvx_crew', m)
          data.crew = m
        } else {
          console.warn(`Failed to fetch crews: ${r.message}`)
        }
      }).catch(r => {
        console.warn(`Failed to fetch crews: ${r}`)
      })
    }
    return m
  })()
})

const customizeData = reactive<{
  name: string | null,
  appeal: number | null,
  stamp: number[] | null,
  akaName: number | null,
  nemsys: number | null,
  subbg: number | null,
  crew: number | null,
  bgm: number | null
}>({
  name: null,
  appeal: null,
  stamp: null,
  akaName: null,
  nemsys: null,
  subbg: null,
  crew: null,
  bgm: null
})
const skill = ref<Skill>({
  volForce: 0,
  level: 0,
  passedAllSkillSeason: false
})

// do fetch

if (game.value.api.customize.get !== null) {
  fetch(`${config.host}/${game.value.api.customize.get}?refId=${refId.value}`).then(r => r.json()).then(r => {
    if (r.result !== -1) {
      customizeData.name = r.name
      customizeData.appeal = r.appeal || 5001 // g6 initial ap card default
      customizeData.nemsys = r.nemsys
      customizeData.akaName = r.akaName || 10001 // default
      customizeData.subbg = r.subbg
      customizeData.stamp = r.stamp
      customizeData.crew = r.crew || 113 // g6 rasis default
      customizeData.bgm = r.bgm
    } else {
      console.warn(`Failed to fetch profile custom settings: ${r.message}`)
    }
  }).catch(r => {
    console.warn(`Failed to fetch profile custom settings: ${r}`)
  })
}

if (game.value.api.profile !== null) {
  fetch(`${config.host}/${game.value.api.profile}?refId=${refId.value}`).then(r => r.json()).then(r => {
    if (r.result !== -1) {
      skill.value.level = r.skillLevel
      skill.value.passedAllSkillSeason = r.passedAllSkillSeason
    } else {
      console.warn(`Failed to fetch profile: ${r.message}`)
    }
  }).catch(r => {
    console.warn(`Failed to fetch profile: ${r}`)
  })

  const queryVfApi = game.value.otherApi.get('query_vol_force')
  if (!queryVfApi) console.warn('SDVX6 query_vol_force api is not found.')
  fetch(`${config.host}/${queryVfApi}?refId=${refId.value}`).then(r => r.json()).then(r => {
    if (r.result !== -1) {
      skill.value.volForce = r.volForce
    } else {
      console.warn(`Failed to fetch vol force: ${r.message}`)
    }
  }).catch(r => {
    console.warn(`Failed to fetch vol force: ${r}`)
  })
}

const computedTexture = reactive({
  subBg: computed(() => {
    const id = (customizeData.subbg || 0).toString().padStart(4, '0')
    const url = `${config.assetsHost}/sdvx/submonitor_bg/subbg_${id}.png`
    if (divProfileBg.value !== null) {
      divProfileBg.value.animate(
        [{ opacity: 0 }, { opacity: 1 }],
        { easing: 'ease-in-out', duration: 250 }
      ).play()
    }
    return `url("${url}")`
  }),
  crew: computed(() => {
    const crewData = data.crew.get(customizeData.crew || 113)
    const id = crewData ? crewData.texture.toString().padStart(4, '0') : '0014'
    const url = `${config.assetsHost}/sdvx/psd_crew/psd_crew_${id}.png`
    if (divProfileCrew.value !== null) {
      divProfileCrew.value.animate(
        [{ opacity: 0 }, { opacity: 1 }],
        { easing: 'ease-in-out', duration: 150 }
      ).play()
    }
    return `url("${url}")`
  }),
  appealCard: computed(() => {
    const appeal = data.appealCards.get(customizeData.appeal || 5001)
    const texture = appeal ? appeal.texture : 'ap_06_0001'
    const url = `${config.assetsHost}/sdvx/ap_card/${texture}.png`
    if (divProfileAppealCard.value !== null) {
      divProfileAppealCard.value.animate(
        [{ opacity: 0 }, { opacity: 1 }],
        { easing: 'ease-in-out', duration: 150 }
      ).play()
    }
    return `url("${url}")`
  }),
  akaName: computed(() => {
    const akaName = data.akaName.get(customizeData.akaName || 10001)
    if (divProfileAkaName.value !== null) {
      divProfileAkaName.value.animate(
        [{ opacity: 0 }, { opacity: 1 }],
        { easing: 'ease-in-out', duration: 150 }
      ).play()
    }
    return akaName || 'よろしくお願いします' // default
  }),
  skillBanner: computed(() => {
    const level = skill.value.level
    if (divProfileSkillBanner.value !== null) {
      divProfileSkillBanner.value.animate(
        [{ opacity: 0 }, { opacity: 1 }],
        { easing: 'ease-in-out', duration: 150 }
      ).play()
    }
    return `url("${config.assetsHost}/sdvx/skill/skill_${level ? level.toString().padStart(2, '0') : 'none'}.png")`
  }),
  skillFrame: computed(() => {
    const p = skill.value.passedAllSkillSeason
    if (divProfileSkillFrame.value !== null) {
      divProfileSkillFrame.value.animate(
        [{ opacity: 0 }, { opacity: 1 }],
        { easing: 'ease-in-out', duration: 150 }
      ).play()
    }
    return `url("${config.assetsHost}/sdvx/skill/skill_frame${p ? '' : '_nomal'}.png")`
  }),
  volForceBanner: computed(() => {
    const bannerIndex = Math.max(1, Math.floor(Math.max(0, skill.value.volForce - 10.0)))
    if (divProfileVolForceBanner.value !== null) {
      divProfileVolForceBanner.value.animate(
        [{ opacity: 0 }, { opacity: 1 }],
        { easing: 'ease-in-out', duration: 150 }
      ).play()
    }
    return `url("${config.assetsHost}/sdvx/force/em6_s${bannerIndex.toString().padStart(2, '0')}_i_eab.png")`
  }),
  volForceStar: computed(() => {
    const bannerIndex = Math.max(1, Math.floor(Math.max(0, skill.value.volForce - 10.0)))
    if (divProfileVolForceStarGroup.value !== null) {
      divProfileVolForceStarGroup.value.animate(
        [{ opacity: 0 }, { opacity: 1 }],
        { easing: 'ease-in-out', duration: 150 }
      ).play()
    }
    if (bannerIndex <= 5) {
      return {
        count: new Array<number>(Math.max(1, bannerIndex)).fill(0),
        texture: `url("${config.assetsHost}/sdvx/force/star_silver_i_eab.png")`
      }
    } else {
      return {
        count: new Array<number>(Math.min(5, Math.max(1, bannerIndex - 5))).fill(0),
        texture: `url("${config.assetsHost}/sdvx/force/star_gold_i_eab.png")`
      }
    }
  }),
  volForceName: computed(() => {
    const mapping = [
      ['SIENNA', '#c07b24'],
      ['COBALT', '#315eff'],
      ['DANDELION', '#ffc21f'],
      ['CYAN', '#32fce0'],
      ['SCARLET', '#ff1717'],
      ['CORAL', '#ff638e'],
      ['ARGENTO', '#bfc6cb'],
      ['ELDORA', '#f3d63a'],
      ['CRIMSON', '#ff0504'],
      ['IMPERIAL', '#b159ff']
    ]
    const index = Math.max(0, Math.floor(Math.max(0, skill.value.volForce - 11.0)))
    if (divProfileVolForceName.value !== null) {
      divProfileVolForceName.value.animate(
        [{ opacity: 0 }, { opacity: 1 }],
        { easing: 'ease-in-out', duration: 150 }
      ).play()
    }
    return `VOLFORCE</br><span style="color: ${mapping[index][1]}">${mapping[index][0]}</span>`
  }),
  nemsys: computed(() => {
    const texture = data.nemsys.get(customizeData.nemsys || 0) || 'nemsys_0000'
    if (divNemsys.value !== null) {
      divNemsys.value.animate(
        [{ opacity: 0 }, { opacity: 1 }],
        { easing: 'ease-in-out', duration: 250 }
      ).play()
    }
    return `url("${config.assetsHost}/sdvx/nemsys/${texture}.png")`
  }),
  stamp: computed(() => {
    const stamps = (customizeData.stamp || [0, 0, 0, 0]).map(v => data.chatStamps.get(v) || 'no_stamp')
    return stamps.map(s => {
      return `url("${config.assetsHost}/sdvx/chat_stamp/${s}.png")`
    })
  })
})

function handleCloseDialog (index: number, input?: string[], shouldClose?: (r: boolean) => void) {
  if (shouldClose) shouldClose(true)
}

function handleApplySettings () {
  if (!game.value) throw new Error('Game is not injected.')
  if (game.value.api.customize.update !== null) {
    fetch(`${config.host}/${game.value.api.customize.update}?refId=${refId.value}`, {
      method: 'POST',
      body: JSON.stringify(customizeData)
    }).then(r => r.json()).then(r => {
      if (r.result !== -1) {
        dialogProperty.title = 'Success'
        dialogProperty.desc = 'Update your profile settings successfully.'
        confirmDialog.value?.show()
      } else {
        dialogProperty.title = 'Failure'
        dialogProperty.desc = `Failed to update profile settings: ${r.message}`
        console.warn(`Failed to update profile settings: ${r.message}`)
        confirmDialog.value?.show()
      }
    }).catch(r => {
      dialogProperty.title = 'Failure'
      dialogProperty.desc = `Failed to update profile settings: ${r}`
      console.warn(`Failed to update profile settings: ${r}`)
      confirmDialog.value?.show()
    })
  }
}

</script>

<!--suppress CssUnknownTarget -->
<style scoped>
#profilePreview {
  width: 850px;
  height: 496px;
  display: flex;
  background-image: v-bind(computedTexture.subBg);
  background-repeat:no-repeat;
  background-size: 850px;
}

#profilePanel {
  position: relative;
  margin: auto;
  width: 700px;
  height: 300px;
  background-size: 700px;
  top: -15px;
  background-image: url("@/assets/sdvx/profile/box_playdata.png");
  background-repeat: no-repeat;
  background-position-y: 78px;
}

#profileCrew {
  position: absolute;
  background-image: v-bind(computedTexture.crew);
  background-repeat:no-repeat;
  background-size: 408px;
  width: 408px;
  height: 283px;
  left: 289px;
  top: 8px
}

#blasterPass {
  position: absolute;
  background-image: url("@/assets/sdvx/profile/blpass_on.png");
  background-repeat: no-repeat;
  background-size: 244px;
  width: 244px;
  height: 69px;
  left: 435px;
  top: 195px;
}

#blasterPassBg {
  position: absolute;
  background-image: url("@/assets/sdvx/profile/blpass_bg.png");
  background-repeat: no-repeat;
  background-size: 322px;
  width: 322px;
  height: 130px;
  left: 374px;
  top: 137px;
}

#appealCard {
  position: absolute;
  background-image: v-bind(computedTexture.appealCard);
  background-repeat: no-repeat;
  background-size: 100px;
  width: 100px;
  height: 140px;
  left: 41px;
  top: 125px;
}

#profileBox1 {
  position: relative;
  background-image: url("@/assets/sdvx/profile/box_playdata2.png");
  width: 252px;
  height: 152px;
  background-size: 252px;
  left: 163px;
  top: 113px
}

.profileFont {
  font-family: '_Continuum Medium', '_DFHSMaruGothic W4 Reform', sans-serif;
  user-select: none;
  color: white;
}

#akaName {
  position: absolute;
  font-size: 14px;
  left: 8px;
  top: 7px;
}

#profileName {
  position: absolute;
  font-size: 26px;
  left: 8px;
  top: 31px;
}

#shopName {
  position: absolute;
  font-size: 14px;
  left: 8px;
  top: 71px;
}

#skillBanner {
  position: absolute;
  background-image: v-bind(computedTexture.skillBanner);
  background-repeat: no-repeat;
  background-size: 103px;
  width: 103px;
  height: 50px;
  left: 11px;
  top: 111px;
}

#skillFrame {
  position: absolute;
  background-image: v-bind(computedTexture.skillFrame);
  background-repeat: no-repeat;
  background-size: 135px;
  width: 135px;
  height: 150px;
  left: -5px;
  top: 84px;
}

#volForceBanner {
  position: absolute;
  background-image: v-bind(computedTexture.volForceBanner);
  background-repeat: no-repeat;
  background-size: 46px;
  width: 46px;
  height: 150px;
  left: 134px;
  top: 95px;
}

#volForceStarGroup {
  display: flex;
  justify-content: center;
  position: absolute;
  width: 50px;
  height: 9px;
  left: 132px;
  top: 134px;
}

#volForceStar {
  background-image: v-bind(computedTexture.volForceStar.texture);
  background-size: 9px;
  width: 9px;
  margin: 0;
}

#volForceName {
  position: absolute;
  font-size: 8px;
  line-height: 11px;
  left: 175px;
  top: 107px;
}

#volForce {
  position: absolute;
  font-size: 15px;
  left: 174px;
  top: 128px;
}

#gamePreview {
  height: 224px;
  display: flex;
  justify-content: center;
  margin-top: 25px;
}

#nemsysPreview {
  background-image: v-bind(computedTexture.nemsys);
  background-repeat: no-repeat;
  background-size: 425px;
  width: 425px;
  height: 224px;
}

#chatStampGroup {
  display: flex;
  height: 224px;
  align-items: center;
  justify-content: center;
}

.chatStamp {
  background-size: 100px;
  width: 100px;
  height: 100px;
  vertical-align: center;
  margin-left: 4px;
  margin-right: 4px;
}

#settings {
  padding: 20px;
  margin-top: 15px;
}

.settings-title {
  font-size: 20px;
  display: block;
  margin-top: 25px;
  margin-bottom: 7px
}

.input {
  appearance: none;
  background-clip: padding-box;
  border: 1px solid rgb(189, 189, 189);
  background-color: rgb(255, 255, 255);
  border-image-repeat: stretch;
  border-image-source: none;
  border-radius: 4px;
  box-sizing: border-box;
  color: rgb(79, 79, 79);
  cursor: text;
  display: block;
  font-family: '_Gilroy Medium', serif;
  font-size: 16px;
  font-stretch: 100%;
  font-style: normal;
  font-variant: normal;
  font-weight: 400;
  letter-spacing: 1px;
  line-height: 34px;
  min-height: 0;
  overflow-wrap: break-word;
  padding: 4px 16px 3px;
  text-align: start;
  text-indent: 0;
  text-rendering: auto;
  text-shadow: none;
  text-size-adjust: 100%;
  text-transform: none;
  transition: all 0.1s linear;
  width: 390px;
  height: 45px;
  word-spacing: 0;
  writing-mode: horizontal-tb;
  -webkit-box-direction: normal;
  -webkit-rtl-ordering: logical;
  -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
  -webkit-border-image: none;
}

.input:focus {
  border-color: #1266f1;
  border-width: 2px;
  outline: none !important;
}

.apply-button {
  margin-top: 40px;
  outline: none;
  background-color: rgb(18, 102, 241);
  border: none;
  border-radius: 5px;
  box-shadow: 0 2px 5px 0 rgba(0, 0, 0, .2), 0 2px 10px 0 rgba(0, 0, 0, .1);
  color: white;
  font-family: '_Gilroy Medium', serif;
  margin-left: 10px;
  margin-right: 10px;
  padding: 10px 24px 8px;
  transition: all 0.1s linear;
}

.apply-button:hover {
  background-color: rgb(21, 88, 199);
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, .2), 0 2px 10px 0 rgba(0, 0, 0, .1);
}

.apply-button:disabled {
  background-color: rgb(101, 155, 245);
}

.apply-button:active {
  background-color: rgb(10, 57, 137);
}
</style>
