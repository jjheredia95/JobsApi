import { createRouter, createWebHistory } from 'vue-router'
import Home from '../components/pages/Home.vue'
import CategoryList from '../components/pages/categories/CategoryList.vue'
import CategoryForm from '../components/pages/categories/CategoryForm.vue'
import VacancyAdminList from '../components/pages/vacancies/VacancyAdminList.vue'
import VacancyForm from '../components/pages/vacancies/VacancyForm.vue'
import VacancyDetails from '../components/pages/vacancies/VacancyDetails.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/categories/list', component: CategoryList },
  { path: '/categories/new', component: CategoryForm },
  { path: '/categories/edit/:id', component: CategoryForm },
  { path: '/vacancies/list', component: VacancyAdminList },
  { path: '/vacancies/new', component: VacancyForm },
  { path: '/vacancies/edit/:id', component: VacancyForm },
  { path: '/vacancies/details/:id', component: VacancyDetails }
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
