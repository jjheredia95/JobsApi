<script setup>

import "../../../assets/css/VacancyForm.css"
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const today = new Date().toISOString().split('T')[0]

const vacancy = ref({
  name: '',
  description: '',
  categoryId: '',
  openDate:'',
  closeDate: '',
  salary: '',
  image: '',
  details: '',
  companyId: '',
  featured: false,
  status: 'OPEN',
  workMode: 'ONSITE',
  employmentType: 'FULL_TIME'
})

const categories = ref([])
const companies = ref([])
const locations = ref([])
const companySearch = ref('')
const showNewCategoryForm = ref(false)
const newCategory = ref({
  name: '',
  description: ''
})
const selectedLocationIds = ref([])
const locationSearch = ref('')
const showNewLocationForm = ref(false)
const newLocation = ref({
  city: '',
  state: ''
})
const errors = ref([])
const loading = ref(false)
const saving = ref(false)
const savingCategory = ref(false)
const savingLocation = ref(false)
const router = useRouter()
const route = useRoute()
const isEdit = computed(() => Boolean(route.params.id))
const vacancyId = computed(() => Number(route.params.id))

const filteredCompanies = computed(() => {
  const search = companySearch.value.trim().toLowerCase()

  if (!search) {
    return companies.value
  }

  return companies.value.filter(company =>
      company.name.toLowerCase().includes(search)
  )
})

const filteredLocations = computed(() => {
  const search = locationSearch.value.trim().toLowerCase()

  if (!search) {
    return locations.value
  }

  return locations.value.filter(location =>
      formatLocation(location).toLowerCase().includes(search)
  )
})

const selectedLocationsSummary = computed(() => {
  if (!selectedLocationIds.value.length) {
    return 'No locations selected'
  }

  return selectedLocationIds.value
      .map(locationId => locations.value.find(location => location.id === locationId))
      .filter(Boolean)
      .map(formatLocation)
      .join(', ')
})

const imagePreview = computed(() => {
  const image = vacancy.value.image?.trim()

  if (!image) {
    return ''
  }

  return image.startsWith('http://') || image.startsWith('https://')
      ? image
      : `/images/${image}`
})

async function fetchJson(url, errorMessage) {
  const response = await fetch(url)

  if (!response.ok) {
    throw new Error(errorMessage)
  }

  return response.json()
}

async function loadCategories() {
  categories.value = await fetchJson('http://localhost:8080/api/categories', 'Could not load categories.')
}

async function loadCompanies() {
  companies.value = await fetchJson('http://localhost:8080/api/companies', 'Could not load companies.')
}

async function loadLocations() {
  locations.value = await fetchJson('http://localhost:8080/api/locations', 'Could not load locations.')
}

async function loadVacancyForEdit() {
  const vacancies = await fetchJson('http://localhost:8080/api/vacancies/full', 'Could not load vacancy.')
  const selectedVacancy = vacancies.find(item => item.id === vacancyId.value)

  if (!selectedVacancy) {
    throw new Error('Vacancy not found.')
  }

  vacancy.value = {
    name: selectedVacancy.name || '',
    description: selectedVacancy.description || '',
    categoryId: selectedVacancy.category?.id || '',
    openDate: selectedVacancy.openDate || '',
    closeDate: selectedVacancy.closeDate || '',
    salary: selectedVacancy.salary ?? '',
    image: selectedVacancy.image || '',
    details: selectedVacancy.details || '',
    companyId: selectedVacancy.company?.id || '',
    featured: Boolean(selectedVacancy.featured),
    status: selectedVacancy.status || 'OPEN',
    workMode: selectedVacancy.workMode || 'ONSITE',
    employmentType: selectedVacancy.employmentType || 'FULL_TIME'
  }

  selectedLocationIds.value = selectedVacancy.locations?.map(location => location.id) || []
}

function collectMessages(value) {
  if (!value) {
    return []
  }

  if (Array.isArray(value)) {
    return value.flatMap(collectMessages)
  }

  if (typeof value === 'object') {
    return Object.values(value).flatMap(collectMessages)
  }

  return [String(value)]
}

async function readErrorMessages(response) {
  try {
    const body = await response.json()
    const messages = collectMessages(body?.message)

    if (messages.length) {
      return messages
    }

    return collectMessages(body?.error)
  } catch {
    return [`Error ${response.status}`]
  }
}



