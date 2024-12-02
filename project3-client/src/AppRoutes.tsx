import { useRoutes, RouteObject } from 'react-router-dom';
import { FC, Suspense } from 'react';
import AppPage from './features/AppPage.tsx';
import Home from './features/home/Home.tsx';
import ResetPassword from './features/auth/components/ResetPassword.tsx';
import ConfirmEmail from './features/auth/components/ConfirmEmail.tsx';
import ProtectedRoute from './features/ProtectedRoute.tsx';
import Error from './features/error/Error.tsx';

const AppRouter: FC = () => {
  const routes: RouteObject[] = [
    {
      path: '/',
      element: <AppPage/>
    },
    {
      path: 'reset_password',
      element: (
        <Suspense>
          <ResetPassword />
        </Suspense>
      )
    },
    {
      path: 'confirm_email',
      element: (
        <Suspense>
          <ConfirmEmail />
        </Suspense>
      )
    },
    {
      path: '/',
      element: (
        <Suspense>
          <ProtectedRoute>
              <Home />
          </ProtectedRoute>
        </Suspense>
      )
    },
    {
      path: '*',
      element: (
        <Suspense>
          <Error />
        </Suspense>
      )
    }
  ];

  return useRoutes(routes);
}

export default AppRouter;
