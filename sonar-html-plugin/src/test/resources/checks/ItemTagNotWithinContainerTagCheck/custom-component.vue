<ComboboxOption
  v-for="option in filteredOptions"
  :key="option.value"
  v-slot="{ selected, active }"
  :value="option"
>
  <li
    :class="[
      'relative cursor-default select-none py-2 pl-10 pr-4',
      active ? 'bg-primary-500 text-white' : 'text-secondary-500',
    ]"
  >
    {{ option.label }}
  </li>
</ComboboxOption>
