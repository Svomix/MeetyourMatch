import { unauthed } from '@/services/axiosInstance';
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';

export const fetchAllTags = createAsyncThunk('tags/get', async (thunkApi) => {
	const response = await unauthed.get('v1/tags')
	return response.data
})

const initialState = null;

const tagSlice = createSlice({
    name: 'tagsInfo',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder.addCase(fetchAllTags.fulfilled, (state, action) => (state = action.payload));
    }
});

export default tagSlice.reducer;
