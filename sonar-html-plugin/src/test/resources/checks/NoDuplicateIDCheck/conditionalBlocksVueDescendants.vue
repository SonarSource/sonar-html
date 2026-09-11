<template>
<div v-if="condition">
  <span id="test">First</span>
</div>
<div v-else>
  <span id="test">Second</span>
</div>
<div v-if="showDetails">
  <span id="inside-branch">First</span>
  <span id="inside-branch">Second</span>
</div>
<span id="shared">Always rendered</span>
<div v-if="showDetails">
  <span id="shared">Conditionally rendered</span>
</div>
</template>
