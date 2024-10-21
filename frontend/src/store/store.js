import { configureStore } from '@reduxjs/toolkit';

import modalSlice from '@store/slices/modalSlice';

export const store = configureStore({
  reducer: {
    modal: modalSlice
  }
});
