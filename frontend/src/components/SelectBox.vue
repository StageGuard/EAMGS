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
  <div id="select-box" class="box-style" @click="openSelectBox()" ref="selectBox">
    <span style="margin: auto; user-select: none">{{ options[currentSelection] }}</span>
  </div>
  <div id="popup-box" class="box-style" ref="popupBox" v-show="selectBoxOpened">
    <div v-for="(item, index) in options" id='' :key="index" class="box-item" :class="{
        'box-item-singleton': options.length === 1,
        'box-item-head': index === 0,
        'box-item-tail': index === options.length - 1
      }" @click="handleSelect(index)">
      <span id="text" style="margin: auto; user-select: none">{{ item }}</span>
    </div>
  </div>
</template>

<!--suppress JSUnusedGlobalSymbols -->
<script setup lang="ts">
import { computed, defineEmits, defineProps, ref } from 'vue'
import $ from 'jquery'
import { lazy } from '@/Lazy'

const props = defineProps<{
  options: string[],
  current: number,
  showItemsCount?: number
}>()

if (props.options.length < 2) throw Error('Select box should contains more than 1 option')
if (props.current > props.options.length - 1) throw Error('Current selection is out of index of options')

// count of items showing in select box, default: 5
const showItemsCount = (() => {
  const opCount = props.showItemsCount ? props.showItemsCount : 5
  return Math.min(opCount, props.options.length)
})()

const callbackEmit = defineEmits<{(e: 'onSelect', index: number): void }>()

const popupBox = ref<HTMLDivElement | null>(null)
const selectBox = ref<HTMLDivElement | null>(null)
const selectBoxOpened = ref<boolean>(false)
const currentSelection = ref<number>(props.current)
let animPlaying = false
const animDuration = 150

/*
 * index of current showing select box
 * e.g. if at top, index equals to 0; if at bottom, index equals to `showItemsCount - 1`
 */
const currentShowingSlot = computed<number>(() => {
  // option count less than or equal to 5
  if (props.options.length <= showItemsCount) {
    return currentSelection.value
  } else {
    const midIndex = Math.floor(showItemsCount / 2) - (showItemsCount % 2 === 0 ? 1 : 0)
    if (currentSelection.value <= midIndex) {
      return currentSelection.value
    } else if (props.options.length - currentSelection.value - 1 <= midIndex) {
      return showItemsCount - (props.options.length - currentSelection.value)
    } else {
      return midIndex
    }
  }
})

const selectBoxHeight = lazy(() => {
  if (selectBox.value == null) throw new Error('Cannot handle select element of select box')
  const selectBoxRect = selectBox.value.getBoundingClientRect()
  const selectBoxHeight = Math.abs(selectBoxRect.bottom - selectBoxRect.top)

  // lazily set select item height
  for (let i = 0; i < props.options.length; i++) {
    if (popupBox.value == null) throw new Error('Cannot handle popup element of select box')
    const item = popupBox.value.children.item(i) as HTMLDivElement | null
    if (item != null) item.style.height = `${selectBoxHeight}px`
  }

  return selectBoxHeight
})

function setItemWidth (w: number) {
  for (let i = 0; i < props.options.length; i++) {
    if (popupBox.value == null) throw new Error('Cannot handle popup element of select box')
    const item = popupBox.value.children.item(i) as HTMLDivElement | null
    if (item != null) item.style.width = `${w}px`
  }
}

