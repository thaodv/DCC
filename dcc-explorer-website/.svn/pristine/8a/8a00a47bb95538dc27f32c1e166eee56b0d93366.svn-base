import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/components/home'
const Main = r => require.ensure([],()=> r(require('@/components/index/index')));
const Block =  r => require.ensure([],()=> r(require('@/components/block/block')));
const BlockModal =r => require.ensure([],()=> r(require('@/components/block/modal')));
const ModalHash =r => require.ensure([],()=> r(require('@/common/modalHash')));
const ModalAddress =r => require.ensure([],()=> r(require('@/common/modalAddress')));
const Transaction =r => require.ensure([],()=> r(require('@/components/transaction')));
const Contracts =r => require.ensure([],()=> r(require('@/components/contracts/index')));
const ContractsDetail =r => require.ensure([],()=> r(require('@/components/contracts/detail')));
const Nofound=r => require.ensure([],()=> r(require('../pages/404.vue')));
const Error=r => require.ensure([],()=> r(require('../pages/search_err.vue')));

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home,
      redirect:{name:"index"},
      children:[
        {
          path: '/index',
          name: 'index',
          component: Main
        },
        {
          path: '/block',
          name: 'Block',
          component: Block,
          children:[
            {
              path: '/search:aid',
              component: BlockModal
            }]
        },
        {
          path: '/transaction',
          name: 'Transaction',
          component: Transaction
        },
        {
          path: '/block/search/:aid',
          component: BlockModal
        },
        {
          path: '/block/hash/:aid',
          component: ModalHash
        },
        {
          path: '/transaction/address/:aid',
          component: ModalAddress
        },
        {
          path:'/contracts',
          component:Contracts
        },
        {
          path:'/contracts/detail/:aid',
          component:ContractsDetail
        },
        {
          path:'/err/:aid',
          name:'err',
          component:Error
        }
      ]
    },
    {
      path:'*',
      name:'nofound',
      component:Nofound
    }
   
  ]
})
