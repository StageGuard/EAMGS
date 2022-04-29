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

import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import DashboardView from '@/views/DashboardView.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'dashboard',
    component: DashboardView,
    props: true
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import(/* webpackChunkName: "profile" */ '@/views/ProfileView.vue')
  },
  {
    path: '/about',
    name: 'about',
    component: () => import(/* webpackChunkName: "about" */ '@/views/AboutView.vue')
  },
  {
    path: '/settings',
    name: 'settings',
    component: () => import(/* webpackChunkName: "settings" */ '@/views/SettingsView.vue')
  }
]

const router = createRouter({ history: createWebHistory(process.env.BASE_URL), routes })

export default router
