<script setup>
import "../../assets/css/Home.css"
import { onMounted, reactive, ref } from 'vue'
import { RouterLink } from 'vue-router'
import Pagination from '../common/Pagination.vue'

// ── State ──────────────────────────────────────────────
const vacancies      = ref([])
const totalPages     = ref(0)
const totalElements  = ref(0)
const currentPage    = ref(0)
const loading        = ref(false)
const error          = ref(false)
const pageSize       = 4
const locOpen        = ref(false)
const filtersOpen    = ref(false)
const suggestions    = ref([])
const locInput       = ref(null)
let debounceTimer    = null

const filters = reactive({
  keyword:    '',
  location:   '',
  category:   '',
  workMode:   '',
  employment: '',
})

const stats = reactive({
  totalJobs:       0,
  totalCompanies:  340,
  totalCategories: 0,
})

// ── Location data ──────────────────────────────────────
const DEFAULT_LOCS = [
  'New York, NY', 'Los Angeles, CA', 'Chicago, IL', 'Houston, TX',
  'Phoenix, AZ', 'San Francisco, CA', 'Seattle, WA', 'Austin, TX',
  'Miami, FL', 'Remote',
]

const US_CITIES = [
  'Akron, OH','Albuquerque, NM','Alexandria, VA','Anaheim, CA','Anchorage, AK',
  'Arlington, TX','Arlington, VA','Atlanta, GA','Aurora, CO','Aurora, IL',
  'Austin, TX','Bakersfield, CA','Baltimore, MD','Baton Rouge, LA','Birmingham, AL',
  'Bloomfield, CT','Bloomington, MN','Boston, MA','Bridgeport, CT','Buffalo, NY',
  'Chandler, AZ','Charlotte, NC','Chesapeake, VA','Chicago, IL','Chula Vista, CA',
  'Cincinnati, OH','Cleveland, OH','Colorado Springs, CO','Columbus, OH','Corpus Christi, TX',
  'Dallas, TX','Danbury, CT','Denver, CO','Detroit, MI','Durham, NC',
  'El Paso, TX','Fairfield, CT','Fort Collins, CO','Fort Wayne, IN','Fort Worth, TX',
  'Fremont, CA','Fresno, CA','Garland, TX','Gilbert, AZ','Glendale, AZ',
  'Glendale, CA','Grand Rapids, MI','Greensboro, NC','Greenwich, CT','Hartford, CT',
  'Henderson, NV','Hialeah, FL','Honolulu, HI','Houston, TX','Huntington Beach, CA',
  'Huntsville, AL','Indianapolis, IN','Irvine, CA','Irving, TX','Jacksonville, FL',
  'Jersey City, NJ','Kansas City, KS','Kansas City, MO','Laredo, TX','Las Vegas, NV',
  'Lexington, KY','Lincoln, NE','Long Beach, CA','Los Angeles, CA','Louisville, KY',
  'Lubbock, TX','Madison, WI','Memphis, TN','Mesa, AZ','Miami, FL',
  'Milwaukee, WI','Minneapolis, MN','Modesto, CA','Montgomery, AL','Nashville, TN',
  'New Haven, CT','New Orleans, LA','New York, NY','Newark, NJ','Norfolk, VA',
  'North Las Vegas, NV','Oakland, CA','Oklahoma City, OK','Omaha, NE','Orlando, FL',
  'Philadelphia, PA','Phoenix, AZ','Pittsburgh, PA','Plano, TX','Portland, OR',
  'Raleigh, NC','Reno, NV','Richmond, VA','Riverside, CA','Rochester, NY',
  'Sacramento, CA','San Antonio, TX','San Diego, CA','San Francisco, CA','San Jose, CA',
  'Santa Ana, CA','Scottsdale, AZ','Seattle, WA','Spokane, WA','St. Louis, MO',
  'St. Paul, MN','Stamford, CT','Stockton, CA','Tampa, FL','Toledo, OH',
  'Tucson, AZ','Tulsa, OK','Virginia Beach, VA','Washington, DC','Waterbury, CT',
  'Wichita, KS','Winston-Salem, NC','Remote',
]

