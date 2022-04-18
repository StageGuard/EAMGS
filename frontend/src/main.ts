import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// @ts-ignore
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

const mounted = createApp(App)
  .use(router)
  .component('font-awesome-icon', FontAwesomeIcon)
  .mount('#app')
