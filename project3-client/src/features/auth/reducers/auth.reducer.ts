import { createSlice, Slice } from '@reduxjs/toolkit';
import { initialAuthUserValues } from '../../../shared/utils/static-data.ts';
import { IAuthUser, IReduxAddAuthUser } from '../interfaces/auth.interface.ts';

const initialValue: IAuthUser = initialAuthUserValues as IAuthUser;

const authSlice: Slice = createSlice({
  name: 'auth',
  initialState: initialValue,
  reducers: {
    addAuthUser: (state: IAuthUser, action: IReduxAddAuthUser): IAuthUser => {
      const { authInfo } = action.payload;
      state = { ...authInfo } as unknown as IAuthUser;
      return state;
    },
    clearAuthUser: (): IAuthUser => {
      return initialAuthUserValues;
    }
  }
});

export const { addAuthUser, clearAuthUser } = authSlice.actions;
export default authSlice.reducer;
