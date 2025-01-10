import { FC, ReactElement, useState } from 'react';
import Breadcrumb from '../../../../shared/breadcrumb/Breadcrumb.tsx';
import CircularPageLoader from '../../../../shared/page-loader/CircularPageLoader.tsx';
import ProfileHeader from './components/ProfileHeader.tsx';
import ProfileTabs from './components/ProfileTabs.tsx';
import SellerOverview from './components/SellerOverview.tsx';
import { useParams } from 'react-router-dom';
import { useGetSellerByIdQuery } from '../../services/seller.service.ts';
import { useGetGigsBySellerIdQuery } from '../../../gigs/services/gigs.service.ts';
import GigCardDisplayItem from '../../../../shared/gigs/GigCardDisplayItem.tsx';
import { ISellerGig } from '../../../gigs/interfaces/gig.interface.ts';
import { v4 as uuidv4 } from 'uuid';
import GigViewReviews from '../../../gigs/components/view/components/GigViewLeft/GigViewReviews.tsx';
import { useGetReviewsBySellerIdQuery } from '../../../order/services/review.service.ts';
import { IReviewDocument } from '../../../order/interfaces/review.interface.ts';

const SellerProfile: FC = (): ReactElement => {
  const [type, setType] = useState<string>('Overview');
  const { sellerId } = useParams();
  const { data: sellerData, isLoading: isSellerLoading, isSuccess: isSellerSuccess } = useGetSellerByIdQuery(`${sellerId}`);
  const { data: gigData, isSuccess: isSellerGigSuccess, isLoading: isSellerGigLoading } = useGetGigsBySellerIdQuery(`${sellerId}`);
  const {
    data: sellerReviewsData,
    isSuccess: isGigReviewSuccess,
    isLoading: isGigReviewLoading
  } = useGetReviewsBySellerIdQuery(`${sellerId}`);
  let reviews: IReviewDocument[] = [];
  if (isGigReviewSuccess) {
    reviews = sellerReviewsData as IReviewDocument[];
  }

  const isLoading: boolean =
    isSellerGigLoading && isSellerLoading && isGigReviewLoading && !isSellerSuccess && !isSellerGigSuccess && !isGigReviewSuccess;

  return (
    <div className="relative w-full pb-6">
      <Breadcrumb breadCrumbItems={['Seller', `${sellerData && sellerData.seller ? sellerData.seller.username : ''}`]} />
      {isLoading ? (
        <CircularPageLoader />
      ) : (
        <div className="container mx-auto px-2 md:px-0">
          <ProfileHeader sellerProfile={sellerData?.seller} showHeaderInfo={true} showEditIcons={false} />
          <div className="my-4 cursor-pointer">
            <ProfileTabs type={type} setType={setType} />
          </div>

          <div className="flex flex-wrap bg-white">
            {type === 'Overview' && <SellerOverview sellerProfile={sellerData?.seller} showEditIcons={false} />}
            {type === 'Active Gigs' && (
              <div className="grid gap-x-6 pt-6 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
                {gigData?.gigs &&
                  gigData?.gigs.map((gig: ISellerGig) => (
                    <GigCardDisplayItem key={uuidv4()} gig={gig} linkTarget={false} showEditIcon={false} />
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

export default SellerProfile;
