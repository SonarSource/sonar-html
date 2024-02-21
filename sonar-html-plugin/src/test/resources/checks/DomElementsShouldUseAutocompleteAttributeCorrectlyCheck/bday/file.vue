<template>
  <div autocomplete="bday"></div> <!-- Noncompliant -->
  <input autocomplete="bday"/> <!-- Noncompliant -->
  <input type="url" autocomplete="bday"/> <!-- Noncompliant -->
  <input type="date" autocomplete="bday"/>
  <input type="text" autocomplete="bday"/>
  <select autocomplete="bday"></select>
  <textarea autocomplete="bday"></textarea>
  <input type="hidden" autocomplete="bday"/>

  <div v-bind:autocomplete="bday"></div>
  <input v-bind:autocomplete="bday"/>
  <input type="url" v-bind:autocomplete="bday"/>
  <input type="date" v-bind:autocomplete="bday"/>
  <input type="text" v-bind:autocomplete="bday"/>
  <select v-bind:autocomplete="bday"></select>
  <textarea v-bind:autocomplete="bday"></textarea>
  <input type="hidden" v-bind:autocomplete="bday"/>

  <div :autocomplete="bday"></div>
  <input :autocomplete="bday"/>
  <input type="url" :autocomplete="bday"/>
  <input type="date" :autocomplete="bday"/>
  <input type="text" :autocomplete="bday"/>
  <select :autocomplete="bday"></select>
  <textarea :autocomplete="bday"></textarea>
  <input type="hidden" :autocomplete="bday"/>
</template>
<script setup>
</script>