<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'

const categories = ref([])
const loading = ref(true)
const error = ref('')
const route = useRoute()
const created = ref(route.query.created === 'true')
const updated = ref(route.query.updated === 'true')
const updatedName = ref(route.query.name || '')

function truncateDescription(description) {
  if (!description) {
    return 'No description'
  }

  return description.length > 96 ? `${description.slice(0, 96)}...` : description
}

async function loadCategories() {
  try {
    const response = await fetch('http://localhost:8080/api/categories')
    if (!response.ok) {
      // noinspection ExceptionCaughtLocallyJS
      throw new Error("There was an error loading the categories.")
    }
    categories.value = await response.json()
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

async function deleteCategory(id) {
  const confirmed = window.confirm('Are you sure you want to delete this category?')

  if (!confirmed) {
    return
  }

  try {
    const response = await fetch(`http://localhost:8080/api/categories/delete/${id}`, {
      method: 'DELETE'
    })

    if (!response.ok) {
      // noinspection ExceptionCaughtLocallyJS
      throw new Error('There was an error deleting the category.')
    }

    categories.value = categories.value.filter(category => category.id !== id)
  } catch (err) {
    error.value = err.message
  }
}

onMounted(() => {
  loadCategories()
})

</script>

<template>

  <!-- ── PAGE HEADER ── -->
  <div class="page-header">
    <div class="container" style="max-width:1100px;">
      <div class="breadcrumb-custom">
        <i class="fa-solid fa-layer-group"></i>
        Categories
      </div>
      <h1 class="page-title">Category List</h1>
      <p class="page-subtitle">Organize vacancy areas so candidates can browse opportunities faster.</p>

      <RouterLink class="btn-new" to="/categories/new">
        <i class="fa-solid fa-plus"></i>
        New Category
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
      Category created successfully.
    </div>
    <div v-if="updated" class="alert alert-success" role="alert">
      Category <strong>{{ updatedName }}</strong> updated successfully.
    </div>

    <template v-if="!error">

      <div class="d-flex align-items-center mb-3">
        <h2 class="section-title mb-0">
          <span class="section-bar"></span>All Categories
          <span class="badge-count">{{ categories.length }} results</span>
        </h2>
      </div>

      <div class="admin-table-wrap">
        <table class="admin-table">
          <thead>
          <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Description</th>
            <th style="text-align:right;">Actions</th>
          </tr>
          </thead>

          <tbody>
          <tr v-if="loading">
            <td colspan="4" class="empty-cell">Loading categories...</td>
          </tr>

          <tr v-else v-for="category in categories" :key="category.id">
            <td class="id-cell">#{{ category.id }}</td>
            <td class="name-cell">{{ category.name }}</td>
            <td class="desc-cell">{{ truncateDescription(category.description) }}</td>
            <td style="text-align:right;">
              <RouterLink
                  :to="`/categories/edit/${category.id}`"
                  class="action-btn action-edit"
                  title="Edit"
              >
                <i class="fas fa-pencil-alt"></i>
              </RouterLink>
              <button
                  type="button"
                  class="action-btn action-delete"
                  title="Delete"
                  @click="deleteCategory(category.id)"
              >
                <i class="fas fa-trash"></i>
              </button>
            </td>
          </tr>

          <tr v-if="!loading && categories.length === 0">
            <td colspan="4" class="empty-cell">No categories found.</td>
          </tr>
          </tbody>
        </table>
      </div>

    </template>

  </main>
</template>