import { IAuthUser } from '../features/auth/interfaces/auth.interface.ts';

export interface IReduxState {
  authUser: IAuthUser;
  header: string;
  logout: boolean;
  buyer: object;
  seller: object;
  showCategoryContainer: boolean;
  notification: object;
}
