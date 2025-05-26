import React, { useState } from 'react';
import { Card } from '@/components/ui/card';
import { Send } from 'lucide-react';
import { Message } from '../Mailbox';
import { MessageDetail } from './MessageDetail';

interface SentProps {
  messages: Message[];
  isLoading: boolean;
}

export const Sent = ({ messages, isLoading }: SentProps) => {
  const [selectedMessage, setSelectedMessage] = useState<Message | null>(null);

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
        type="sent"
      />
    );
  }

  if (messages.length === 0) {
    return (
      <div className="text-center py-12">
        <Send className="w-12 h-12 text-gray-400 mx-auto mb-4" />
        <h3 className="text-lg font-medium text-gray-900 mb-2">No sent messages</h3>
        <p className="text-gray-600">You haven't sent any messages yet</p>
      </div>
    );
  }

  return (
    <div className="space-y-2">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900">
          Sent ({messages.length})
        </h2>
      </div>
      
      {messages.map((message) => (
        <Card 
          key={message.id}
          className="p-4 cursor-pointer transition-all hover:shadow-md border-l-4 border-l-green-200 bg-white"
          onClick={() => setSelectedMessage(message)}
        >
          <div className="flex items-center space-x-3">
            <Send className="w-5 h-5 text-green-600" />
            <div className="flex-1 min-w-0">
              <div className="flex items-center justify-between">
                <p className="text-sm font-medium text-gray-900 truncate">
                  To: {message.recipientName }
                </p>
                <span className="text-xs text-gray-500 ml-2">
                  {formatDate(message.timestamp)}
                </span>
              </div>
              <p className="text-sm text-gray-700 truncate mt-1">
                {message.subject}
              </p>
              <p className="text-xs text-gray-500 truncate mt-1">
                {message.content.substring(0, 100)}...
              </p>
            </div>
          </div>
        </Card>
      ))}
    </div>
  );
};