function openSelectBox () {
  if (selectBox.value == null) throw new Error('Cannot handle select element of select box')
  if (popupBox.value == null) throw new Error('Cannot handle popup element of select box')
  if (animPlaying) return

  const selectBoxRect = selectBox.value.getBoundingClientRect()
  const selectBoxWidth = Math.abs(selectBoxRect.right - selectBoxRect.left)
  setItemWidth(selectBoxWidth)

  if (!selectBoxOpened.value) {
    const selectBoxRect = selectBox.value.getBoundingClientRect()
    popupBox.value.style.left = `${selectBoxRect.left}px`
    selectBoxOpened.value = true
    selectBox.value.style.opacity = '0'

    const popupBoxTop = selectBoxRect.top - selectBoxHeight() * currentShowingSlot.value

    const openAnim = popupBox.value.animate(
      [{ top: `${selectBoxRect.top}px`, height: `${selectBoxHeight()}px` },
        { top: `${popupBoxTop}px`, height: `${selectBoxHeight() * showItemsCount}px` }],
      { easing: 'ease-out', duration: animDuration })
    openAnim.onfinish = () => {
      if (popupBox.value == null) throw new Error('Cannot handle popup element of select box')
      popupBox.value.style.top = `${popupBoxTop}px`
      popupBox.value.style.height = `${selectBoxHeight() * showItemsCount}px`
      animPlaying = false
    }
    // play animation
    $({ _: 0 }).animate({ _: 1 }, {
      duration: animDuration,
      step () {
        if (popupBox.value == null) throw Error('Cannot handle popup element of select box')
        const popupBoxTop = popupBox.value.getBoundingClientRect().top
        popupBox.value.scrollTop = currentSelection.value * selectBoxHeight() + (popupBoxTop - selectBoxRect.top)
      }
    })
    openAnim.play()

    for (let i = 1; i < showItemsCount; i++) {
      if (currentShowingSlot.value - i >= 0) {
        const lastElement = popupBox.value.children.item(currentSelection.value - i) as HTMLDivElement | null
        if (lastElement != null) {
          lastElement.style.opacity = '0'
          const anim = lastElement.animate(
            [{ opacity: '0' }, { opacity: '1' }],
            { duration: 150, easing: 'ease-in', delay: 50 * (i + 1) }
          )
          anim.onfinish = () => {
            lastElement.style.opacity = '1'
          }
          lastElement.animate(
            [{ top: '8px' }, { top: '0' }],
            { duration: 100, easing: 'ease-out', delay: 50 * (i + 1) }
          ).play()
          anim.play()
        }
      }
      if (currentShowingSlot.value - i < showItemsCount) {
        const nextElement = popupBox.value.children.item(currentSelection.value + i) as HTMLDivElement | null
        if (nextElement != null) {
          nextElement.style.opacity = '0'
          const anim = nextElement.animate(
            [{ opacity: '0' }, { opacity: '1' }],
            { duration: 150, easing: 'ease-in', delay: 50 * (i + 1) }
          )
          anim.onfinish = () => {
            nextElement.style.opacity = '1'
          }
          nextElement.animate(
            [{ top: '-8px' }, { top: '0' }],
            { duration: 100, easing: 'ease-out', delay: 50 * (i + 1) }
          ).play()
          anim.play()
        }
      }
    }

    setTimeout(() => {
      const prevWindowClickEvent = window.onclick
      window.onclick = (ev: MouseEvent) => {
        if (selectBoxOpened.value && popupBox.value != null) {
          ev.preventDefault()
          const popupBoxRect = popupBox.value.getBoundingClientRect()
          if (!(
            (ev.clientX >= popupBoxRect.left && ev.clientX <= popupBoxRect.right) &&
            (ev.clientY >= popupBoxRect.top && ev.clientY <= popupBoxRect.bottom)
          )) handleSelect(-1)
        }
        window.onclick = prevWindowClickEvent
      }
    })

    animPlaying = true
  }
}

function handleSelect (index: number) {
  if (selectBox.value == null) throw new Error('Cannot handle select element of select box')
  if (popupBox.value == null) throw new Error('Cannot handle popup element of select box')
  if (animPlaying) return

  const selectBoxRect = selectBox.value.getBoundingClientRect()
  selectBox.value.style.opacity = '1'

  if (index >= 0) {
    currentSelection.value = index
    callbackEmit('onSelect', currentSelection.value)
  }
  animPlaying = true
  const closeAnim = popupBox.value.animate(
    [{ opacity: '1' }, { opacity: '0' }],
    { easing: 'ease-in-out', duration: animDuration }
  )
  closeAnim.onfinish = () => {
    if (popupBox.value == null) throw new Error('Cannot handle popup element of select box')
    popupBox.value.style.top = `${selectBoxRect.top}px`
    selectBoxOpened.value = false
    animPlaying = false
  }
  closeAnim.play()
}
</script>

<style scoped>

.box-style {
  border-radius: 10px;
  box-shadow: lightgray 0 0 5px 0;
  border-width: 1px;
  background-color: white;
}

.box-style:hover {
  box-shadow: lightgray 0 0 10px 0;
}

#select-box {
  display: flex;
  align-items: center;
  padding: 15px
}

#popup-box {
  position: absolute;
  z-index: 999;
  top: 0;
  left: 0;
  width: auto;
  overflow-y: scroll;
  -ms-overflow-style: none;
  /*noinspection CssUnknownProperty*/
  scrollbar-width: none;
}

#popup-box::-webkit-scrollbar {
  display: none;
}

.box-item {
  display: flex;
  align-items: center;
  position: relative;
  background-color: white;
  transition: background-color 0.2s ease-in-out;
}

.box-item:hover {
  background-color: #eaeaea;
}

.box-item-head {
  border-radius: 10px 10px 0 0;
}

.box-item-tail {
  border-radius: 0 0 10px 10px;
}

.box-item-singleton {
  border-radius: 10px;
}
</style>
