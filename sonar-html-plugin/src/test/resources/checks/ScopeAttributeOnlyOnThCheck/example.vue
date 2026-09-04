<template>
  <!-- Matches "th" by name before the component checks below even run, so no violation either way (possible false negative if this is a component) -->
  <Th scope="row">Total</Th>

  <!-- Unresolved custom component: "scope" may be an arbitrary prop unrelated to table headers, so not flagged -->
  <HeaderCell scope="row">Total</HeaderCell>

  <!-- PascalCase wins over a tag-name collision: "Caption" is a Vue component here, not the native <caption> tag -->
  <Caption scope="row">Total</Caption>

  <!-- Vue 2.0-2.4 legacy scoped slot syntax: "scope" here is unrelated to table headers -->
  <todo-list>
    <template scope="props">{{ props.text }}</template>
  </todo-list>
</template>
