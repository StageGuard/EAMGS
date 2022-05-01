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
  <div id="background" ref="dialogRootContainer" v-show="showing">
    <div id="dialog-body" ref="dialogBody">
      <div id="dialog-title">{{ title }}</div>
      <div id="dialog-desc" v-if="desc !== undefined">{{ desc }}</div>
      <div id="dialog-content" ref="dialogContent" v-if="type === 'input'">
        <div class="input-option" v-for="(inp, index) in input" :key="index">
          <span class="input-name">{{ inp.name }}</span>
          <span class="input-desc" v-if="inp.desc">{{ inp.desc }}</span>
          <input type="text" class="input" name="input" :value="inp.default">
        </div>
      </div>
      <div id="dialog-content" v-else></div>
      <div id="dialog-footer">
        <button class="selection-button" v-for="(item, index) in selectionItems" :key="index"
                @click="handleSelect(index)">
          {{ item }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineEmits, defineExpose, defineProps, ref } from 'vue'
import { lazy } from '@/utils/Lazy'

interface DialogType {
  input: string,
  confirm: string
}

const props = defineProps<{
  initialShow?: boolean
  type: keyof DialogType,
  title: string,
  desc?: string,
  selection?: string[],
  input?: { name: string, desc?: string, default: string }[],
}>()

if (props.type === 'input' && (props.input === undefined || props.input.length === 0)) {
  throw Error('No input options in dialog since type is input.')
}

// eslint-disable-next-line func-call-spacing
const callbackEmits = defineEmits<{
  (e: 'onSelect', index: number, input?: string[], shouldClose?: (r: boolean) => void): void
}>()

const showing = ref<boolean>(props.initialShow ? props.initialShow : false)
const selectionItems = props.selection ? props.selection : ['Apply', 'Cancel']
const dialogBody = ref<HTMLDivElement | null>(null)
const dialogContent = ref<HTMLElement | null>(null)
const dialogRootContainer = ref<HTMLElement | null>(null)

defineExpose({
  show: showDialog,
  dismiss: dismissDialog
})

const inputItems = lazy<HTMLInputElement[] | null>(() => {
  if (!props.input) return null
  if (dialogContent.value === null) return null
  const r: HTMLInputElement[] = []
  // eslint-disable-next-line no-undef
  for (let i = 0; i < props.input.length; i++) {
    const inputOption = dialogContent.value.children.item(i) as HTMLDivElement
    const input = inputOption.children.namedItem('input') as HTMLInputElement
    r.push(input)
  }
  return r
})

function showDialog () {
  showing.value = true
  if (dialogRootContainer.value === null) throw Error('Cannot handle dialog')
  dialogRootContainer.value.animate(
    [{ opacity: 0 }, { opacity: 1 }],
    { duration: 200, easing: 'ease-out' }
  ).play()
}

function dismissDialog () {
  if (dialogRootContainer.value === null) throw Error('Cannot handle dialog')
  const anim = dialogRootContainer.value.animate(
    [{ opacity: 1 }, { opacity: 0 }],
    { duration: 200, easing: 'ease-out' }
  )
  anim.onfinish = () => (showing.value = false)
  anim.play()
}

setTimeout(() => {
  const prevWindowClickEvent = window.onclick
  window.onclick = (ev: MouseEvent) => {
    if (dialogBody.value != null) {
      ev.preventDefault()
      const popupBoxRect = dialogBody.value.getBoundingClientRect()
      if (!(
        (ev.clientX >= popupBoxRect.left && ev.clientX <= popupBoxRect.right) &&
        (ev.clientY >= popupBoxRect.top && ev.clientY <= popupBoxRect.bottom)
      )) handleSelect(-1)
    }
    window.onclick = prevWindowClickEvent
  }
})

function handleSelect (index: number) {
  callbackEmits('onSelect', index, inputItems()?.map(e => e.value), (closeDialog: boolean) => {
    if (closeDialog === undefined) closeDialog = true
    if (index < 0) closeDialog = true

    if (closeDialog) dismissDialog()
  })
}

</script>

<style scoped>

#background {
  display: flex;
  align-content: center;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 999;
  background-color: rgba(0, 0, 0, 40%);
}

#dialog-body {
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  background-color: #fbfbfb;
  border-radius: 15px;
  border-style: none;
  width: 500px;
  height: auto;
  margin: auto;
}

#dialog-title {
  font-weight: bold;
  padding: 20px 15px;
  font-size: 22px;
  border: 1px solid transparent;
  border-bottom-color: lightgray;
}

#dialog-desc {
  font-weight: normal;
  padding: 20px 15px;
  font-size: 18px;
  letter-spacing: normal;
}

#dialog-content {
  font-weight: normal;
  padding: 10px 15px;
  font-size: 18px;
  letter-spacing: normal;
  border: 1px solid transparent;
  border-bottom-color: lightgray;
}

.input-option {
  padding: 10px 5px;
  font-weight: normal;
}

.input-name {
  display: block;
  font-size: 16px;
  letter-spacing: normal;
  margin-bottom: 5px;
}

.input-desc {
  display: block;
  font-size: 14px;
  letter-spacing: normal;
  margin-bottom: 5px;
  color: gray;
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
  letter-spacing: normal;
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

#dialog-footer {
  padding: 20px 15px;
  border: 1px solid transparent;
}

.selection-button {
  float: right;
  margin-bottom: 20px;
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

.selection-button:hover {
  background-color: rgb(21, 88, 199);
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, .2), 0 2px 10px 0 rgba(0, 0, 0, .1);
}

.selection-button:disabled {
  background-color: rgb(101, 155, 245);
}

.selection-button:active {
  background-color: rgb(10, 57, 137);
}

</style>
