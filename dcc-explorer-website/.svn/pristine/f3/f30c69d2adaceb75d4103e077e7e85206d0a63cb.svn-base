// The Vue build version to load with the `import` command (runtime-only or
// standalone) has been set in webpack.base.conf with an alias.
import babelpolyfill from '@babel/polyfill'
import Vue from 'vue'
import App from './App'
import router from './router'

//修改html默认样式
import "./assets/css/common.css"
import "./assets/css/explorer.css"
//引入bootstrap CSS
import 'bootstrap/dist/css/bootstrap.css'

import qs from 'qs';
import axios from './untils/config'
Vue.prototype.$axios = axios; 

import './common/toast/toast.css';
import Toast from "./common/toast/toast.js"
Vue.use(Toast)

import * as filters from './filters.js'
Object.keys(filters).forEach(key => {  
    Vue.filter(key, filters[key])  
}) 

/* eslint-disable no-new */
new Vue({el: '#app', router, components: {
        App
    }, template: '<App/>'})