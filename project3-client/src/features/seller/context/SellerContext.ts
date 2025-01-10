
import { ISellerContext } from '../interfaces/seller.interface.ts';
import { Context, createContext } from 'react';
import { emptySellerData } from '../../../shared/utils/static-data.ts';

export const SellerContext: Context<ISellerContext> = createContext({
  showEditIcons: false,
  sellerProfile: emptySellerData
}) as Context<ISellerContext>;
