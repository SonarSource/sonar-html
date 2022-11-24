<template>
  {{  (3 < 5 ? "foo" : "bar") }}
</template>

<script>
export default {
 mounted() {
   // 5 > 0 ? foo : bar;
 },
}
</script>
