<template>
  <div v-if="isVisible" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <button class="close-button" @click="closeModal">×</button>
      <slot name="modal-content"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">

import { defineProps, defineEmits} from 'vue';
const props = defineProps<{
  isVisible: boolean;
}>();
const emit = defineEmits<{
  (event: 'update:isVisible', value: boolean): void;
}>();
const closeModal = () => {
  emit('update:isVisible', false);
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: center;
  align-items: center;
  backdrop-filter: blur(5px);
  z-index: 1000;
}

.modal-content {
  background: #353637;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  position: relative;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: transparent;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: white;
}
</style>