const STATE_ABBR = {
  'Alabama':'AL','Alaska':'AK','Arizona':'AZ','Arkansas':'AR','California':'CA',
  'Colorado':'CO','Connecticut':'CT','Delaware':'DE','Florida':'FL','Georgia':'GA',
  'Hawaii':'HI','Idaho':'ID','Illinois':'IL','Indiana':'IN','Iowa':'IA',
  'Kansas':'KS','Kentucky':'KY','Louisiana':'LA','Maine':'ME','Maryland':'MD',
  'Massachusetts':'MA','Michigan':'MI','Minnesota':'MN','Mississippi':'MS',
  'Missouri':'MO','Montana':'MT','Nebraska':'NE','Nevada':'NV','New Hampshire':'NH',
  'New Jersey':'NJ','New Mexico':'NM','New York':'NY','North Carolina':'NC',
  'North Dakota':'ND','Ohio':'OH','Oklahoma':'OK','Oregon':'OR','Pennsylvania':'PA',
  'Rhode Island':'RI','South Carolina':'SC','South Dakota':'SD','Tennessee':'TN',
  'Texas':'TX','Utah':'UT','Vermont':'VT','Virginia':'VA','Washington':'WA',
  'West Virginia':'WV','Wisconsin':'WI','Wyoming':'WY','District of Columbia':'DC',
}

// ── Location helpers ───────────────────────────────────
function onLocFocus() {
  const q = filters.location.trim().toLowerCase()
  suggestions.value = q.length >= 2
      ? US_CITIES.filter(c => c.toLowerCase().includes(q)).slice(0, 8)
      : DEFAULT_LOCS
  locOpen.value = true
}

function onLocInput(e) {
  filters.location = e.target.value
  clearTimeout(debounceTimer)
  const val = filters.location.trim()
  if (!val) { suggestions.value = DEFAULT_LOCS; locOpen.value = true; return }
  if (val.length < 2) return
  const q = val.toLowerCase()
  suggestions.value = US_CITIES.filter(c => c.toLowerCase().includes(q)).slice(0, 8)
  locOpen.value = true
  debounceTimer = setTimeout(() => fetchCitiesFromApi(val), 350)
}

function onLocBlur() {
  setTimeout(() => { locOpen.value = false }, 160)
}

function selectCity(city) {
  filters.location = city
  locOpen.value    = false
}

