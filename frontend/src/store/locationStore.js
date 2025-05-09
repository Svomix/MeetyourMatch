import { unauthed } from '@/services/axiosInstance';
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';

export const fetchAllLocations = createAsyncThunk('tags/get', async (thunkApi) => {
	const response = await unauthed.get('/maps/locations')
	return response.data
})

const initialState = null;

const locationsSlice = createSlice({
    name: 'locationsInfo',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder.addCase(fetchAllLocations.fulfilled, (state, action) => (state = action.payload));
    }
});

export default locationsSlice.reducer;
