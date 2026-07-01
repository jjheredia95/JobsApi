
<script setup>

import "../../../assets/css/VacancyAdminList.css"
import { onMounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import Pagination from "../../common/Pagination.vue";

const vacancies = ref([])
const loading = ref(true)
const error = ref('')
const route = useRoute()
const created = ref(route.query.created === 'true')
const updated = ref(route.query.updated === 'true')
const deletingId = ref(null)

const currentPage = ref(0)
const totalPages = ref(0)
const pageSize = 10
const totalElements = ref(0)

function formatFeatured(value) {
  return value ? 'Featured' : 'Standard'
}

function statusClass(status) {
  const s = (status || '').toLowerCase()
  if (s.includes('publish')) return 'status-published'
  return 'status-open'
}

async function loadVacancies(page = 0) {
  try {
    const response = await fetch(`http://localhost:8080/api/vacancies?page=${page}&size=${pageSize}`)
    if (!response.ok) {
      // noinspection ExceptionCaughtLocallyJS
      throw new Error('There was an error loading the vacancies.')
    }

    const data = await response.json()
    vacancies.value = data.content
    totalPages.value = data.totalPages
    currentPage.value = data.number
    totalElements.value = data.totalElements

  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

async function deleteVacancy(id) {
  const confirmed = window.confirm('Are you sure you want to delete this vacancy?')

  if (!confirmed) {
    return
  }

  deletingId.value = id
  error.value = ''

  try {
    const response = await fetch(`http://localhost:8080/api/vacancies/delete/${id}`, {
      method: 'DELETE'
    })

    if (!response.ok) {
      // noinspection ExceptionCaughtLocallyJS
      throw new Error('There was an error deleting the vacancy.')
    }

    vacancies.value = vacancies.value.filter(vacancy => vacancy.id !== id)

    if (vacancies.value.length === 0 && currentPage.value > 0) {
      await loadVacancies(currentPage.value - 1)
    } else {
      await loadVacancies(currentPage.value)
    }
  } catch (err) {
    error.value = err.message
  } finally {
    deletingId.value = null
  }
}

function onPageChange(page) {
  loadVacancies(page - 1)
}

onMounted(() => {
  loadVacancies(0)
})
</script>

<template>
  <!-- ── PAGE HEADER ── -->
  <div class="page-header">
    <div class="container" style="max-width:1100px;">
      <div class="breadcrumb-custom">
        <i class="fa-solid fa-briefcase"></i>
        Vacancies
      </div>
      <h1 class="page-title">Vacancy List</h1>
      <p class="page-subtitle">Review published opportunities and keep openings current for candidates.</p>

      <RouterLink class="btn-new" to="/vacancies/new">
        <i class="fa-solid fa-plus"></i>
        New Vacancy
      </RouterLink>
    </div>
  </div>

  <!-- ── MAIN ── -->
  <main class="container py-4" style="max-width:1100px;">

    <!-- Alerts -->
    <div v-if="error" class="alert alert-danger" role="alert">
      {{ error }}
    </div>
    <div v-if="created" class="alert alert-success" role="alert">
      Vacancy created successfully.
    </div>
    <div v-if="updated" class="alert alert-success" role="alert">
      Vacancy updated successfully.
    </div>

    <template v-if="!error">

      <div class="d-flex align-items-center mb-3">
        <h2 class="section-title mb-0">
          <span class="section-bar"></span>All Vacancies
          <span class="badge-count">{{ totalElements }} results</span>
        </h2>
      </div>

      <div class="admin-table-wrap">
        <table class="admin-table">
          <thead>
          <tr>
            <th>Id</th>
            <th>Category</th>
            <th>Name</th>
            <th>Published Date</th>
            <th>Status</th>
            <th>Featured</th>
            <th style="text-align:right;">Actions</th>
          </tr>
          </thead>

          <tbody>
          <tr v-if="loading">
            <td colspan="7" class="empty-cell">Loading vacancies...</td>
          </tr>

          <tr v-else v-for="vacancy in vacancies" :key="vacancy.id">
            <td class="id-cell">#{{ vacancy.id }}</td>
            <td class="cat-cell">{{ vacancy.category }}</td>
            <td class="name-cell">{{ vacancy.name }}</td>
            <td class="date-cell">{{ vacancy.publishedDate }}</td>
            <td>
                <span class="status-pill" :class="statusClass(vacancy.status)">
                  <span class="status-dot"></span>{{ vacancy.status }}
                </span>
            </td>
            <td>
                <span class="featured-pill" :class="vacancy.featured ? 'featured-yes' : 'featured-no'">
                  {{ formatFeatured(vacancy.featured) }}
                </span>
            </td>
            <td style="text-align:right;">
              <RouterLink
                  :to="`/vacancies/edit/${vacancy.id}`"
                  class="action-btn action-edit"
                  title="Edit"
              >
                <i class="fas fa-pencil-alt"></i>
              </RouterLink>
              <button
                  type="button"
                  class="action-btn action-delete"
                  :title="deletingId === vacancy.id ? 'Deleting' : 'Delete'"
                  :disabled="deletingId === vacancy.id"
                  @click="deleteVacancy(vacancy.id)"
              >
                <i :class="deletingId === vacancy.id ? 'fas fa-spinner fa-spin' : 'fas fa-trash'"></i>
              </button>
            </td>
          </tr>

          <tr v-if="!loading && vacancies.length === 0">
            <td colspan="7" class="empty-cell">No vacancies found.</td>
          </tr>
          </tbody>
        </table>
      </div>

      <Pagination
          :current-page="currentPage + 1"
          :total-pages="totalPages"
          @page-change="onPageChange"
      />
    </template>

  </main>
</template>
