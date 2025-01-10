import { IAuthUser } from '../features/auth/interfaces/auth.interface.ts';
import { IBuyerDocument } from '../features/buyer/interfaces/buyer.interface.ts';
import { ISellerDocument } from '../features/seller/interfaces/seller.interface.ts';
import { INotification } from '../shared/header/interfaces/header.interface.ts';

export interface IReduxState {
  authUser: IAuthUser;
  header: string;
  logout: boolean;
  buyer: IBuyerDocument;
  seller: ISellerDocument;
  showCategoryContainer: boolean;
  notification: INotification;
}
