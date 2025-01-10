import { FC, ReactElement, useEffect } from 'react';
import HomeSlider from './components/HomeSlider.tsx';
import HomeGigsView from './components/HomeGigsView.tsx';
import FeaturedExperts from './components/FeaturedExperts.tsx';
import { useGetRandomSellersQuery } from '../seller/services/seller.service.ts';
import { ISellerDocument } from '../seller/interfaces/seller.interface.ts';
import { useGetGigsByCategoryQuery, useGetTopRatedGigsByCategoryQuery } from '../gigs/services/gigs.service.ts';
import { IReduxState } from '../../store/store.interface.ts';
import { useAppSelector } from '../../store/store.ts';
import { ISellerGig } from '../gigs/interfaces/gig.interface.ts';
import TopGigsView from '../../shared/gigs/TopGigsView.tsx';
import { lowerCase } from '../../shared/utils/utils.service.ts';
import { socketService } from '../../sockets/socket.service.ts';

const Home: FC = (): ReactElement => {
  const authUser = useAppSelector((state: IReduxState) => state.authUser);
  const { data, isSuccess } = useGetRandomSellersQuery('10');
  const { data: categoryData, isSuccess: isCategorySuccess } = useGetGigsByCategoryQuery(`${authUser.username}`);
  const { data: topGigsData, isSuccess: isTopGigsSuccess } = useGetTopRatedGigsByCategoryQuery(`${authUser.username}`);
  let sellers: ISellerDocument[] = [];
  let categoryGigs: ISellerGig[] = [];
  let topGigs: ISellerGig[] = [];

  if (isSuccess) {
    sellers = data.sellers as ISellerDocument[];
  }

  if (isCategorySuccess) {
    categoryGigs = categoryData.gigs as ISellerGig[];
  }

  if (isTopGigsSuccess) {
    topGigs = topGigsData.gigs as ISellerGig[];
  }

  useEffect(() => {
    socketService.setupSocketConnection();
  }, []);

  return (
    <div className="m-auto px-6 w-screen relative min-h-screen xl:container md:px-12 lg:px-6">
      <HomeSlider />
      {topGigs.length > 0 && (
        <TopGigsView
          gigs={topGigs}
          title="Top rated services in"
          subTitle={`Highest rated talents for all your ${lowerCase(topGigs[0].categories)} needs.`}
          category={topGigs[0].categories}
          width="w-72"
          type="home"
        />
      )}
      {categoryGigs.length > 0 && (
        <HomeGigsView gigs={categoryGigs} title="Because you viewed a gig on" subTitle="" category={categoryGigs[0].categories} />
      )}
      <FeaturedExperts sellers={sellers} />
    </div>
  );
}

export default Home;
