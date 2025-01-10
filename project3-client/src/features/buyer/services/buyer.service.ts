import { api } from '../../../store/api.ts';
import { IResponse } from '../../../shared/shared.interface.ts';

export const buyerApi = api.injectEndpoints({
  endpoints: (build) => ({
    getCurrentBuyerByUsername: build.query<IResponse, string>({
      query: (username) => ({
        url: 'buyer/username',
        params: { username }
      }),
      providesTags: ['Buyer']
    }),

    getBuyerByUsername: build.query<IResponse, string>({
      query: (username) => ({
        url: `buyer/${username}`
      }),
      providesTags: ['Buyer']
    }),

    getBuyerByEmail: build.query<IResponse, string>({
      query: (email) => ({
        url: 'buyer/email',
        params: { email }
      }),
      providesTags: ['Buyer']
    })
  })
});

export const { useGetCurrentBuyerByUsernameQuery, useGetBuyerByUsernameQuery, useGetBuyerByEmailQuery } = buyerApi;