async function fetchCitiesFromApi(q) {
  try {
    const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(q)}&countrycodes=us&featuretype=city&format=json&limit=8&addressdetails=1`
    const res  = await fetch(url, { headers: { 'Accept-Language': 'en-US,en' } })
    const data = await res.json()
    const seen = new Set(); const cities = []
    for (const item of data) {
      const addr  = item.address || {}
      const city  = addr.city || addr.town || addr.village || addr.hamlet || ''
      const abbr  = STATE_ABBR[addr.state || ''] || ''
      if (!city || !abbr) continue
      const label = `${city}, ${abbr}`
      if (!seen.has(label)) { seen.add(label); cities.push(label) }
    }
    if ('remote'.startsWith(q.toLowerCase())) cities.unshift('Remote')
    if (cities.length > 0) suggestions.value = cities
  } catch { /* local results already showing */ }
}

// ── Filters ────────────────────────────────────────────
function toggleFilters() { filtersOpen.value = !filtersOpen.value }

function setFilter(key, value) { filters[key] = value }

const categoryOptions = [
  { value: '',           label: 'All' },
  { value: 'technology', label: '💻 Technology' },
  { value: 'healthcare', label: '🏥 Healthcare' },
  { value: 'finance',    label: '💰 Finance' },
  { value: 'marketing',  label: '📣 Marketing' },
  { value: 'education',  label: '🎓 Education' },
]
const workModeOptions = [
  { value: '',       label: 'All' },
  { value: 'remote', label: '🏠 Remote' },
  { value: 'onsite', label: '🏢 On-site' },
  { value: 'hybrid', label: '🔀 Hybrid' },
]
const employmentOptions = [
  { value: '',           label: 'All' },
  { value: 'fulltime',   label: '⏱ Full-time' },
  { value: 'parttime',   label: '🕐 Part-time' },
  { value: 'contract',   label: '📋 Contract' },
  { value: 'internship', label: '🎓 Internship' },
]

// ── Job card helpers ───────────────────────────────────
const GRADIENTS = [
  'linear-gradient(135deg,#6366F1,#8B5CF6)',
  'linear-gradient(135deg,#0EA5E9,#0284C7)',
  'linear-gradient(135deg,#10B981,#059669)',
  'linear-gradient(135deg,#F59E0B,#D97706)',
  'linear-gradient(135deg,#F43F5E,#E11D48)',
  'linear-gradient(135deg,#8B5CF6,#6D28D9)',
]

function logoGradient(vacancy) {
  return GRADIENTS[(vacancy.id ?? 0) % GRADIENTS.length]
}

function vacancyInitial(vacancy) {
  return (vacancy.name || '?')[0].toUpperCase()
}

function vacancyCategory(vacancy) {
  const c = vacancy.category
  if (!c) return 'General'
  return typeof c === 'string' ? c : c.name || 'General'
}

function vacancyWorkMode(vacancy) {
  return vacancy.workMode || vacancy.work_mode || 'On-site'
}

function workModeBadgeClass(vacancy) {
  const wm = vacancyWorkMode(vacancy).toLowerCase()
  if (wm.includes('remote')) return 'badge-wm-remote'
  if (wm.includes('hybrid')) return 'badge-wm-hybrid'
  return 'badge-wm-onsite'
}

function vacancyLocations(vacancy) {
  const loc = vacancy.locations || vacancy.location
  if (!loc) return []
  if (Array.isArray(loc)) return loc.map(l => typeof l === 'string' ? l : l.name || l.city || '')
  return [loc]
}

function vacancyDaysAgo(vacancy) {
  const d = vacancy.publishedDate || vacancy.date || vacancy.createdAt
  if (!d) return ''
  const diff = Math.floor((Date.now() - new Date(d)) / 86400000)
  if (diff === 0) return 'Today'
  if (diff === 1) return 'Yesterday'
  return `${diff} days ago`
}

// ── API ────────────────────────────────────────────────
async function loadData(page = 0) {
  loading.value = true
  error.value   = false
  try {
    const params = new URLSearchParams({ page, size: pageSize })
    if (filters.keyword)    params.append('description', filters.keyword)
    if (filters.category)   params.append('category',    filters.category)
    if (filters.location)   params.append('location',    filters.location)
    if (filters.workMode)   params.append('workMode',    filters.workMode)
    if (filters.employment) params.append('employment',  filters.employment)

    const res = await fetch(`http://localhost:8080/api/home?${params}`)
    if (!res.ok) throw new Error()
    const data = await res.json()
    vacancies.value     = data.content      || []
    totalPages.value    = data.totalPages   || 0
    totalElements.value = data.totalElements || 0
    currentPage.value   = data.number       || 0
    stats.totalJobs     = data.totalElements || 0
  } catch {
    error.value     = true
    vacancies.value = []
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    const res  = await fetch('http://localhost:8080/api/categories')
    if (!res.ok) return
    const data = await res.json()
    stats.totalCategories = Array.isArray(data) ? data.length : 0
  } catch { /* silent */ }
}

function onPageChange(page) { loadData(page - 1) }

onMounted(() => { loadData(0); loadCategories() })
</script>

