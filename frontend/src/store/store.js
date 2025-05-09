import { configureStore } from '@reduxjs/toolkit';
import modalSlice from '@store/modalSlice';
import authSlice from './authSlice';
import profileSlice from "./profileSlice";
import eventSlice from "./eventStore";
import tagSlice from "./tagStore";
import locationsSlice from "./locationStore";

// const reHydrateStore = () => {
//   if (typeof window === 'undefined') return undefined;
//   if (localStorage.getItem('appState') !== null)
//     return JSON.parse(localStorage.getItem('appState'));
// };

// const appMiddleware = ({ getState }) => {
//   return (next) => (action) => {
//     const result = next(action);
//     localStorage.setItem('appState', JSON.stringify(getState()));
//     return result;
//   };
// };

export const store = configureStore({
  reducer: {
    modal: modalSlice,
    auth: authSlice,
    profileInfo: profileSlice,
    eventInfo: eventSlice,
    tagsInfo: tagSlice,
    locationsInfo: locationsSlice
  },
  // preloadedState: reHydrateStore(),
  //middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(appMiddleware)
});