function handleCategoryChange() {
  if (vacancy.value.categoryId === '__other__') {
    vacancy.value.categoryId = ''
    showNewCategoryForm.value = true
  }
}

function closeNewCategoryForm() {
  showNewCategoryForm.value = false
  newCategory.value = {
    name: '',
    description: ''
  }
}

async function createCategory() {
  errors.value = []
  savingCategory.value = true

  try {
    const categoryName = newCategory.value.name.trim()
    const response = await fetch('http://localhost:8080/api/categories/create', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        name: categoryName,
        description: newCategory.value.description.trim()
      })
    })

    if (!response.ok) {
      errors.value = await readErrorMessages(response)
      return
    }

    const updatedCategories = await response.json()
    categories.value = updatedCategories

    const createdCategory = updatedCategories.find(category =>
        category.name.toLowerCase() === categoryName.toLowerCase()
    )

    if (createdCategory) {
      vacancy.value.categoryId = createdCategory.id
    }

    closeNewCategoryForm()
  } catch (err) {
    errors.value = [err.message]
  } finally {
    savingCategory.value = false
  }
}

function formatLocation(location) {
  return location.state ? `${location.city}, ${location.state}` : location.city
}

function handleLocationChange() {
  if (selectedLocationIds.value.includes('__other__')) {
    selectedLocationIds.value = selectedLocationIds.value.filter(locationId => locationId !== '__other__')
    showNewLocationForm.value = true
  }
}

function closeNewLocationForm() {
  showNewLocationForm.value = false
  newLocation.value = {
    city: '',
    state: ''
  }
}

async function createLocation() {
  errors.value = []
  savingLocation.value = true

  try {
    const response = await fetch('http://localhost:8080/api/locations', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        city: newLocation.value.city.trim(),
        state: newLocation.value.state.trim()
      })
    })

    if (!response.ok) {
      errors.value = await readErrorMessages(response)
      return
    }

    const createdLocation = await response.json()
    locations.value = [...locations.value, createdLocation]
    selectedLocationIds.value = [
      ...new Set([...selectedLocationIds.value, createdLocation.id])
    ]
    closeNewLocationForm()
  } catch (err) {
    errors.value = [err.message]
  } finally {
    savingLocation.value = false
  }
}

function buildJsonPayload() {
  const payload = {
    name: vacancy.value.name.trim(),
    description: vacancy.value.description.trim(),
    categoryId: Number(vacancy.value.categoryId),
    openDate: vacancy.value.openDate,
    closeDate: vacancy.value.closeDate || null,
    salary: vacancy.value.salary === '' ? null : Number(vacancy.value.salary),
    image: vacancy.value.image?.trim() || null,
    details: vacancy.value.details?.trim() || null,
    companyId: Number(vacancy.value.companyId),
    locationIds: selectedLocationIds.value.map(Number)
  }

  if (isEdit.value) {
    payload.featured = vacancy.value.featured
    payload.status = vacancy.value.status
    payload.workMode = vacancy.value.workMode
    payload.employmentType = vacancy.value.employmentType
  }

  return payload
}

async function handleSubmit() {
  errors.value = []
  saving.value = true

  try {
    const url = isEdit.value
        ? `http://localhost:8080/api/vacancies/update/${vacancyId.value}`
        : 'http://localhost:8080/api/vacancies/create'

    const response = await fetch(url, {
      method: isEdit.value ? 'PUT' : 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(buildJsonPayload())
    })

    if (!response.ok) {
      errors.value = await readErrorMessages(response)
      return
    }

    router.push(`/vacancies/list?${isEdit.value ? 'updated' : 'created'}=true`)
  } catch (err) {
    errors.value = [err.message]
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  loading.value = true
  errors.value = []

  try {
    await Promise.all([
      loadCategories(),
      loadCompanies(),
      loadLocations()
    ])

    if (isEdit.value) {
      await loadVacancyForEdit()
    }
  } catch (err) {
    errors.value = [err.message]
  } finally {
    loading.value = false
  }
})
</script>