<template>

  <!-- ══════════════════════════════════════
       HERO
  ══════════════════════════════════════ -->
  <section class="hero">
    <div class="container" style="max-width:1320px;">
      <div class="row align-items-center g-4">

        <!-- LEFT: Title -->
        <div class="col-lg-3 text-lg-start text-center">
          <div class="hero-eyebrow">Now Hiring · {{ stats.totalJobs }}+ Open Roles</div>
          <h1>The job you want is <em>here.</em></h1>
          <p class="hero-lead">Search by keyword, location, category, or work mode.</p>
        </div>

        <!-- RIGHT: Search + Filters -->
        <div class="col-lg-9">

          <!-- Search box -->
          <div class="search-box" :class="{ 'loc-open': locOpen }">
            <div class="d-flex align-items-center gap-2 flex-wrap flex-lg-nowrap">

              <!-- Keyword — fixed half -->
              <div class="search-keyword">
                <input
                    v-model="filters.keyword"
                    type="text"
                    class="search-input form-control"
                    placeholder="Job title or keyword"
                    @keyup.enter="loadData(0)"
                />
              </div>

              <div class="search-divider d-none d-lg-flex"></div>

              <!-- Location — takes remaining space -->
              <div class="search-location loc-wrapper">
                <div class="loc-input-wrap">
                  <svg class="loc-icon" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>
                  </svg>
                  <input
                      ref="locInput"
                      type="text"
                      class="loc-text-input"
                      placeholder="Location"
                      autocomplete="off"
                      :value="filters.location"
                      @input="onLocInput"
                      @focus="onLocFocus"
                      @blur="onLocBlur"
                  />
                </div>
              </div>

              <!-- Search button -->
              <button class="btn-search d-flex align-items-center gap-2" @click="loadData(0)">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
                Search
              </button>
            </div>

            <!-- Dropdown: direct child of search-box → inherits full width -->
            <div class="loc-dropdown" :class="{ open: locOpen }">
              <div
                  v-for="city in suggestions"
                  :key="city"
                  class="loc-option"
                  @mousedown.prevent="selectCity(city)"
              >
                <svg v-if="city === 'Remote'" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/>
                </svg>
                <svg v-else width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>
                </svg>
                {{ city }}
              </div>
            </div>
          </div>

          <!-- Filters toggle -->
          <div class="mt-3">
            <button class="btn-filters" :class="{ open: filtersOpen }" @click="toggleFilters">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="4" y1="6" x2="20" y2="6"/><line x1="8" y1="12" x2="16" y2="12"/><line x1="11" y1="18" x2="13" y2="18"/>
              </svg>
              Filters
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"
                   :style="{ transform: filtersOpen ? 'rotate(180deg)' : 'rotate(0)', transition: 'transform .25s' }">
                <polyline points="6 9 12 15 18 9"/>
              </svg>
            </button>
          </div>

          <!-- Collapsible filter panel -->
          <div class="filters-panel" :class="{ open: filtersOpen }">
            <div class="filters-panel-inner">

              <!-- Category -->
              <div class="filter-row">
                <span class="filter-label">Category:</span>
                <button
                    v-for="opt in categoryOptions" :key="opt.value"
                    class="f-pill" :class="{ active: filters.category === opt.value }"
                    @click="setFilter('category', opt.value)"
                >{{ opt.label }}</button>
              </div>

              <!-- Work Mode -->
              <div class="filter-row mt-2">
                <span class="filter-label">Work mode:</span>
                <button
                    v-for="opt in workModeOptions" :key="opt.value"
                    class="f-pill" :class="{ active: filters.workMode === opt.value }"
                    @click="setFilter('workMode', opt.value)"
                >{{ opt.label }}</button>
              </div>

              <!-- Employment -->
              <div class="filter-row mt-2">
                <span class="filter-label">Employment:</span>
                <button
                    v-for="opt in employmentOptions" :key="opt.value"
                    class="f-pill" :class="{ active: filters.employment === opt.value }"
                    @click="setFilter('employment', opt.value)"
                >{{ opt.label }}</button>
              </div>

            </div>
          </div>

        </div>
      </div>
    </div>
  </section>

  <!-- ══════════════════════════════════════
       STATS STRIP
  ══════════════════════════════════════ -->
  <div class="stats-strip">
    <div class="container" style="max-width:900px;">
      <div class="row g-0">
        <div class="col-6 col-md-3 stats-item">
          <div class="stats-num">{{ stats.totalJobs }}<span>+</span></div>
          <div class="stats-label">Open positions</div>
        </div>
        <div class="col-6 col-md-3 stats-item">
          <div class="stats-num">{{ stats.totalCompanies }}<span>+</span></div>
          <div class="stats-label">Companies hiring</div>
        </div>
        <div class="col-6 col-md-3 stats-item">
          <div class="stats-num">{{ stats.totalCategories }}</div>
          <div class="stats-label">Job categories</div>
        </div>
        <div class="col-6 col-md-3 stats-item" style="border-right:none;">
          <div class="stats-num">4.8<span>★</span></div>
          <div class="stats-label">Avg. employer rating</div>
        </div>
      </div>
    </div>
  </div>

  <!-- ══════════════════════════════════════
       FEATURED VACANCIES
  ══════════════════════════════════════ -->
  <main class="container py-4" style="max-width:900px;">

    <div class="d-flex align-items-center justify-content-between mb-3">
      <h2 class="section-title mb-0">
        <span class="section-bar"></span>Featured
        <span class="badge-count ms-2">{{ totalElements }} vacancies</span>
      </h2>
      <RouterLink to="/vacancies/list" class="link-viewall">View all →</RouterLink>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border" style="color:#6366F1;" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert alert-danger">
      Could not load vacancies. Please try again.
    </div>

    <!-- Empty -->
    <div v-else-if="vacancies.length === 0" class="text-center py-5 text-muted">
      No vacancies found with those filters.
    </div>

    <!-- Cards -->
    <div v-else class="d-flex flex-column gap-3">
      <RouterLink
          v-for="vacancy in vacancies"
          :key="vacancy.id"
          :to="`/vacancies/details/${vacancy.id}`"
          class="job-card card border p-3"
      >
        <div class="d-flex align-items-start gap-3">

          <!-- Logo -->
          <div class="company-logo" :style="{ background: logoGradient(vacancy) }">
            {{ vacancyInitial(vacancy) }}
          </div>

          <!-- Body -->
          <div class="flex-fill">
            <div class="d-flex flex-wrap gap-1 mb-2">
              <span class="jh-badge badge-cat">{{ vacancyCategory(vacancy) }}</span>
              <span class="jh-badge badge-feat">Featured</span>
              <span class="jh-badge" :class="workModeBadgeClass(vacancy)">{{ vacancyWorkMode(vacancy) }}</span>
            </div>

            <div class="job-title">{{ vacancy.name }}</div>

            <div class="d-flex flex-wrap gap-3 mt-1">
              <span v-if="vacancyLocations(vacancy).length" class="job-meta-item">
                <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>
                </svg>
                {{ vacancyLocations(vacancy).join(', ') }}
              </span>
              <span class="job-meta-item">
                <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
                </svg>
                {{ vacancy.publishedDate || vacancy.date || 'No date' }}
              </span>
              <span class="status-open d-flex align-items-center gap-1">
                <span class="status-dot"></span>{{ vacancy.status || 'Open' }}
              </span>
            </div>

            <div class="job-desc">
              {{ vacancy.description || vacancy.details || vacancy.summary || 'No description available.' }}
            </div>
          </div>

          <!-- Right -->
          <div class="d-none d-md-flex flex-column align-items-end gap-2 flex-shrink-0">
            <span class="btn-view">View details →</span>
            <span class="job-date">{{ vacancyDaysAgo(vacancy) }}</span>
          </div>

        </div>
      </RouterLink>
    </div>

    <!-- Pagination -->
    <Pagination
        v-if="totalPages > 1"
        :current-page="currentPage + 1"
        :total-pages="totalPages"
        @page-change="onPageChange"
    />

  </main>

</template>