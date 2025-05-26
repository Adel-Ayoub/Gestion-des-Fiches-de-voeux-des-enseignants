import React, { useState } from 'react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Mail, MailOpen } from 'lucide-react';
import { Message } from '../Mailbox';
import { MessageDetail } from './MessageDetail';
import {useEffect} from 'react';
import {jwtDecode} from 'jwt-decode';
interface InboxProps {
  messages: Message[];
  onMarkAsRead: (messageId: number) => void;
  isLoading: boolean;
}

export const Inbox = ({ messages, onMarkAsRead, isLoading }: InboxProps) => {
  const [selectedMessage, setSelectedMessage] = useState<Message | null>(null);
  const [isTeacher, setIsTeacher] = useState(false);
  useEffect(() => {
    const token = localStorage.getItem('jwt');
    const decodedToken = jwtDecode(token);
    setIsTeacher(decodedToken.roles === 'ROLE_TEACHER');
}, []);
  const handleMessageClick = (message: Message) => {
    setSelectedMessage(message);
    if (!message.isRead) {
      onMarkAsRead(message.id);
    }
  };

  const formatDate = (timestamp: string) => {
    const date = new Date(timestamp);
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const messageDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    
    if (messageDate.getTime() === today.getTime()) {
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } else {
      return date.toLocaleDateString();
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (selectedMessage) {
    return (
      <MessageDetail 
        message={selectedMessage} 
        onBack={() => setSelectedMessage(null)}
        type="inbox"
      />
    );
  }

  if (messages.length === 0) {
    return (
      <div className="text-center py-12">
        <Mail className="w-12 h-12 text-gray-400 mx-auto mb-4" />
        <h3 className="text-lg font-medium text-gray-900 mb-2">No messages</h3>
        <p className="text-gray-600">Your inbox is empty</p>
      </div>
    );
  }

  return (
    <div className="space-y-2">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900">
          Inbox ({messages.length})
        </h2>
        <Badge variant="secondary">
          {messages.filter(m => !m.isRead).length} unread
        </Badge>
      </div>
      
      {messages.map((message) => (
        <Card 
          key={message.id}
          className={`p-4 cursor-pointer transition-all hover:shadow-md border-l-4 ${
            message.isRead 
              ? 'border-l-gray-200 bg-white' 
              : 'border-l-blue-500 bg-blue-50'
          }`}
          onClick={() => handleMessageClick(message)}
        >
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3 flex-1">
              {message.isRead ? (
                <MailOpen className="w-5 h-5 text-gray-400" />
              ) : (
                <Mail className="w-5 h-5 text-blue-600" />
              )}
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between">
                  <p className={`text-sm font-medium truncate ${
                    message.isRead ? 'text-gray-900' : 'text-blue-900'
                  }`}>
                    From: {message.senderName || 'Admin'}
                  </p>
                  <span className="text-xs text-gray-500 ml-2">
                    {formatDate(message.timestamp)}
                  </span>
                </div>
                <p className={`text-sm truncate mt-1 ${
                  message.isRead ? 'text-gray-700' : 'text-blue-800 font-medium'
                }`}>
                  {message.subject}
                </p>
                <p className="text-xs text-gray-500 truncate mt-1">
                  {message.content.substring(0, 100)}...
                </p>
              </div>
            </div>
            {!message.isRead && (
              <div className="w-2 h-2 bg-blue-600 rounded-full ml-2"></div>
            )}
          </div>
        </Card>
      ))}
    </div>
  );
};
