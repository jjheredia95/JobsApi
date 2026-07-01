<script setup>
  import { onBeforeUnmount, onMounted, ref } from 'vue'
  import { RouterView, RouterLink } from 'vue-router'
  import { Collapse } from 'bootstrap'

  const navbarRef = ref(null)
  const menuRef   = ref(null)

  const closeNavbar = () => {
    if (!menuRef.value?.classList.contains('show')) return
    Collapse.getOrCreateInstance(menuRef.value, { toggle: false }).hide()
  }

  const handleDocumentClick = (event) => {
    if (!navbarRef.value || navbarRef.value.contains(event.target)) return
    closeNavbar()
  }

  onMounted(() => document.addEventListener('click', handleDocumentClick))
  onBeforeUnmount(() => document.removeEventListener('click', handleDocumentClick))
</script>

<template>

  <!-- ── NAVBAR ── -->
  <nav ref="navbarRef" class="jh-navbar navbar navbar-expand-lg sticky-top px-3 px-md-4">

    <!-- Brand -->
    <RouterLink class="jh-brand navbar-brand" to="/">
      Jobs<span>Hub</span>
    </RouterLink>

    <!-- Hamburger -->
    <button
        class="navbar-toggler border-0 ms-auto"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#mainNavbar"
        aria-controls="mainNavbar"
        aria-expanded="false"
        aria-label="Toggle navigation"
    >
      <span class="navbar-toggler-icon" style="filter:invert(1);"></span>
    </button>

    <!-- Collapsible links -->
    <div id="mainNavbar" ref="menuRef" class="collapse navbar-collapse">
      <ul class="navbar-nav ms-4 gap-1">
        <li class="nav-item">
          <RouterLink class="jh-nav-link" to="/vacancies/list" @click="closeNavbar">Jobs</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="jh-nav-link" to="/categories/list" @click="closeNavbar">Categories</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="jh-nav-link" to="/companies" @click="closeNavbar">Companies</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="jh-nav-link" to="/users" @click="closeNavbar">Users</RouterLink>
        </li>
      </ul>
    </div>

    <!-- Auth buttons always at far right -->
    <div class="d-flex gap-2 ms-auto align-items-center">
      <button class="jh-btn-signin">Sign In</button>
      <button class="jh-btn-signup">Sign Up</button>
    </div>

  </nav>

  <!-- ── PAGE CONTENT ── -->
  <main>
    <RouterView />
  </main>

  <!-- ── FOOTER ── -->
  <footer class="jh-footer py-3 text-center">
    © 2026 JobsHub, Inc. &nbsp;·&nbsp;
    WebApp developed with Vue 3 · Author: Juan Heredia &nbsp;·&nbsp;
    <a href="#">Privacy</a> &nbsp;·&nbsp;
    <a href="#">Terms</a>
  </footer>

</template>