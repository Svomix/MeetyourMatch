import { getAccessToken } from '@/services/authService';
import { createSlice } from '@reduxjs/toolkit';

export const authStates = {
  unAuth: 'unAuth',
  auth: 'auth'
};

const initialState = getAccessToken() ? authStates.auth : authStates.unAuth;

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setAuth: (state, action) => (state = action.payload)
  }
});

export const { setAuth } = authSlice.actions;
export default authSlice.reducer;
