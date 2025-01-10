import { api } from '../../../store/api.ts';
import { IResponse } from '../../../shared/shared.interface.ts';


export const settingsApi = api.injectEndpoints({
  endpoints: (build) => ({
    changePassword: build.mutation<IResponse, { currentPassword: string; newPassword: string; username: string }>({
      query({ currentPassword, newPassword, username }) {
        return {
          url: '/auth/change-password',
          method: 'PUT',
          body: { currentPassword, newPassword, username }
        };
      },
      invalidatesTags: ['Auth']
    })
  })
});

export const { useChangePasswordMutation } = settingsApi;
