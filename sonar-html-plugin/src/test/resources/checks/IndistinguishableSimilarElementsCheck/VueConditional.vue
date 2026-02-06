<template>
  <!-- Compliant: mutually exclusive v-if/v-else -->
  <nav v-if="showPrimary">Primary</nav>
  <nav v-else>Secondary</nav>

  <!-- Compliant: mutually exclusive v-if/v-else-if/v-else -->
  <aside v-if="mode === 'a'">A</aside>
  <aside v-else-if="mode === 'b'">B</aside>
  <aside v-else>C</aside>

  <!-- Noncompliant: unconditional duplicate navs without aria-label -->
  <nav>First</nav>
  <nav>Second</nav>
</template>
