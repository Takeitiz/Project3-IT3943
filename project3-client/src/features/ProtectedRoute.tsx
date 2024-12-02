import { FC, ReactElement, ReactNode, useCallback, useEffect, useState } from 'react';
import { IReduxState } from '../store/store.interface.ts';
import { useAppDispatch, useAppSelector } from '../store/store.ts';
import { Navigate, NavigateFunction, useNavigate } from 'react-router-dom';
import { useCheckCurrentUserQuery } from './auth/services/auth.service.ts';
import { addAuthUser } from './auth/reducers/auth.reducer.ts';
import { applicationLogout, saveToSessionStorage } from '../shared/utils/utils.service.ts';
import HomeHeader from '../shared/header/components/HomeHeader.tsx';

export interface IProtectedRouteProps {
  children: ReactNode;
}


const ProtectedRoute: FC<IProtectedRouteProps> = ({ children }): ReactElement => {
  const authUser = useAppSelector((state: IReduxState) => state.authUser);
  const showCategoryContainer = useAppSelector((state: IReduxState) => state.showCategoryContainer);
  const header = useAppSelector((state: IReduxState) => state.header);
  const [tokenIsValid, setTokenIsValid] = useState<boolean>(false);
  const dispatch = useAppDispatch();
  const navigate: NavigateFunction = useNavigate();
  // @ts-ignore
  const { data, isError } = useCheckCurrentUserQuery();

  const checkUser = useCallback(async () => {
    if (data && data.user) {
      setTokenIsValid(true);
      dispatch(addAuthUser({ authInfo: data.user }));
      saveToSessionStorage(JSON.stringify(true), JSON.stringify(authUser.username));
    }

    if (isError) {
      setTokenIsValid(false);
      applicationLogout(dispatch, navigate);
    }
  }, [data, dispatch, navigate, isError, authUser.username]);

  useEffect(() => {
    checkUser();
  }, [checkUser]);

  if ((data && data.user) || authUser) {
    if (tokenIsValid) {
      return (
        <>
          {header && header === 'home' && <HomeHeader showCategoryContainer={showCategoryContainer} />}
          {children}
        </>
      );
    } else {
      return <></>;
    }
  } else {
    return <>{<Navigate to="/" />}</>;
  }
}

export default ProtectedRoute;
