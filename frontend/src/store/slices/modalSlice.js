import { createSlice } from '@reduxjs/toolkit';

export const ModalPage = {
  None: 'None',
  Login: 'Login',
  Register: 'Register',
  Verify: 'Verify'
};

const initialState = { page: ModalPage.None };

const modalSlice = createSlice({
  name: 'modal',
  initialState,
  reducers: {
    setModal: (state, action) => {
      state.page = action.payload;
    }
  }
});

export const { setModal } = modalSlice.actions;
export default modalSlice.reducer;
