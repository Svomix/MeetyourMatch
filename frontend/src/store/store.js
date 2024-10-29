import { configureStore } from '@reduxjs/toolkit';

import modalSlice from '@store/slices/modalSlice';

const reHydrateStore = () => {
  if (typeof window === 'undefined') {
    return undefined;
  }
  if (localStorage.getItem('appState') !== null) {
    return JSON.parse(localStorage.getItem('appState')); // re-hydrate the store
  }
};

const appMiddleware = ({ getState }) => {
  return (next) => (action) => {
    const result = next(action);
    localStorage.setItem('appState', JSON.stringify(getState()));
    return result;
  };
};

export const store = configureStore({
  reducer: {
    modal: modalSlice
  },
  preloadedState: reHydrateStore(),
  middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(appMiddleware)
});
