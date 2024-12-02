import { Dispatch, SetStateAction } from 'react';
import { IBuyerDocument } from '../../../features/buyer/interfaces/buyer.interface.ts';
import { ISellerDocument } from '../../../features/seller/interfaces/seller.interface.ts';
import { IAuthUser } from '../../../features/auth/interfaces/auth.interface.ts';

export interface IReduxHeader {
  type: string;
  payload: string;
}

export interface IReduxShowCategory {
  type: string;
  payload: boolean;
}

export interface IReduxNotification {
  type?: string;
  payload: INotification;
}

export interface INotification {
  hasUnreadMessage?: boolean;
  hasUnreadNotification?: boolean;
}

export interface IHomeHeaderProps {
  buyer?: IBuyerDocument;
  seller?: ISellerDocument;
  authUser?: IAuthUser;
  type?: string;
  showCategoryContainer?: boolean;
  setIsDropdownOpen?: Dispatch<SetStateAction<boolean>>;
  setIsOrderDropdownOpen?: Dispatch<SetStateAction<boolean>>;
  setIsMessageDropdownOpen?: Dispatch<SetStateAction<boolean>>;
  setIsNotificationDropdownOpen?: Dispatch<SetStateAction<boolean>>;
}

export interface IHeaderSideBarProps {
  setShowRegisterModal?: Dispatch<SetStateAction<IHeaderModalProps>>;
  setShowLoginModal?: Dispatch<SetStateAction<IHeaderModalProps>>;
  setOpenSidebar?: Dispatch<SetStateAction<boolean>>;
}

export interface IHeader {
  navClass: string;
}

export interface ISettings {
  id: number;
  name: string;
  url: string;
  show: boolean;
}

export interface IHeaderModalProps {
  login: boolean;
  register: boolean;
  forgotPassword: boolean;
}
