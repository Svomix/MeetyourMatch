import { getIsLoggedIn, tokenType } from '@/services/authService';
import { authed, unauthed } from '@/services/axiosInstance';
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';

export const fetchEventInfo = createAsyncThunk('events/get', async (action, thunkApi) => {
    const instance = getIsLoggedIn() ? authed : unauthed;
	const response = await instance.get(`v1/events/${action}`)
	return { data: response.data, id: action }
})

const initialState = null;

const eventSlice = createSlice({
    name: 'eventInfo',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder.addCase(fetchEventInfo.fulfilled, (state, action) => (state = action.payload));
    }
});

export default eventSlice.reducer;
