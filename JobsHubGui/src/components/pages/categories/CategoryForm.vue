<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const category = ref({
  name: '',
  description: ''
})

const errors = ref([])
const router = useRouter()
const route = useRoute()

const categoryId = computed(() => route.params.id)
const isEditing = computed(() => Boolean(categoryId.value))

const loadCategory = async () => {
  if (!isEditing.value) {
    return
  }

  try {
    const response = await fetch(`http://localhost:8080/api/categories/${categoryId.value}`)

    if (!response.ok) {
      errors.value = [`Error loading category ${categoryId.value}`]
      return
    }

    category.value = await response.json()
  } catch (err) {
    errors.value = [err.message]
  }
}

const handleSubmit = async () => {
  errors.value = []

  const url = isEditing.value
      ? `http://localhost:8080/api/categories/update/${categoryId.value}`
      : 'http://localhost:8080/api/categories/create'

  const method = isEditing.value ? 'PUT' : 'POST'

  try {
    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(category.value)
    })

    if (!response.ok) {
      const body = await response.json()
      errors.value = typeof body?.message === 'object'
          ? Object.values(body.message)
          : [body?.message || `Error ${response.status}`]
      return
    }

    router.push(
        isEditing.value
            ? `/categories/list?updated=true&name=${encodeURIComponent(category.value.name)}`
            : '/categories/list?created=true'
    )
  } catch (err) {
    errors.value = [err.message]
  }
}

onMounted(() => {
  loadCategory()
})
</script>

<template>

  <!-- ── PAGE HEADER ── -->
  <div class="page-header">
    <div class="container" style="max-width:700px;">
      <div class="breadcrumb-custom">
        <i class="fa-solid fa-layer-group"></i>
        Categories / {{ isEditing ? 'Edit' : 'New' }}
      </div>
      <h1 class="page-title">{{ isEditing ? 'Edit Category' : 'New Category' }}</h1>
      <p class="page-subtitle">
        {{ isEditing ? 'Update the details for this category.' : 'Create a new category to organize vacancies.' }}
      </p>
    </div>
  </div>

  <!-- ── MAIN ── -->
  <main class="container py-4" style="max-width:700px;">

    <div v-if="errors.length" class="form-alert form-alert-error">
      <ul class="mb-0 ps-3">
        <li v-for="(msg, i) in errors" :key="i">{{ msg }}</li>
      </ul>
    </div>

    <form @submit.prevent="handleSubmit">
      <div class="form-card">
        <div class="form-card-title">
          <span class="section-bar"></span>Category details
        </div>

        <div class="row g-3">
          <div class="col-12">
            <label for="name" class="jh-label">Name</label>
            <input
                id="name"
                v-model="category.name"
                type="text"
                name="name"
                class="jh-input"
                placeholder="Enter the category name"
            />
          </div>

          <div class="col-12">
            <label for="description" class="jh-label">Description</label>
            <textarea
                id="description"
                v-model="category.description"
                name="description"
                class="jh-input"
                rows="3"
                placeholder="Enter a short description"
            ></textarea>
          </div>
        </div>
      </div>

      <div class="d-flex justify-content-end gap-2 mt-3 mb-5">
        <button type="button" class="btn-cancel" @click="router.push('/categories/list')">Cancel</button>
        <button type="submit" title="Save category" class="btn-submit">
          {{ isEditing ? 'Update Category' : 'Save Category' }}
        </button>
      </div>
    </form>

  </main>
</template>