import { Dispatch, MouseEventHandler, ReactNode, SetStateAction } from 'react';
import { IMessageDocument } from '../../../features/chat/interfaces/chat.interface.ts';
import { IOrderDocument } from '../../../features/order/interfaces/order.interface.ts';
import { IBuyerDocument } from '../../../features/buyer/interfaces/buyer.interface.ts';
import { IAuthUser } from '../../../features/auth/interfaces/auth.interface.ts';

export interface IModalBgProps {
  children?: ReactNode;
  onClose?: MouseEventHandler<HTMLButtonElement>;
  onToggle?: Dispatch<SetStateAction<boolean>>;
  onTogglePassword?: Dispatch<SetStateAction<boolean>>;
}

export interface IModalProps {
  header?: string;
  gigTitle?: string;
  singleMessage?: IMessageDocument;
  order?: IOrderDocument;
  receiver?: IBuyerDocument;
  authUser?: IAuthUser;
  type?: string;
  approvalModalContent?: IApprovalModalContent;
  hideCancel?: boolean;
  cancelBtnHandler?: () => void;
  onClick?: () => void;
  onClose?: () => void;
  onBodyChange?: (text: string) => void;
}

export interface IApprovalModalContent {
  header: string;
  body: string;
  btnText: string;
  btnColor: string;
}
