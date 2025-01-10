import { IGigsProps, ISellerGig } from '../../interfaces/gig.interface.ts';
import { FC, useRef, useState } from 'react';
import BudgetDropdown from './components/BudgetDropdown.tsx';
import DeliveryTimeDropdown from './components/DeliveryTimeDropdown.tsx';
import { Location, useLocation, useParams, useSearchParams } from 'react-router-dom';
import {
  categories,
  getDataFromLocalStorage,
  lowerCase,
  replaceDashWithSpaces, replaceSpacesWithDash, saveToLocalStorage
} from '../../../../shared/utils/utils.service.ts';
import { useSearchGigsQuery } from '../../services/search.service.ts';
import CircularPageLoader from '../../../../shared/page-loader/CircularPageLoader.tsx';
import { v4 as uuidv4 } from 'uuid';
import GigCardDisplayItem from '../../../../shared/gigs/GigCardDisplayItem.tsx';
import PageMessage from '../../../../shared/page-message/PageMessage.tsx';
import { find } from 'lodash';
import GigPaginate from '../../../../shared/gigs/GigPaginate.tsx';

const ITEMS_PER_PAGE = 10;

const Gigs: FC<IGigsProps> = ({ type }) => {
  const [itemFrom, setItemFrom] = useState<string>('0');
  const [paginationType, setPaginationType] = useState<string>('forward');
  const [searchParams] = useSearchParams();
  const { category } = useParams<string>();
  const location: Location = useLocation();
  const updatedSearchParams: URLSearchParams = new URLSearchParams(searchParams.toString());
  const queryType: string =
    type === 'search'
      ? replaceDashWithSpaces(`${updatedSearchParams}`)
      : `query=${(`${lowerCase(`${category}`)}`)}&${updatedSearchParams.toString()}`;
  console.log(queryType);
  const { data, isSuccess, isLoading, isError } = useSearchGigsQuery({
    query: `${queryType}`,
    from: itemFrom,
    size: `${ITEMS_PER_PAGE}`,
    type: paginationType
  });
  const gigs = useRef<ISellerGig[]>([]);
  let totalGigs = 0;
  const filterApplied = getDataFromLocalStorage('filterApplied');
  const categoryName = find(categories(), (item: string) => location.pathname.includes(replaceSpacesWithDash(`${lowerCase(`${item}`)}`)));
  const gigCategories = categoryName ?? searchParams.get('query');

  if (isSuccess) {
    gigs.current = data.gigs as ISellerGig[];
    totalGigs = data.total ?? 0;
    saveToLocalStorage('filterApplied', JSON.stringify(false));
  }

  return (
    <>
      {isLoading && !isSuccess ? (
        <CircularPageLoader />
      ) : (
      <div className="container mx-auto items-center p-5">
        {!isLoading && data && data.gigs && data?.gigs.length > 0 ? (
        <>
          <h3 className="mb-5 flex gap-3 text-4xl">
            {type === 'search' && <span className="text-black">Results for</span>}
            <strong className="text-black">{gigCategories}</strong>
          </h3>
          <div className="mb-4 flex gap-4">
            <BudgetDropdown />
            <DeliveryTimeDropdown />
          </div>
          <div className="my-5">
            <div className="">
              <span className="font-medium text-[#74767e]">{data.total} services available</span>
            </div>
            {filterApplied ? (
              <CircularPageLoader />
            ) : (
              <div className="grid gap-x-6 pt-6 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                {data &&
                  data.gigs &&
                  data?.gigs.map((gig: ISellerGig) => (
                    <GigCardDisplayItem key={uuidv4()} gig={gig} linkTarget={true} showEditIcon={false} />
                  ))}
              </div>
            )}
          </div>
        </>
        ) : (
          <PageMessage
            header="No services found for your search"
            body="Try a new search or get a free quote for your project from our commnunity of freelancers."
          />
        )}
        {isError && <PageMessage header="Services issue" body="A network issue occured. Try agin later." />}
        {isSuccess && !filterApplied && data && data.gigs && data.gigs.length > 0 && (
          <GigPaginate
            gigs={gigs.current}
            totalGigs={totalGigs}
            showNumbers={true}
            itemsPerPage={ITEMS_PER_PAGE}
            setItemFrom={setItemFrom}
            setPaginationType={setPaginationType}
          />
        )}
      </div>
      )}
    </>
  )
}

export default Gigs;
