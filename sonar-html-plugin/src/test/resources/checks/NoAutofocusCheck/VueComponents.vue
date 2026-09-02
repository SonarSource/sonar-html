<template>
  <div>
    <!-- Vue component (PascalCase) - autofocus is a prop, not the DOM attribute, so should NOT be flagged -->
    <CustomInput autofocus />

    <!-- Vue component (kebab-case) - same as above, should NOT be flagged -->
    <custom-input autofocus />

    <!-- Native input - SHOULD still be flagged -->
    <input autofocus />
  </div>
</template>
