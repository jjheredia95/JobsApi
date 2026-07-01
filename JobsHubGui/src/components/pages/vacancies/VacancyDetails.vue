<script setup>

import "../../../assets/css/VacancyDetaiils.css"
import { onMounted, ref } from "vue"
import { useRoute } from "vue-router"

const route = useRoute()
const id = route.params.id

const vacancy = ref({})
const error = ref('')
const isLoading = ref(true)

async function loadVacancy() {
  try {
    const response = await fetch(`http://localhost:8080/api/vacancies/details/${id}`)
    if (!response.ok) {
      throw new Error("An error occurred trying to load the vacancy.")
    }
    vacancy.value = await response.json()
  } catch (err) {
    error.value = err.message
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadVacancy()
})
</script>

<template>

  <!-- ── PAGE HEADER ── -->
  <div class="page-header">
    <div class="container" style="max-width:1060px;">
      <div class="breadcrumb-custom">
        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/>
        </svg>
        Jobs / {{ vacancy.name || '...' }}
      </div>

      <div v-if="isLoading">
        <div class="page-title" style="color:#94A3B8;">Loading...</div>
      </div>

      <div v-else-if="!error">
        <h1 class="page-title">{{ vacancy.name }}</h1>
        <div class="d-flex flex-wrap gap-2 mt-2">
          <span class="jh-badge badge-status">{{ vacancy.status }}</span>
          <span class="jh-badge badge-wm">{{ vacancy.workMode }}</span>
          <span class="jh-badge badge-emp">{{ vacancy.employmentType }}</span>
          <span v-if="vacancy.salary" class="jh-badge badge-sal">${{ vacancy.salary?.toLocaleString() }}</span>
        </div>
      </div>
    </div>
  </div>

  <!-- ── MAIN ── -->
  <main class="container py-4" style="max-width:1060px;">

    <!-- Loading -->
    <div v-if="isLoading" class="text-center py-5">
      <div class="spinner-border" style="color:#6366F1;" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <!-- Content -->
    <div v-else>
      <a class="back-link" onclick="history.back()">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
        </svg>
        Back to jobs
      </a>

      <div class="row g-4">

        <!-- ── LEFT: Main content ── -->
        <div class="col-lg-8">

          <!-- Job information -->
          <div class="detail-card">
            <div class="detail-card-title">
              <span class="section-bar"></span>Job information
            </div>

            <!-- Locations -->
            <div class="meta-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>
              </svg>
              <span class="meta-label">Locations</span>
              <div class="d-flex flex-wrap gap-1">
                <span
                    v-for="location in vacancy.locations"
                    :key="location.id"
                    class="jh-badge badge-loc"
                >
                  📍 {{ location.city }}, {{ location.state }}
                </span>
              </div>
            </div>

            <!-- Published -->
            <div class="meta-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="18" rx="2"/>
                <line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
              </svg>
              <span class="meta-label">Published</span>
              {{ vacancy.publishedDate }}
            </div>

            <!-- Open date -->
            <div class="meta-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
              </svg>
              <span class="meta-label">Open date</span>
              {{ vacancy.openDate }}
            </div>

            <!-- Close date -->
            <div v-if="vacancy.closeDate" class="meta-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
              </svg>
              <span class="meta-label">Close date</span>
              {{ vacancy.closeDate }}
            </div>

            <!-- Category -->
            <div class="meta-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
                <line x1="7" y1="7" x2="7.01" y2="7"/>
              </svg>
              <span class="meta-label">Category</span>
              {{ vacancy.category }}
            </div>

            <hr class="detail-divider" />

            <p class="desc-text">{{ vacancy.description }}</p>
          </div>

          <!-- Job details -->
          <div v-if="vacancy.details" class="detail-card">
            <div class="detail-card-title">
              <span class="section-bar"></span>Job details
            </div>
            <p class="desc-text" style="white-space:pre-line;">{{ vacancy.details }}</p>
          </div>

        </div>

        <!-- ── RIGHT: Sidebar ── -->
        <div class="col-lg-4">

          <!-- Apply card -->
          <div class="detail-card">
            <button class="btn-apply mb-3">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
              </svg>
              Apply now
            </button>
            <div v-if="vacancy.closeDate" class="apply-deadline">
              Application closes {{ vacancy.closeDate }}
            </div>
          </div>

          <!-- Company card -->
          <div v-if="vacancy.company" class="detail-card">
            <div class="detail-card-title">
              <span class="section-bar"></span>About the company
            </div>
            <div class="d-flex align-items-start gap-3 mb-3">
              <div class="company-logo-box">
                {{ (vacancy.company.name || '?')[0].toUpperCase() }}
              </div>
              <div>
                <div class="company-name">{{ vacancy.company.name }}</div>
                <div v-if="vacancy.company.headquarters" class="company-meta">
                  {{ vacancy.company.headquarters }}
                </div>
                <a
                    v-if="vacancy.company.website"
                    :href="vacancy.company.website"
                    target="_blank"
                    class="company-link d-block mt-1"
                >
                  {{ vacancy.company.website }} ↗
                </a>
              </div>
            </div>
            <p v-if="vacancy.company.description" class="desc-text" style="font-size:.8rem;">
              {{ vacancy.company.description }}
            </p>
          </div>

        </div>
      </div>

      <div class="mb-5"></div>
    </div>

  </main>
</template>
