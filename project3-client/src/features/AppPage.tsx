import { FC, ReactElement, useCallback, useEffect, useState } from 'react';
import Index from './index/Index'
import { IReduxState } from '../store/store.interface.ts';
import { useAppDispatch, useAppSelector } from '../store/store.ts';
import { useCheckCurrentUserQuery } from './auth/services/auth.service.ts';
import { NavigateFunction, useNavigate } from 'react-router-dom';
import { addAuthUser } from './auth/reducers/auth.reducer.ts';
import { applicationLogout, saveToSessionStorage } from '../shared/utils/utils.service.ts';
import HomeHeader from '../shared/header/components/HomeHeader.tsx';
import Home from './home/Home.tsx';

const AppPage: FC = (): ReactElement => {
  const authUser = useAppSelector((state: IReduxState) => state.authUser);
  const appLogout = useAppSelector((state: IReduxState) => state.logout);
  const showCategoryContainer = true;
  const [tokenIsValid, setTokenIsValid] = useState<boolean>(false);
  const dispatch = useAppDispatch();
  const navigate: NavigateFunction = useNavigate();
  const { data: currentUserData, isError } = useCheckCurrentUserQuery(authUser.username!, { skip: authUser.id === null });

  const checkUser = useCallback(async () => {
    try {
      if (currentUserData && currentUserData.user && !appLogout) {
        setTokenIsValid(true);
        dispatch(addAuthUser({ authInfo: currentUserData.user }));
        saveToSessionStorage(JSON.stringify(true), JSON.stringify(authUser.username));
      }
    } catch (error) {
      console.log(error);
    }
  }, [currentUserData, dispatch, appLogout, authUser.username]);

  const logoutUser = useCallback(async () => {
    if ((!currentUserData && appLogout) || isError) {
      setTokenIsValid(false);
      applicationLogout(dispatch, navigate);
    }
  }, [currentUserData, dispatch, navigate, appLogout, isError]);

  useEffect(() => {
    checkUser();
    logoutUser();
  }, [checkUser, logoutUser]);

  if (authUser) {
    return !tokenIsValid && !authUser.id ? (
      <Index />
    ) : (
      <>
        <HomeHeader showCategoryContainer={showCategoryContainer} />
        <Home />
      </>
    );
  } else {
    return <Index />;
  }
}

export default AppPage;
