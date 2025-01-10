import { FC, ReactElement, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

import { IMessageDocument } from '../interfaces/chat.interface';
import { useGetUserMessagesQuery } from '../services/chat.service';
// import { chatMessageReceived } from '../services/chat.utils';
import ChatList from './chatlist/ChatList.tsx';
import ChatWindow from './chatwindow/ChatWindow.tsx';
import { socket } from '../../../sockets/socket.service.ts';


const Chat: FC = (): ReactElement => {
  const { conversationId } = useParams<string>();
  const [chatMessagesData, setChatMessagesData] = useState<IMessageDocument[]>([]);
  const { data, isSuccess, isLoading, isError } = useGetUserMessagesQuery(`${conversationId}`);

  useEffect(() => {
    if (isSuccess) {
      setChatMessagesData(data?.messages as IMessageDocument[]);
    }
  }, [isSuccess, data?.messages]);

  useEffect(() => {
    const handleMessageReceived = (data: IMessageDocument) => {
      if (data.conversationId === conversationId) {
        setChatMessagesData(prevMessages => {
          const updatedMessages = [...prevMessages, data];
          return updatedMessages.filter((item, index, list) =>
            list.findIndex(listItem => listItem.id === item.id) === index
          );
        });
      }
    };

    socket.on('message received', handleMessageReceived);
    return () => {
      socket.off('message received', handleMessageReceived);
    };
  }, [conversationId]);

  return (
    <div className="border-grey mx-2 my-5 flex max-h-[90%] flex-wrap border lg:container lg:mx-auto">
      <div className="lg:border-grey relative w-full overflow-hidden lg:w-1/3 lg:border-r">
        <ChatList />
      </div>

      <div className="relative hidden w-full overflow-hidden md:w-2/3 lg:flex">
        {conversationId && chatMessagesData.length > 0 ? (
          <ChatWindow chatMessages={chatMessagesData} isLoading={isLoading} isError={isError} />
        ) : (
          <div className="flex w-full items-center justify-center">Select a user to chat with.</div>
        )}
      </div>
    </div>
  );
};

export default Chat;
