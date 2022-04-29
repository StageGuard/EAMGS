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
    <div class="select-games" style="margin-top: 200px">
      <h2 style="margin-right: 20px;">Current selection</h2>
      <select-box
        :options="[
          '739AF21EBFFA4EF0','2CC6EAF30B7B4E85','7B641D6D47C84408','0033ADC1F7964DC7',
          'B2FE53B2F42040B5','44AEFFF08339469E','4A09D33D47714071','50D1E2B0E2EC4C6D',
          'ABC8C9EC740B4671','3675404D37024ADF','C3FBD55B610E481C','C438A123B2614063',
          '26ABB4985EFA48BD','04F04EFF42E74938','2B14E2447BAD4D85','CC9F321B346043DA',
          'FD8349ECB9D24E95','3203134C2B2641F6','96F3CE780AB14A12','A915614281DF49CE',
          'BE79154A59A8492E','ED4A2F642FA347C9','96F42FBFB5AE481A','1163019745224083',
          '0D990B758C4A4943','CEC05DA29F7A475D','64C0CF60D785459A','E3A0C5AD8D3A422A'
        ]"
        :current="0" :show-items-count="9" @on-select="selectGame"></select-box>
    </div>
  </div>
</template>

<script setup type="ts">
import SelectBox from '@/components/SelectBox.vue'

function selectGame (index) {
  console.log(index)
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
