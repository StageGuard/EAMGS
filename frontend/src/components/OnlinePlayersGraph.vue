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
  <div class="root-card">
    <canvas :id="`online-player-graph-${id}`"></canvas>
  </div>
</template>

<script setup lang="ts">
import { Chart, ScriptableContext } from 'chart.js'
import { computed, defineProps, onMounted, ref, watch } from 'vue'
import config from '@/config'

const gameInfo = defineProps<{
  id: string,
  name: string
}>()

const data = ref<number[]>([])

const labels = computed(() => {
  const r = ['-1h', 'now']
  if (data.value.length <= 2) return r
  for (let i = 2; i < data.value.length; i++) {
    r.unshift(`-${i}h`)
  }
  return r
})

const normalizedData = computed(() => {
  const r = Array.from(data.value)
  if (r.length < 2) {
    for (let i = 0; i < 2 - r.length; i++) {
      r.unshift(0)
    }
  }
  return r
})

onMounted(() => {
  const ctx = (() => {
    const canvas = document.getElementById(`online-player-graph-${gameInfo.id}`)
    if (!canvas) throw new Error(`Canvas with id ${gameInfo.id} not found.`)

    const context = (canvas as HTMLCanvasElement).getContext('2d')
    if (!context) throw new Error(`Cannot get 2d context of canvas ${gameInfo.id}.`)

    return context
  })()

  const totalDuration = 1000
  const delayBetweenPoints = totalDuration / labels.value.length
  // eslint-disable-next-line
  const previousY = (ctx: any) => ctx.index === 0 ? ctx.chart.scales.y.getPixelForValue(100) : ctx.chart.getDatasetMeta(ctx.datasetIndex).data[ctx.index - 1].getProps(['y'], true).y

  const chart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels.value,
      datasets: [{
        data: normalizedData.value,
        borderColor: 'rgb(20, 85, 254)',
        borderWidth: 2,
        pointRadius: 0,
        fill: {
          target: 'origin',
          above (ctx: ScriptableContext<'line'>) {
            return createLineGradient(ctx.chart)
          }
        },
        tension: 0.2
      }]
    },
    options: {
      responsive: true,
      scales: {
        x: {
          grid: {
            display: false
          }
        },
        y: {
          grid: {
            display: false
          },
          suggestedMin: 0,
          ticks: {
            stepSize: 1
          }
        }
      },
      interaction: {
        mode: 'nearest',
        axis: 'x',
        intersect: false
      },
      animations: {
        x: {
          type: 'number',
          easing: 'easeOutCubic',
          duration: delayBetweenPoints,
          from: NaN,
          delay (ctx: { type: string, xStarted: boolean, index: number }) {
            if (ctx.type !== 'data' || ctx.xStarted) {
              return 0
            }
            ctx.xStarted = true
            return ctx.index * delayBetweenPoints
          }
        },
        y: {
          type: 'number',
          easing: 'easeOutCubic',
          duration: delayBetweenPoints,
          from: previousY,
          delay (ctx: { type: string, yStarted: boolean, index: number }) {
            if (ctx.type !== 'data' || ctx.yStarted) {
              return 0
            }
            ctx.yStarted = true
            return ctx.index * delayBetweenPoints
          }
        }
      },
      plugins: {
        legend: {
          display: false,
          position: 'bottom'
        },
        title: {
          display: true,
          position: 'bottom',
          text: gameInfo.name
        }
      }
    }
  })

  watch(labels, () => {
    chart.data.labels = labels.value
    chart.update()
  })
  watch(normalizedData, () => {
    chart.data.datasets[0].data = normalizedData.value
    chart.update()
  })
})

fetch(`${config.host}/online?game=${gameInfo.id}`)
  .then(res => res.json())
  .then(res => {
    if (res.result !== -1) {
      data.value = res.record
    } else {
      console.log(`Failed to fetch online players for game ${gameInfo.id}: ${res.message}`)
    }
  }).catch(err => {
    console.log(`Failed to fetch online players for game ${gameInfo.id}: ${err}`)
  })

function createLineGradient (chart: Chart, primaryColor = 'rgb(20,172,254)'): CanvasGradient {
  const gradient = chart.ctx.createLinearGradient(0, 0, 0, chart.height)
  gradient.addColorStop(0, primaryColor)
  gradient.addColorStop(0.5, 'rgb(255, 255, 255, 0)')
  return gradient
}
</script>

<style scoped>
.root-card {
  background-color: white;
  border-radius: 15px;
  box-shadow: lightgray 0 0 5px 0;
  border-style: none;
  width: 500px;
  height: 225px;
  padding: 20px;
  transition: all 0.2s ease-in-out;
}

.root-card:hover {
  box-shadow: darkgray 0 0 12px 0;
  transition: all 0.2s ease-in-out;
}
</style>