<template>

  <!-- ── PAGE HEADER ── -->
  <div class="page-header">
    <div class="container" style="max-width:900px;">
      <div class="breadcrumb-custom">
        <i class="fa-solid fa-briefcase"></i>
        Vacancies / {{ isEdit ? 'Edit' : 'New' }}
      </div>
      <h1 class="page-title">{{ isEdit ? 'Edit Vacancy' : 'New Vacancy' }}</h1>
      <p class="page-subtitle">
        {{ isEdit ? 'Update the details for this vacancy.' : 'Fill in the details to publish a new vacancy.' }}
      </p>
    </div>
  </div>

  <!-- ── MAIN ── -->
  <main class="container py-4" style="max-width:900px;">

    <div v-if="errors.length" class="form-alert form-alert-error">
      <ul class="mb-0 ps-3">
        <li v-for="(msg, i) in errors" :key="i">{{ msg }}</li>
      </ul>
    </div>

    <div v-if="loading" class="text-center py-5 text-muted">
      Loading form data...
    </div>

    <form v-else @submit.prevent="handleSubmit" novalidate>

      <!-- ═══ BASIC INFORMATION ═══ -->
      <div class="form-card">
        <div class="form-card-title">
          <span class="section-bar"></span>Basic information
        </div>

        <div class="row g-3">
          <div class="col-md-6">
            <label for="name" class="jh-label">Name</label>
            <input id="name" v-model="vacancy.name" type="text" class="jh-input" placeholder="e.g. React Developer" required />
          </div>

          <div class="col-md-6">
            <label for="categoryId" class="jh-label">Category</label>
            <select id="categoryId" v-model="vacancy.categoryId" class="jh-select" required @change="handleCategoryChange">
              <option value="" disabled>Select a category</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
              <option value="__other__">+ Other (create new)</option>
            </select>
          </div>

          <!-- New category inline form -->
          <div v-if="showNewCategoryForm" class="col-12">
            <div class="inline-card">
              <div class="inline-card-title">Create Category</div>
              <div class="row g-3">
                <div class="col-md-6">
                  <label for="newCategoryName" class="jh-label">Name</label>
                  <input id="newCategoryName" v-model="newCategory.name" type="text" class="jh-input" placeholder="Category name" required />
                </div>
                <div class="col-md-6">
                  <label for="newCategoryDescription" class="jh-label">Description</label>
                  <input id="newCategoryDescription" v-model="newCategory.description" type="text" class="jh-input" placeholder="Category description" required />
                </div>
                <div class="col-12 d-flex justify-content-end gap-2">
                  <button type="button" class="btn-ghost" @click="closeNewCategoryForm">Cancel</button>
                  <button type="button" class="btn-mini-primary" :disabled="savingCategory || !newCategory.name.trim() || !newCategory.description.trim()" @click="createCategory">
                    {{ savingCategory ? 'Creating...' : 'Create category' }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="col-12">
            <label for="description" class="jh-label">Description</label>
            <textarea id="description" v-model="vacancy.description" class="jh-input" rows="2" placeholder="Short summary of the vacancy" required></textarea>
          </div>

          <div class="col-md-6">
            <label for="salary" class="jh-label">Salary</label>
            <input id="salary" v-model="vacancy.salary" type="number" step="0.01" min="0" class="jh-input" placeholder="0.00" />
          </div>

          <div class="col-md-6">
            <label for="companyId" class="jh-label">Company</label>
            <input id="companySearch" v-model="companySearch" type="search" class="jh-input mb-2" placeholder="Search companies..." />
            <select id="companyId" v-model="vacancy.companyId" class="jh-select" required>
              <option value="" disabled>Select a company</option>
              <option v-for="company in filteredCompanies" :key="company.id" :value="company.id">{{ company.name }}</option>
            </select>
          </div>
        </div>
      </div>

      <!-- ═══ DATES ═══ -->
      <div class="form-card">
        <div class="form-card-title">
          <span class="section-bar"></span>Dates
        </div>

        <div class="row g-3">
          <div class="col-md-6">
            <label for="openDate" class="jh-label">Open Date</label>
            <input id="openDate" v-model="vacancy.openDate" type="date" class="jh-input" :min="today" required />
          </div>

          <div class="col-md-6">
            <label for="closeDate" class="jh-label">Close Date</label>
            <input id="closeDate" v-model="vacancy.closeDate" type="date" class="jh-input" :min="vacancy.openDate || today" />
          </div>
        </div>
      </div>

      <!-- ═══ LOCATION & MEDIA ═══ -->
      <div class="form-card">
        <div class="form-card-title">
          <span class="section-bar"></span>Location and media
        </div>

        <div class="row g-3">
          <div class="col-md-6">
            <label for="locationIds" class="jh-label">Locations</label>
            <input id="locationSearch" v-model="locationSearch" type="search" class="jh-input mb-2" placeholder="Search locations... (Ctrl+click for multiple)" />
            <select id="locationIds" v-model="selectedLocationIds" class="jh-select" multiple size="7" required @change="handleLocationChange">
              <option v-for="location in filteredLocations" :key="location.id" :value="location.id">{{ formatLocation(location) }}</option>
              <option value="__other__">+ Other (create new)</option>
            </select>
            <div class="jh-helptext">{{ selectedLocationsSummary }}</div>
          </div>

          <div class="col-md-6">
            <label for="image" class="jh-label">Image</label>
            <input id="image" v-model="vacancy.image" type="text" name="image" class="jh-input" maxlength="255" placeholder="logo.png or https://example.com/logo.png" />
            <div class="jh-helptext">Optional. Use a file name from the public images folder or an image URL.</div>
            <img v-if="imagePreview" class="vacancy-image-preview mt-3" :src="imagePreview" alt="Vacancy image preview" />
          </div>

          <!-- New location inline form -->
          <div v-if="showNewLocationForm" class="col-12">
            <div class="inline-card">
              <div class="inline-card-title">Create Location</div>
              <div class="row g-3">
                <div class="col-md-6">
                  <label for="newLocationCity" class="jh-label">City</label>
                  <input id="newLocationCity" v-model="newLocation.city" type="text" class="jh-input" placeholder="City" required />
                </div>
                <div class="col-md-6">
                  <label for="newLocationState" class="jh-label">State</label>
                  <input id="newLocationState" v-model="newLocation.state" type="text" class="jh-input" placeholder="State" required />
                </div>
                <div class="col-12 d-flex justify-content-end gap-2">
                  <button type="button" class="btn-ghost" @click="closeNewLocationForm">Cancel</button>
                  <button type="button" class="btn-mini-primary" :disabled="savingLocation || !newLocation.city.trim() || !newLocation.state.trim()" @click="createLocation">
                    {{ savingLocation ? 'Creating...' : 'Create location' }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="col-12">
            <label for="details" class="jh-label">Details</label>
            <textarea id="details" v-model="vacancy.details" class="jh-input" rows="5" placeholder="Responsibilities, requirements, benefits, or any extra information"></textarea>
          </div>
        </div>
      </div>

      <!-- ═══ PUBLISHING (edit only) ═══ -->
      <div v-if="isEdit" class="form-card">
        <div class="form-card-title">
          <span class="section-bar"></span>Publishing
        </div>

        <div class="row g-3">
          <div class="col-md-4">
            <label for="status" class="jh-label">Status</label>
            <select id="status" v-model="vacancy.status" class="jh-select" required>
              <option value="PUBLISHED">Published</option>
              <option value="OPEN">Open</option>
              <option value="CLOSED">Closed</option>
            </select>
          </div>

          <div class="col-md-4">
            <label for="workMode" class="jh-label">Work Mode</label>
            <select id="workMode" v-model="vacancy.workMode" class="jh-select" required>
              <option value="REMOTE">Remote</option>
              <option value="ONSITE">Onsite</option>
              <option value="HYBRID">Hybrid</option>
            </select>
          </div>

          <div class="col-md-4">
            <label for="employmentType" class="jh-label">Employment Type</label>
            <select id="employmentType" v-model="vacancy.employmentType" class="jh-select" required>
              <option value="FULL_TIME">Full time</option>
              <option value="PART_TIME">Part time</option>
              <option value="CONTRACT">Contract</option>
              <option value="INTERNSHIP">Internship</option>
              <option value="TEMPORARY">Temporary</option>
            </select>
          </div>

          <div class="col-12">
            <label class="jh-checkbox">
              <input id="featured" v-model="vacancy.featured" type="checkbox" />
              <span>Featured vacancy</span>
            </label>
          </div>
        </div>
      </div>

      <!-- ═══ ACTIONS ═══ -->
      <div class="d-flex justify-content-end gap-2 mt-3 mb-5">
        <button type="button" class="btn-cancel" @click="router.push('/vacancies/list')">Cancel</button>
        <button type="submit" class="btn-submit" :disabled="saving">
          {{ saving ? 'Saving...' : (isEdit ? 'Update Vacancy' : 'Save Vacancy') }}
        </button>
      </div>

    </form>
  </main>
</template>