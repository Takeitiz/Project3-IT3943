import { FC, ReactElement } from 'react';
import { IFeaturedExpertProps } from '../interfaces/home.interface';
import { Link } from 'react-router-dom';
import { v4 as uuidv4 } from 'uuid';
import { lowerCase } from '../../../shared/utils/utils.service';
import StarRating from '../../../shared/rating/StarRating.tsx';

const FeaturedExperts: FC<IFeaturedExpertProps> = ({ sellers }): ReactElement => {
  return (
    <div className="mx-auto my-8 flex flex-col w-full">
      <div className="flex w-full flex-col justify-between self-center">
        <h2 className="flex self-center text-base font-bold md:text-2xl lg:text-3xl">Featured Experts</h2>
        <h4 className="pt-1 text-center text-sm md:text-base lg:text-lg">Work with talented people for the best possible
          result.</h4>
      </div>
      <div className="mt-6">
        <div className="grid gap-8 pt-3 grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {[...sellers, 1, 2, 3, 4].map(() => (
            <div key={uuidv4()} className="w-full rounded-lg border border-grey bg-white shadow">
              <div className="flex flex-col items-center pb-10 pt-5">
                <img className="mb-3 h-24 w-24 rounded-full shadow-lg"
                     src="https://placehold.co/330x220?text=Profile+Image" alt="Profile image" />
                <h5 className="mb-1 xl:text-xl font-medium text-gray-900 ">GiaVi</h5>
                <span className="text-sm w-[90%] mb-1 text-gray-500 text-center dark:text-gray-500">This is what i
                            do</span>
                <div className="flex justify-center w-full gap-x-1 self-center h-6">
                  <div className="mt-1 w-20 gap-x-2">
                    <StarRating value={5} size={14} />
                  </div>
                  <div className="ml-2 flex self-center gap-1 rounded bg-orange-400 px-1 text-xs">
                                <span className="font-bold text-white">
                                    5
                                </span>
                  </div>

                </div>
                <div className="mt-4 flex space-x-3 md:mt-6">
                  <Link
                        to={`/seller_profile/${lowerCase('giavi')}/12345/view`}
                        className="rounded bg-sky-500 px-6 py-3 text-center text-sm font-bold text-white hover:bg-sky-400 focus:outline-none md:px-4 md:py-2 md:text-base">
                    View Profile
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default FeaturedExperts;
