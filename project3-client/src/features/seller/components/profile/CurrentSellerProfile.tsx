import { FC, ReactElement, useEffect, useState } from 'react';
import Breadcrumb from '../../../../shared/breadcrumb/Breadcrumb.tsx';
import { IReduxState } from '../../../../store/store.interface.ts';
import { useAppDispatch, useAppSelector } from '../../../../store/store.ts';
import ProfileHeader from './components/ProfileHeader.tsx';
import { ISellerDocument } from '../../interfaces/seller.interface.ts';
import equal from 'react-fast-compare';
import Button from '../../../../shared/button/Button.tsx';
import { addSeller } from '../../reducers/seller.reducer.ts';
import ProfileTabs from './components/ProfileTabs.tsx';
import SellerOverview from './components/SellerOverview.tsx';
import { IResponse } from '../../../../shared/shared.interface.ts';
import { showErrorToast, showSuccessToast } from '../../../../shared/utils/utils.service.ts';
import { useParams } from 'react-router-dom';
import { useUpdateSellerMutation } from '../../services/seller.service.ts';
import CircularPageLoader from '../../../../shared/page-loader/CircularPageLoader.tsx';
import GigCardDisplayItem from '../../../../shared/gigs/GigCardDisplayItem.tsx';
import { ISellerGig } from '../../../gigs/interfaces/gig.interface.ts';
import { useGetGigsBySellerIdQuery } from '../../../gigs/services/gigs.service.ts';
import { v4 as uuidv4 } from 'uuid';
import { useGetReviewsBySellerIdQuery } from '../../../order/services/review.service.ts';
import { IReviewDocument } from '../../../order/interfaces/review.interface.ts';
import GigViewReviews from '../../../gigs/components/view/components/GigViewLeft/GigViewReviews.tsx';

const CurrentSellerProfile: FC = (): ReactElement => {
  const seller = useAppSelector((state: IReduxState) => state.seller);
  const [sellerProfile, setSellerProfile] = useState<ISellerDocument>(seller);
  const [showEdit, setShowEdit] = useState<boolean>(true);
  const [type, setType] = useState<string>('Overview');
  const { sellerId } = useParams();
  const dispatch = useAppDispatch();
  const { data, isSuccess: isSellerGigSuccess, isLoading: isSellerGigLoading } = useGetGigsBySellerIdQuery(`${sellerId}`);
  const { data: sellerData, isSuccess: isGigReviewSuccess, isLoading: isGigReviewLoading } = useGetReviewsBySellerIdQuery(`${sellerId}`);
  const [updateSeller, { isLoading }] = useUpdateSellerMutation();
  let reviews: IReviewDocument[] = [];
  if (isGigReviewSuccess) {
    reviews = sellerData as IReviewDocument[];
  }

  const isDataLoading: boolean = isSellerGigLoading && isGigReviewLoading && !isSellerGigSuccess && !isGigReviewSuccess;

  const onUpdateSeller = async (): Promise<void> => {
    try {
      const response: IResponse = await updateSeller({ sellerId: `${sellerId}`, seller: sellerProfile }).unwrap();
      dispatch(addSeller(response.seller));
      setSellerProfile(response.seller as ISellerDocument);
      setShowEdit(false);
      showSuccessToast('Seller profile updated successfully.');
    } catch (error) {
      console.log(error);
      showErrorToast('Error updating profile.');
    }
  };

  useEffect(() => {
    const isEqual: boolean = equal(sellerProfile, seller);
    setShowEdit(isEqual);
  }, [seller, sellerProfile]);

  return (
    <div className="relative w-full pb-6">
      <Breadcrumb breadCrumbItems={['Seller', `${seller.username}`]} />
      {isLoading || isDataLoading ? (
        <CircularPageLoader />
      ) : (
        <div className="container mx-auto px-2 md:px-0">
          <div className="my-2 flex h-8 justify-end md:h-10">
            {!showEdit && (
              <div>
                <Button
                  className="md:text-md rounded bg-sky-500 px-6 py-1 text-center text-sm font-bold text-white hover:bg-sky-400 focus:outline-none md:py-2"
                  label="Update"
                  onClick={onUpdateSeller}
                />
                &nbsp;&nbsp;
                <Button
                  className="md:text-md rounded bg-red-500 px-6 py-1 text-center text-sm font-bold text-white hover:bg-red-500 focus:outline-none md:py-2"
                  label="Cancel"
                  onClick={() => {
                    setShowEdit(false);
                    setSellerProfile(seller);
                    dispatch(addSeller(seller));
                  }}
                />
              </div>
            )}
          </div>
          <ProfileHeader sellerProfile={sellerProfile} setSellerProfile={setSellerProfile} showHeaderInfo={true} showEditIcons={true} />
          <div className="my-4 cursor-pointer">
            <ProfileTabs type={type} setType={setType} />
          </div>

          <div className="flex flex-wrap bg-white">
            {type === 'Overview' && (
              <SellerOverview sellerProfile={sellerProfile} setSellerProfile={setSellerProfile} showEditIcons={true} />
            )}
            {type === 'Active Gigs' && (
              <div className="grid gap-x-6 pt-6 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
                {data?.gigs &&
                  data?.gigs.map((gig: ISellerGig) => (
                    <GigCardDisplayItem key={uuidv4()} gig={gig} linkTarget={false} showEditIcon={true} />
                  ))}
              </div>
            )}
            {type === 'Ratings & Reviews' && <GigViewReviews showRatings={false} reviews={reviews} hasFetchedReviews={true} />}
          </div>
        </div>
      )}
    </div>
  );
};

export default CurrentSellerProfile;
