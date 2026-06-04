<template>
  <!-- Compliant: legend is a direct child of fieldset -->
  <fieldset>
    <legend>Section</legend>
    <input type="text" />
  </fieldset>

  <!-- Compliant: legend inside a transparent template wrapper directly under fieldset -->
  <fieldset>
    <template v-if="show">
      <legend>Section</legend>
    </template>
  </fieldset>

  <!-- Noncompliant: legend wrapped in div -->
  <fieldset>
    <div>
      <legend>Section</legend> <!-- Noncompliant -->
    </div>
  </fieldset>
</template>
