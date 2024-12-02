import { BaseQueryFn, createApi, FetchArgs, fetchBaseQuery, FetchBaseQueryError } from '@reduxjs/toolkit/query/react';
import { getDataFromSessionStorage } from '../shared/utils/utils.service.ts';

const BASE_ENDPOINT = 'http://localhost:4000';

const baseQuery = fetchBaseQuery({
  baseUrl: `${BASE_ENDPOINT}/api/v1`,
  prepareHeaders: (headers) => {
    headers.set('Content-Type', 'application/json');
    headers.set('Accept', 'application/json');
    return headers;
  },
  // credentials: 'include'
  // enable to send request with jwt token
});

const baseQueryWithReAuth: BaseQueryFn<string | FetchArgs, unknown, FetchBaseQueryError> = async (args, api, extraOptions) => {
  const result = await baseQuery(args, api, extraOptions);
  if (result.error && result.error.status === 401) {
    const loggedInUsername: string = getDataFromSessionStorage('loggedInuser');
    await baseQuery(`/auth/refresh-token/${loggedInUsername}`, api, extraOptions);
  }
  return result;
};

export const api = createApi({
  reducerPath: 'clientApi',
  baseQuery: baseQueryWithReAuth,
  tagTypes: ['Auth', 'Currentuser', 'Buyer', 'Seller', 'Chat', 'Checkout', 'Gigs', 'Search', 'Review', 'Order', 'Notification'],
  endpoints: () => ({})
});
