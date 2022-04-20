import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)
Chart.defaults.font = {
  size: 12,
  family: 'Gilroy Medium',
  style: 'normal',
  weight: 'normal'
}

createApp(App)
  .use(router)
  .component('font-awesome-icon', FontAwesomeIcon)
  .mount('#app')
