import { useEffect } from 'react';
import { getDataFromSessionStorage } from '../utils/utils.service';
import { socket } from '../../sockets/socket.service.ts';

const useBeforeWindowUnload = (): void => {
  useEffect(() => {
    // If the user closes the browser or tab, we emit the socketio event
    window.addEventListener('beforeunload', () => {
      const loggedInUsername: string = getDataFromSessionStorage('loggedInuser');
      socket.emit('removeLoggedInUser', loggedInUsername);
    });
  }, []);
};

export default useBeforeWindowUnload;
