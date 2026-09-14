<template>
<main>
<div v-if="showFirst"><span id="independent">First</span></div>
<div v-if="showSecond"><span id="independent">Second</span></div>
<div v-if="enabled"><span id="same-condition">First</span></div>
<div v-if="enabled"><span id="same-condition">Second</span></div>
</main>
</template>
