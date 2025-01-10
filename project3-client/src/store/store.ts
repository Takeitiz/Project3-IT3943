import storage from 'redux-persist/lib/storage';
import { combineReducers, configureStore, EnhancedStore } from '@reduxjs/toolkit';
import { Reducer } from 'redux';
import { FLUSH, PAUSE, PERSIST, persistReducer, PURGE, REGISTER, REHYDRATE } from 'redux-persist';
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import { api } from './api.ts';
import { setupListeners } from '@reduxjs/toolkit/query';
import authReducer from '../features/auth/reducers/auth.reducer.ts';
import logoutReducer from '../features/auth/reducers/logout.reducer.ts';
import buyerReducer from '../features/buyer/reducers/buyer.reducer.ts';
import sellerReducer from '../features/seller/reducers/seller.reducer.ts';
import headerReducer from '../shared/header/reducers/header.reducer.ts';
import categoryReducer from '../shared/header/reducers/category.reducer.ts';
import notificationReducer from '../shared/header/reducers/notification.reducer.ts';

const persistConfig = {
  key: 'root',
  storage: storage,
  blacklist: ['clientApi', '_persist']
};

export const combineReducer = combineReducers({
  [api.reducerPath]: api.reducer,
  authUser: authReducer,
  logout: logoutReducer,
  buyer: buyerReducer,
  seller: sellerReducer,
  header: headerReducer,
  showCategoryContainer: categoryReducer,
  notification: notificationReducer
});

export const rootReducers: Reducer<RootState> = (state, action) => {
  // this is to reset the state to default when user logs out
  if (action.type === 'logout/logout') {
    state = {} as RootState;
  }
  return combineReducer(state, action);
};

const persistedReducer = persistReducer(persistConfig, rootReducers);

export const store: EnhancedStore = configureStore({
  devTools: true,
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER]
      }
    })
  .concat(api.middleware)
});
setupListeners(store.dispatch);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export const useAppDispatch: () => AppDispatch = useDispatch;
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
