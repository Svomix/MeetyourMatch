import { getAccessToken } from '@/services/authService';
import { createSlice } from '@reduxjs/toolkit';

const initialState = !!getAccessToken();

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setAuth: (state, action) => (state = action.payload)
  }
});

export const { setAuth } = authSlice.actions;
export default authSlice.reducer;
