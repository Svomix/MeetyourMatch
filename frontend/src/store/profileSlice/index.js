import { authed } from '@/services/axiosInstance';
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';

export const fetchProfileInfo = createAsyncThunk('account/getInfo', async (thunkApi) => {
  const response = await authed.get('/account/getInfo');
  return response.data;
});

const initialState = null;

const profileSlice = createSlice({
  name: 'profileInfo',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder.addCase(fetchProfileInfo.fulfilled, (state, action) => (state = action.payload));
  }
});

export default profileSlice.reducer;
