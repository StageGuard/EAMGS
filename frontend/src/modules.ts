/*
 * Copyright (c) 2022 StageGuard
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

interface GameModule {
  gameId: string,
  settingRoute: Promise<InstanceType<never>>
}

const definedGameModules: GameModule[] = [
  {
    gameId: 'sdvx6',
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    settingRoute: () => import(/* webpackChunkName: "settings.sdvx6" */ '@/views/settings/SDVX6Settings.vue')
  },
  {
    gameId: 'iidx',
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    settingRoute: () => import(/* webpackChunkName: "settings.iidx" */ '@/views/settings/IIDXSettings.vue')
  }
]

export default definedGameModules
