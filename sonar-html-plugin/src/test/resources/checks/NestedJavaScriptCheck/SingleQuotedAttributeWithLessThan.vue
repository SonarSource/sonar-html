<template>
    <div class="container">
        <section>
            <CheckerSearch v-model="searchValue"  @search="handleSearch" :disabled='searchValue.length<1' />
        </section>
    </div>
</template>

<script>
    setTimeout(() => {  })
</script>
