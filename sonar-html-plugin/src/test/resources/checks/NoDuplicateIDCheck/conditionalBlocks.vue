<template>
  <div>
    <!-- Compliant: IDs in v-if/v-else-if blocks are mutually exclusive -->
    <InputText
       v-if="is_freight_charge_type"
       id="fixed_surcharge_name"
       model-value="Ocean Freight, BAS"
       disabled
    />
    <InputText
       v-else-if="is_inland_shipment_type"
       id="fixed_surcharge_name"
       model-value="INLAND"
       disabled
    />
    <InputText
       v-else
       id="fixed_surcharge_name"
       model-value="Default"
       disabled
    />

    <!-- Compliant: IDs in v-if/v-else blocks -->
    <CmButton v-if="canEdit" id="action-btn" @click="edit">Edit</CmButton>
    <CmButton v-else id="action-btn" @click="goBack">Back</CmButton>

    <!-- Non-compliant: Duplicate IDs outside conditionals -->
    <div id="static-elem">Element 1</div>
    <div id="static-elem">Element 2</div>
  </div>
</template>
