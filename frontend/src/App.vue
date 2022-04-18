<template>
  <nav>
    <router-link to="/">Dashboard</router-link>
    <router-link to="/profile">Profile</router-link>
    <router-link to="/ranking">Ranking</router-link>
    <router-link to="/settings">Settings</router-link>
    <router-link to="/about">About</router-link>
    <span class="user-info">user name</span>
  </nav>
  <div class="root-content">
    <router-view/>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const contentWidth = 1200
const sidePadding = ref<string>('40px')

function __onWindowResize (): any {
  if (document.body.clientWidth > contentWidth) {
    sidePadding.value = `${Math.max((document.body.clientWidth - contentWidth) / 2, 40)}px`
  } else {
    sidePadding.value = '40px'
  }
}

window.onresize = __onWindowResize
__onWindowResize()
</script>

<style>
@font-face {
  font-family: 'Gilroy Medium';
  src: url('./assets/Gilroy-Medium.otf') format('opentype');
  font-weight: normal;
}

@font-face {
  font-family: 'Gilroy Bold';
  src: url('assets/Gilroy-Bold.otf') format('opentype');
  font-weight: bold;
}

/*noinspection ALL*/
#app {
  font-family: 'Gilroy Medium', 'Gilroy Bold', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.user-info {
  display: flex;
  float: right;
}

nav {
  padding: 35px v-bind(sidePadding);
  border-width: 0 0 1px 0;
  border-style: solid;
  border-color: rgba(169, 169, 169, 0.85);
  background-color: white;
  box-shadow: darkgrey 0 3px 20px 0;
}

nav a {
  align-self: center;
  font-weight: normal;
  padding: 15px 30px;
  margin: 0 10px;
  color: rgb(179, 184, 194);
  border-radius: 90px;
  text-decoration-line: none;
  transition: all 0.2s ease-in-out;
}

nav a:hover {
  color: rgb(255, 255, 255);
  background-color: rgb(199, 204, 213);
}

/*noinspection ALL*/
nav a.router-link-exact-active {
  font-weight: bold;
  color: white;
  background-color: rgb(20, 85, 254);
  background-clip: padding-box;
  border-radius: 90px;
  filter: drop-shadow(2px 2px 15px rgba(0, 0, 0, 0.3));
  transition: all 0.2s ease-in-out;
}

.root-content {
  padding-left: v-bind(sidePadding);
  padding-right: v-bind(sidePadding);
}

</style>
