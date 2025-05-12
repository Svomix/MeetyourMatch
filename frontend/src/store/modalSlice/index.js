import { createSlice } from '@reduxjs/toolkit';

export const ModalPage = {
  None: 'None',
  Login: 'Login',
  Register: 'Register',
  Verify: 'Verify',
  PlaceEditor: 'PlaceEditor'
};

const initialState = { page: ModalPage.None, data: {} };

const modalSlice = createSlice({
  name: 'modal',
  initialState,
  reducers: {
    setModal: (state, action) => {
      state.page = action.payload;
    },
    setModalData: (state, action) => {
      state.data[action.payload.key] = action.payload.data
    }
  }
});

export const { setModal, setModalData } = modalSlice.actions;
export default modalSlice.reducer;
