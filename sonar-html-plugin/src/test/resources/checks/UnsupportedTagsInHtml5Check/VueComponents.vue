<template>
  <div>
    <!-- Vue Bootstrap component - should NOT be flagged -->
    <BLink :to="{ path: `/members/${data.item.memberId}` }">{{ data.item.fullName }}</BLink>

    <!-- Deprecated HTML blink tag - SHOULD be flagged -->
    <blink>Deprecated</blink>

    <!-- More Vue components that match deprecated tag names - should NOT be flagged -->
    <BigButton>Click me</BigButton>
    <FontAwesome name="home" />
    <CenterLayout>Content</CenterLayout>

    <!-- kebab-case Vue components - should NOT be flagged -->
    <b-link to="/home">Home</b-link>
  </div>
</template>
