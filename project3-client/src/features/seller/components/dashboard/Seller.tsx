import { FC, ReactElement } from 'react';
import DashboardHeader from '../../../../shared/header/components/DashboardHeader.tsx';
import { Outlet, useParams } from 'react-router-dom';
import { useGetSellerByIdQuery } from '../../services/seller.service.ts';
import { IOrderDocument } from '../../../order/interfaces/order.interface.ts';
import { ISellerGig } from '../../../gigs/interfaces/gig.interface.ts';
import { ISellerDocument } from '../../interfaces/seller.interface.ts';
import { useGetGigsBySellerIdQuery, useGetSellerPausedGigsQuery } from '../../../gigs/services/gigs.service.ts';
import { useGetOrdersBySellerIdQuery } from '../../../order/services/order.service.ts';

const Seller: FC = (): ReactElement => {
  const { sellerId } = useParams<string>();
  const { data, isSuccess } = useGetSellerByIdQuery(`${sellerId}`);
  const { data: sellerGigs, isSuccess: isSellerGigsSuccess } = useGetGigsBySellerIdQuery(`${sellerId}`);
  const { data: sellerPausedGigs, isSuccess: isSellerPausedGigsSuccess } = useGetSellerPausedGigsQuery(`${sellerId}`);
  const { data: sellerOrders, isSuccess: isSellerOrdersSuccess } = useGetOrdersBySellerIdQuery(`${sellerId}`);
  let gigs: ISellerGig[] = [];
  let pausedGigs: ISellerGig[] = [];
  let orders: IOrderDocument[] = [];
  let seller: ISellerDocument | undefined = undefined;

  if (isSuccess) {
    seller = data?.seller as ISellerDocument;
  }

  if (isSellerGigsSuccess) {
    gigs = sellerGigs?.gigs as ISellerGig[];
  }

  if (isSellerPausedGigsSuccess) {
    pausedGigs = sellerPausedGigs?.gigs as ISellerGig[];
  }

  if (isSellerOrdersSuccess) {
    orders = sellerOrders?.orders as IOrderDocument[];
  }

  return (
    <div className="relative w-screen">
      <DashboardHeader />
      <div className="m-auto px-6 w-screen xl:container md:px-12 lg:px-6 relative min-h-screen">
        <Outlet context={{ seller, gigs, pausedGigs, orders }} />
      </div>
    </div>
  );
};

export default Seller;
