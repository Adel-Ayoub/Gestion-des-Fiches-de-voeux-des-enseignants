
import React from 'react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { ArrowLeft, Mail, Send, User, Clock } from 'lucide-react';
import { Message } from '../Mailbox';

interface MessageDetailProps {
  message: Message;
  onBack: () => void;
  type: 'inbox' | 'sent';
}

export const MessageDetail = ({ message, onBack, type }: MessageDetailProps) => {
  const formatFullDate = (timestamp: string) => {
    const date = new Date(timestamp);
    return date.toLocaleString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="max-w-3xl">
      <div className="flex items-center space-x-3 mb-6">
        <Button
          variant="ghost"
          size="sm"
          onClick={onBack}
          className="flex items-center space-x-2"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back</span>
        </Button>
        <div className="h-4 w-px bg-gray-300"></div>
        <Badge variant={type === 'inbox' ? 'default' : 'secondary'}>
          {type === 'inbox' ? 'Received' : 'Sent'}
        </Badge>
      </div>

      <Card className="p-6">
        <div className="border-b pb-4 mb-6">
          <h1 className="text-xl font-bold text-gray-900 mb-4">
            {message.subject}
          </h1>
          
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                  <User className="w-4 h-4 text-blue-600" />
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-900">
                    {type === 'inbox' ? 'From: Admin' : 'To: Admin'}
                  </p>
                  <p className="text-xs text-gray-600">
                    {type === 'inbox' ? 'System Administrator' : 'System Administrator'}
                  </p>
                </div>
              </div>
              
              <div className="flex items-center space-x-2 text-gray-500">
                <Clock className="w-4 h-4" />
                <span className="text-sm">{formatFullDate(message.timestamp)}</span>
              </div>
            </div>

            <div className="flex items-center space-x-2">
              {type === 'inbox' ? (
                <Mail className="w-5 h-5 text-blue-600" />
              ) : (
                <Send className="w-5 h-5 text-green-600" />
              )}
              {type === 'inbox' && !message.isRead && (
                <Badge variant="destructive" className="text-xs">
                  New
                </Badge>
              )}
            </div>
          </div>
        </div>

        <div className="prose max-w-none">
          <div className="whitespace-pre-wrap text-gray-800 leading-relaxed">
            {message.content}
          </div>
        </div>

        {type === 'inbox' && (
          <div className="mt-8 pt-6 border-t">
            <Button 
              variant="outline" 
              className="flex items-center space-x-2"
              onClick={onBack}
            >
              <Send className="w-4 h-4" />
              <span>Reply to Admin</span>
            </Button>
          </div>
        )}
      </Card>
    </div>
  );
};
