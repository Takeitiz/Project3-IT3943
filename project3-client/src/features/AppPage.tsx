import { FC, ReactElement, useCallback, useEffect, useState } from 'react';
import Index from './index/Index'
import { IReduxState } from '../store/store.interface.ts';
import { useAppDispatch, useAppSelector } from '../store/store.ts';
import { useCheckCurrentUserQuery } from './auth/services/auth.service.ts';
import { NavigateFunction, useNavigate } from 'react-router-dom';
import { addAuthUser } from './auth/reducers/auth.reducer.ts';
import { applicationLogout, getDataFromLocalStorage, saveToSessionStorage } from '../shared/utils/utils.service.ts';
import HomeHeader from '../shared/header/components/HomeHeader.tsx';
import Home from './home/Home.tsx';
import { useGetCurrentBuyerByUsernameQuery } from './buyer/services/buyer.service.ts';
import { addBuyer } from './buyer/reducers/buyer.reducer.ts';
import { useGetSellerByUsernameQuery } from './seller/services/seller.service.ts';
import { addSeller } from './seller/reducers/seller.reducer.ts';
import CircularPageLoader from '../shared/page-loader/CircularPageLoader.tsx';
import { socket } from '../sockets/socket.service.ts';

const AppPage: FC = (): ReactElement => {
  const authUser = useAppSelector((state: IReduxState) => state.authUser);
  const appLogout = useAppSelector((state: IReduxState) => state.logout);
  const showCategoryContainer = useAppSelector((state: IReduxState) => state.showCategoryContainer);
  const [tokenIsValid, setTokenIsValid] = useState<boolean>(false);
  const dispatch = useAppDispatch();
  const navigate: NavigateFunction = useNavigate();
  const { data: currentUserData, isError } = useCheckCurrentUserQuery(authUser.username!, { skip: authUser.id === null });
  const { data: buyerData, isLoading: isBuyerLoading } = useGetCurrentBuyerByUsernameQuery(authUser.username!, { skip: authUser.id === null });
  const { data: sellerData, isLoading: isSellerLoading } = useGetSellerByUsernameQuery(`${authUser.username}`, {
    skip: authUser.id === null
  });

  const checkUser = useCallback(async () => {
    try {
      if (currentUserData && currentUserData.user && !appLogout) {
        setTokenIsValid(true);
        dispatch(addAuthUser({ authInfo: currentUserData.user }));
        dispatch(addBuyer(buyerData?.buyer));
        dispatch(addSeller(sellerData?.seller));
        saveToSessionStorage(JSON.stringify(true), JSON.stringify(authUser.username));
        const becomeASeller = getDataFromLocalStorage('becomeASeller');
        if (becomeASeller) {
          navigate('/seller_onboarding');
        }
        if (authUser.username !== null) {
          socket.emit('loggedInUsers', authUser.username);
        }
      }
    } catch (error) {
      console.log(error);
    }
  }, [currentUserData, navigate, dispatch, appLogout, authUser.username, buyerData, sellerData]);

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
        {isBuyerLoading && isSellerLoading ? (
          <CircularPageLoader />
        ) : (
          <>
            <HomeHeader showCategoryContainer={showCategoryContainer} />
            <Home />
          </>
        )}
      </>
    );
  } else {
    return <Index />;
  }
}

export default AppPage;
