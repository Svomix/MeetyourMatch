import { configureStore } from '@reduxjs/toolkit';
import modalSlice from '@store/modalSlice';
import authSlice from './authSlice';

// const reHydrateStore = () => {
//   if (typeof window === 'undefined') return undefined;
//   if (localStorage.getItem('appState') !== null)
//     return JSON.parse(localStorage.getItem('appState'));
// };

const appMiddleware = ({ getState }) => {
  return (next) => (action) => {
    const result = next(action);
    localStorage.setItem('appState', JSON.stringify(getState()));
    return result;
  };
};

export const store = configureStore({
  reducer: {
    modal: modalSlice,
    auth: authSlice
  },
  // preloadedState: reHydrateStore(),
  middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(appMiddleware)
});
