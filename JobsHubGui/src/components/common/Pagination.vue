<script setup>
import '../../assets/css/pagination.css'

const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['page-change'])

function goToPage(page) {
  if (page < 1 || page > props.totalPages) return
  emit('page-change', page)
}
</script>

<template>
  <nav class="pagination-nav" aria-label="Pagination">
    <ul class="pagination-list">
      <li :class="['pagination-item', currentPage === 1 ? 'disabled' : '']">
        <span class="pagination-link" @click="goToPage(currentPage - 1)">
          &laquo; Previous
        </span>
      </li>

      <li
          v-for="page in totalPages"
          :key="page"
          :class="['pagination-item', page === currentPage ? 'active' : '']"
      >
        <span class="pagination-link" @click="goToPage(page)">
          {{ page }}
        </span>
      </li>

      <li :class="['pagination-item', currentPage === totalPages ? 'disabled' : '']">
        <span class="pagination-link" @click="goToPage(currentPage + 1)">
          Next &raquo;
        </span>
      </li>
    </ul>
  </nav>
</template>
