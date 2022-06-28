(function(){"use strict";var e={2379:function(e,t){t["Z"]={host:"http://81.70.194.140:11451/api",assetsHost:"http://139.196.223.161/eam_assets"}},3889:function(e,t,n){var i=n(9242),o=n(3396),r=n(7139),a=n(4870),u=n(2379),l=n(5323),s=n(3388);const c=(0,o.Uk)("Dashboard"),d=(0,o.Uk)("Profile"),f=(0,o.Uk)("Ranking"),p=(0,o.Uk)("Settings"),g=(0,o.Uk)("About"),v={id:"root-content"};var m=(0,o.aZ)({setup(e){(0,i.sj)((e=>({"822c7198":n.value})));const t=1200,n=(0,a.iH)("40px");function m(){document.body.clientWidth>t?n.value=`${Math.max((document.body.clientWidth-t)/2,40)}px`:n.value="40px"}window.onresize=m,m();const h=(0,a.iH)(null),b=(0,a.iH)({$delegate:{online:null,dbStatus:null,startupEpochSecond:null,profileCount:null,serverUrl:null,pcbIdRequired:null}}),y=(0,a.iH)(new Map);fetch(`${u.Z.host}/status`).then((e=>e.json())).then((e=>{if(b.value.$delegate.online=!0,-1!==e.result){b.value.$delegate.dbStatus=e.dbStatus,b.value.$delegate.startupEpochSecond=e.startupEpochSecond,b.value.$delegate.profileCount=e.profileCount,b.value.$delegate.serverUrl=e.serverUrl,b.value.$delegate.pcbIdRequired=e.pcbIdRequired;for(const t in e.games)y.value.set(t,{$delegate:{id:t,name:e.games[t].name,supportedVersions:e.games[t].supportedVersions,api:{info:e.games[t].api.info,ranking:e.games[t].api.ranking,profile:e.games[t].api.profile,customize:{get:e.games[t].api.customize_get,update:e.games[t].api.customize_update}},otherApi:(()=>{const n=e.games[t].api,i=["info","ranking","profile","customize_get","customize_update"],o=new Map;for(const e in n)-1===i.indexOf(e)&&o.set(e,n[e]);return o})()}})}else alert("Failed to fetch server status: "+e.message),b.value.$delegate.startupEpochSecond=-1,b.value.$delegate.profileCount=-1})).catch((e=>{b.value.$delegate.online=!1,b.value.$delegate.dbStatus=!1,b.value.$delegate.startupEpochSecond=-1,b.value.$delegate.profileCount=-1,b.value.$delegate.serverUrl="-",console.error(e)})),(0,o.JJ)("server-status",b),(0,o.JJ)("games",y);const w=(0,o.Fl)((()=>(0,l.ej)("cid"))),k=(0,o.Fl)((()=>(0,l.ej)("p"))),S=(0,a.iH)(null);function _(){h.value?.show()}function C(e,t,n){0===e&&t?E(t[0],Number(t[1])).then((e=>{-1!==e.result?(S.value=e.refId,(0,l.d8)("cid",t[0],365),(0,l.d8)("p",t[1],365),n&&n(!0)):(null===S.value&&((0,l.d8)("cid",t[0],365),(0,l.d8)("p",t[1],365)),alert("Verification failed: "+e.message),n&&n(!1))})).catch((e=>{alert("Verification failed: "+e),n&&n(!1)})):n&&n(!0)}async function E(e,t){return fetch(`${u.Z.host}/verify`,{method:"POST",body:JSON.stringify({cardId:e,pin:t})}).then((e=>e.json()))}(0,o.JJ)("refId",S),w.value&&k.value&&E(w.value,Number(k.value)).then((e=>{-1!==e.result?S.value=e.refId:alert("Verification failed: "+e.message)})).catch((e=>{alert("Verification failed: "+e)}));const $=(0,a.iH)(new Map);return(0,o.JJ)("globals",$),(e,t)=>{const n=(0,o.up)("router-link"),i=(0,o.up)("router-view");return(0,o.wg)(),(0,o.iD)(o.HY,null,[(0,o._)("nav",null,[(0,o.Wm)(n,{to:"/"},{default:(0,o.w5)((()=>[c])),_:1}),(0,o.Wm)(n,{to:"/profile"},{default:(0,o.w5)((()=>[d])),_:1}),(0,o.Wm)(n,{to:"/ranking"},{default:(0,o.w5)((()=>[f])),_:1}),(0,o.Wm)(n,{to:"/settings"},{default:(0,o.w5)((()=>[p])),_:1}),(0,o.Wm)(n,{to:"/about"},{default:(0,o.w5)((()=>[g])),_:1}),(0,o._)("span",{class:"card-info",onClick:t[0]||(t[0]=e=>_())},(0,r.zw)(S.value?S.value:"[UNVERIFIED]"),1)]),(0,o._)("div",v,[(0,o.Wm)(i)]),(0,o.Wm)(s.Z,{ref_key:"verificationDialog",ref:h,type:"input",title:"Verification",desc:"Please input card id and pin to verify your profile",input:[{name:"Card Id",desc:"Your EAM card id, start with E004",default:(0,a.SU)(w)},{name:"Pin",desc:"Password of your card, 4 digit",default:(0,a.SU)(k)}],onOnSelect:C},null,8,["input"])],64)}}});const h=m;var b=h,y=n(9321),w=n(7749),k=n(5140);k.kL.register(...k.zX),k.kL.defaults.font={size:12,family:"Gilroy Medium",style:"normal",weight:"normal"},(0,i.ri)(b).use(y.Z).component("font-awesome-icon",w.GN).mount("#app")},9321:function(e,t,n){n.d(t,{Z:function(){return s}});var i=n(678);const o=[{gameId:"sdvx6",settingRoute:()=>n.e(422).then(n.bind(n,3381))},{gameId:"iidx",settingRoute:()=>n.e(245).then(n.bind(n,4495))}];var r=o,a=n(5323);const u=[{path:"/",name:"dashboard",component:()=>n.e(966).then(n.bind(n,5884)),props:!0},{path:"/profile",name:"profile",component:()=>n.e(845).then(n.bind(n,8284))},{path:"/ranking",name:"ranking",component:()=>n.e(845).then(n.bind(n,5134))},{path:"/about",name:"about",component:()=>n.e(443).then(n.bind(n,9526))},{path:"/settings",name:"settings",component:()=>n.e(571).then(n.bind(n,499)),children:r.map((e=>({path:`/settings/${e.gameId}`,name:`settings-${e.gameId}`,component:e.settingRoute}))),redirect:{name:`settings-${(0,a.ej)("sel","sdvx6")}`}}],l=(0,i.p7)({history:(0,i.r5)("/"),routes:u});var s=l},3172:function(e,t,n){n.d(t,{V:function(){return i}});const i=e=>{let t=!1,n=null;const i=function(){return t||(n=e(),t=!0),n};return i.isLazy=!0,i}},5323:function(e,t,n){n.d(t,{d8:function(){return o},ej:function(){return i}});const i=function(e,t){let n=t||"";return document.cookie.split(";").forEach((t=>{const i=t.trim();i.startsWith(e+"=")&&(n=i.replace(e+"=",""))})),n},o=function(e,t,n){const i=new Date;i.setTime(i.getTime()+24*n*60*60*1e3);const o="expires="+i.toUTCString();document.cookie=e+"="+t+";"+o+";path=/"}},3388:function(e,t,n){n.d(t,{Z:function(){return y}});n(1703);var i=n(3396),o=n(7139),r=n(4870),a=n(9242),u=n(3172);const l={id:"dialog-title"},s={key:0,id:"dialog-desc"},c={class:"input-name"},d={key:0,class:"input-desc"},f=["value"],p={key:2,id:"dialog-content"},g={id:"dialog-footer"},v=["onClick"];var m=(0,i.aZ)({props:{initialShow:{type:Boolean},type:null,title:null,desc:null,selection:null,input:null},emits:["onSelect"],setup(e,{expose:t,emit:n}){const m=e;if("input"===m.type&&(void 0===m.input||0===m.input.length))throw Error("No input options in dialog since type is input.");const h=(0,r.iH)(!!m.initialShow&&m.initialShow),b=m.selection?m.selection:["Apply","Cancel"],y=(0,r.iH)(null),w=(0,r.iH)(null),k=(0,r.iH)(null);t({show:_,dismiss:C});const S=(0,u.V)((()=>{if(!m.input)return null;if(null===w.value)return null;const e=[];for(let t=0;t<m.input.length;t++){const n=w.value.children.item(t),i=n.children.namedItem("input");e.push(i)}return e}));function _(){if(h.value=!0,null===k.value)throw Error("Cannot handle dialog");k.value.animate([{opacity:0},{opacity:1}],{duration:200,easing:"ease-out"}).play()}function C(){if(null===k.value)throw Error("Cannot handle dialog");const e=k.value.animate([{opacity:1},{opacity:0}],{duration:200,easing:"ease-out"});e.onfinish=()=>h.value=!1,e.play()}function E(e){n("onSelect",e,S()?.map((e=>e.value)),(t=>{e<0&&(t=!0),t&&C()}))}return setTimeout((()=>{const e=window.onclick;window.onclick=t=>{if(null!=y.value){t.preventDefault();const e=y.value.getBoundingClientRect();t.clientX>=e.left&&t.clientX<=e.right&&t.clientY>=e.top&&t.clientY<=e.bottom||E(-1)}window.onclick=e}})),(t,n)=>(0,i.wy)(((0,i.wg)(),(0,i.iD)("div",{id:"background",ref_key:"dialogRootContainer",ref:k},[(0,i._)("div",{id:"dialog-body",ref_key:"dialogBody",ref:y},[(0,i._)("div",l,(0,o.zw)(e.title),1),void 0!==e.desc?((0,i.wg)(),(0,i.iD)("div",s,(0,o.zw)(e.desc),1)):(0,i.kq)("",!0),"input"===e.type?((0,i.wg)(),(0,i.iD)("div",{key:1,id:"dialog-content",ref_key:"dialogContent",ref:w},[((0,i.wg)(!0),(0,i.iD)(i.HY,null,(0,i.Ko)(e.input,((e,t)=>((0,i.wg)(),(0,i.iD)("div",{class:"input-option",key:t},[(0,i._)("span",c,(0,o.zw)(e.name),1),e.desc?((0,i.wg)(),(0,i.iD)("span",d,(0,o.zw)(e.desc),1)):(0,i.kq)("",!0),(0,i._)("input",{type:"text",class:"input",name:"input",value:e.default},null,8,f)])))),128))],512)):((0,i.wg)(),(0,i.iD)("div",p)),(0,i._)("div",g,[((0,i.wg)(!0),(0,i.iD)(i.HY,null,(0,i.Ko)((0,r.SU)(b),((e,t)=>((0,i.wg)(),(0,i.iD)("button",{class:"apply-button",key:t,onClick:e=>E(t)},(0,o.zw)(e),9,v)))),128))])],512)],512)),[[a.F8,h.value]])}}),h=n(89);const b=(0,h.Z)(m,[["__scopeId","data-v-76bb87c8"]]);var y=b}},t={};function n(i){var o=t[i];if(void 0!==o)return o.exports;var r=t[i]={exports:{}};return e[i].call(r.exports,r,r.exports,n),r.exports}n.m=e,function(){var e=[];n.O=function(t,i,o,r){if(!i){var a=1/0;for(c=0;c<e.length;c++){i=e[c][0],o=e[c][1],r=e[c][2];for(var u=!0,l=0;l<i.length;l++)(!1&r||a>=r)&&Object.keys(n.O).every((function(e){return n.O[e](i[l])}))?i.splice(l--,1):(u=!1,r<a&&(a=r));if(u){e.splice(c--,1);var s=o();void 0!==s&&(t=s)}}return t}r=r||0;for(var c=e.length;c>0&&e[c-1][2]>r;c--)e[c]=e[c-1];e[c]=[i,o,r]}}(),function(){n.n=function(e){var t=e&&e.__esModule?function(){return e["default"]}:function(){return e};return n.d(t,{a:t}),t}}(),function(){n.d=function(e,t){for(var i in t)n.o(t,i)&&!n.o(e,i)&&Object.defineProperty(e,i,{enumerable:!0,get:t[i]})}}(),function(){n.f={},n.e=function(e){return Promise.all(Object.keys(n.f).reduce((function(t,i){return n.f[i](e,t),t}),[]))}}(),function(){n.u=function(e){return"js/"+{245:"settings.iidx",422:"settings.sdvx6",443:"about",571:"settings",845:"profile",966:"dashboard"}[e]+"."+{245:"f440c900",422:"797583e5",443:"0f0f0bc2",571:"db3bc53a",845:"6735fe19",966:"fcecf913"}[e]+".js"}}(),function(){n.miniCssF=function(e){return"css/"+{422:"settings.sdvx6",571:"settings",966:"dashboard"}[e]+"."+{422:"b992f0e9",571:"08dc756a",966:"ce9caabc"}[e]+".css"}}(),function(){n.g=function(){if("object"===typeof globalThis)return globalThis;try{return this||new Function("return this")()}catch(e){if("object"===typeof window)return window}}()}(),function(){n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)}}(),function(){var e={},t="eagwebui:";n.l=function(i,o,r,a){if(e[i])e[i].push(o);else{var u,l;if(void 0!==r)for(var s=document.getElementsByTagName("script"),c=0;c<s.length;c++){var d=s[c];if(d.getAttribute("src")==i||d.getAttribute("data-webpack")==t+r){u=d;break}}u||(l=!0,u=document.createElement("script"),u.charset="utf-8",u.timeout=120,n.nc&&u.setAttribute("nonce",n.nc),u.setAttribute("data-webpack",t+r),u.src=i),e[i]=[o];var f=function(t,n){u.onerror=u.onload=null,clearTimeout(p);var o=e[i];if(delete e[i],u.parentNode&&u.parentNode.removeChild(u),o&&o.forEach((function(e){return e(n)})),t)return t(n)},p=setTimeout(f.bind(null,void 0,{type:"timeout",target:u}),12e4);u.onerror=f.bind(null,u.onerror),u.onload=f.bind(null,u.onload),l&&document.head.appendChild(u)}}}(),function(){n.r=function(e){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})}}(),function(){n.p="/"}(),function(){var e=function(e,t,n,i){var o=document.createElement("link");o.rel="stylesheet",o.type="text/css";var r=function(r){if(o.onerror=o.onload=null,"load"===r.type)n();else{var a=r&&("load"===r.type?"missing":r.type),u=r&&r.target&&r.target.href||t,l=new Error("Loading CSS chunk "+e+" failed.\n("+u+")");l.code="CSS_CHUNK_LOAD_FAILED",l.type=a,l.request=u,o.parentNode.removeChild(o),i(l)}};return o.onerror=o.onload=r,o.href=t,document.head.appendChild(o),o},t=function(e,t){for(var n=document.getElementsByTagName("link"),i=0;i<n.length;i++){var o=n[i],r=o.getAttribute("data-href")||o.getAttribute("href");if("stylesheet"===o.rel&&(r===e||r===t))return o}var a=document.getElementsByTagName("style");for(i=0;i<a.length;i++){o=a[i],r=o.getAttribute("data-href");if(r===e||r===t)return o}},i=function(i){return new Promise((function(o,r){var a=n.miniCssF(i),u=n.p+a;if(t(a,u))return o();e(i,u,o,r)}))},o={143:0};n.f.miniCss=function(e,t){var n={422:1,571:1,966:1};o[e]?t.push(o[e]):0!==o[e]&&n[e]&&t.push(o[e]=i(e).then((function(){o[e]=0}),(function(t){throw delete o[e],t})))}}(),function(){var e={143:0};n.f.j=function(t,i){var o=n.o(e,t)?e[t]:void 0;if(0!==o)if(o)i.push(o[2]);else{var r=new Promise((function(n,i){o=e[t]=[n,i]}));i.push(o[2]=r);var a=n.p+n.u(t),u=new Error,l=function(i){if(n.o(e,t)&&(o=e[t],0!==o&&(e[t]=void 0),o)){var r=i&&("load"===i.type?"missing":i.type),a=i&&i.target&&i.target.src;u.message="Loading chunk "+t+" failed.\n("+r+": "+a+")",u.name="ChunkLoadError",u.type=r,u.request=a,o[1](u)}};n.l(a,l,"chunk-"+t,t)}},n.O.j=function(t){return 0===e[t]};var t=function(t,i){var o,r,a=i[0],u=i[1],l=i[2],s=0;if(a.some((function(t){return 0!==e[t]}))){for(o in u)n.o(u,o)&&(n.m[o]=u[o]);if(l)var c=l(n)}for(t&&t(i);s<a.length;s++)r=a[s],n.o(e,r)&&e[r]&&e[r][0](),e[r]=0;return n.O(c)},i=self["webpackChunkeagwebui"]=self["webpackChunkeagwebui"]||[];i.forEach(t.bind(null,0)),i.push=t.bind(null,i.push.bind(i))}();var i=n.O(void 0,[998],(function(){return n(3889)}));i=n.O(i)})();
//# sourceMappingURL=app.c12d70c0.js.map