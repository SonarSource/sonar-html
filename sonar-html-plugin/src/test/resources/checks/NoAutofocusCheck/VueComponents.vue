<template>
  <div>
    <!-- Vue component (PascalCase) - autofocus is a prop, not the DOM attribute, so should NOT be flagged -->
    <CustomInput autofocus />

    <!-- Vue component (kebab-case) - same as above, should NOT be flagged -->
    <custom-input autofocus />

    <!-- Vue component whose name collides with a known HTML tag case-insensitively
         (e.g. Ant Design's Input) - PascalCase still wins over the tag whitelist, so should NOT be flagged -->
    <Input autofocus />

    <!-- Native input - SHOULD still be flagged -->
    <input autofocus />
  </div>
</template>
