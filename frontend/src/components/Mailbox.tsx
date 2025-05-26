import React, { useState, useEffect } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { RefreshCw, Mail } from 'lucide-react';
import { Inbox } from './mailbox/Inbox';
import { Sent } from './mailbox/Sent';
import { Compose } from './mailbox/Compose';
import { mailService } from '../services/mailService';
import { useToast,Toast } from '@/hooks/use-toast';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
export interface Message {
  id: number;
  subject: string;
  content: string;
  sender: string;
  recipient: string;
  timestamp: string;
  isRead: boolean;
  senderName?: string; // Optional, for display purposes
  recipientName?: string; // Optional, for display purposes
}
interface MailboxProps {
  userId: number;
  compose?: boolean; // Optional prop to control the initial tab
}
export async function handleSendMessage(recipient:number,subject: string, content: string)  {
   
try {
      const token = localStorage.getItem('jwt');
      const decodedToken = jwtDecode(token);
            const isAdmin = decodedToken.roles === 'ROLE_ADMIN';
      const userId = await axios.get("http://localhost:8080/api/users/by-email", {
        headers: { Authorization: token },
        params: { email: decodedToken.sub }
      });
      await mailService.sendMessage(recipient,userId.data.id,subject, content, isAdmin);
           // Refresh sent messages
      
      return true;
    } catch (error) {
      console.error('Error sending message:', error);
            return false;
    }
  };

const Mailbox = ({ userId,compose=true }: MailboxProps) => {
  const [inboxMessages, setInboxMessages] = useState<Message[]>([]);
  const [sentMessages, setSentMessages] = useState<Message[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('inbox');
  const { toast } = useToast();

  const fetchMessages = async () => {
    setIsLoading(true);
    try {
      const [inbox, sent] = await Promise.all([
        mailService.getInboxMessages(userId),
        mailService.getSentMessages(userId)
      ]);
      setInboxMessages(inbox);
      setSentMessages(sent);
      toast({
        title: "Messages refreshed",
        description: "Your mailbox has been updated",
      });
    } catch (error) {
      console.error('Error fetching messages:', error);
      toast({
        title: "Error",
        description: "Failed to fetch messages. Please try again.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  
  const markAsRead = async (messageId: number) => {
    try {
      await mailService.markAsRead(messageId);
      setInboxMessages(prev => 
        prev.map(msg => 
          msg.id === messageId ? { ...msg, isRead: true } : msg
        )
      );
    } catch (error) {
      console.error('Error marking message as read:', error);
    }
  };

  useEffect(() => {
    if (!userId){ 
      console.error('User ID is required to fetch messages');
      console.log(userId);
      return;}
    fetchMessages();
  }, [userId]);

  const unreadCount = inboxMessages.filter(msg => !msg.isRead).length;

  return (
    <div className="w-full max-w-4xl mx-auto bg-white rounded-lg shadow-lg border">
      <div className="p-6 border-b bg-gradient-to-r from-blue-50 to-indigo-50">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
              <Mail className="w-5 h-5 text-white" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Mailbox</h1>
              <p className="text-sm text-gray-600">Communicate with admin</p>
            </div>
          </div>
          <Button
            onClick={fetchMessages}
            disabled={isLoading}
            variant="outline"
            size="sm"
            className="flex items-center space-x-2"
          >
            <RefreshCw className={`w-4 h-4 ${isLoading ? 'animate-spin' : ''}`} />
            <span>Refresh</span>
          </Button>
        </div>
      </div>

      <div className="p-6">
        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="flex  w-full  bg-gray-100">
            <TabsTrigger value="inbox" className={` ${compose ? 'w-1/3' : 'w-1/2'}`}>
              Inbox
              {unreadCount > 0 && (
                <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                  {unreadCount}
                </span>
              )}
            </TabsTrigger>
            <TabsTrigger value="sent" className={` ${compose ? 'w-1/3' : 'w-1/2'}`}>Sent</TabsTrigger>

            {compose &&<TabsTrigger value="compose" className={` ${compose ? 'w-1/3' : 'w-1/2'}`}>Compose</TabsTrigger>}
          </TabsList>

          <TabsContent value="inbox" className="mt-6">
            <Inbox 
              messages={inboxMessages} 
              onMarkAsRead={markAsRead}
              isLoading={isLoading}
            />
          </TabsContent>

          <TabsContent value="sent" className="mt-6">
            <Sent 
              messages={sentMessages}
              isLoading={isLoading}
            />
          </TabsContent>
          {compose &&(
          <TabsContent value="compose" className="mt-6">
            <Compose 
              onSendMessage={handleSendMessage} 
            />
          </TabsContent>)}
        </Tabs>
      </div>
    </div>
  );
};
export default Mailbox;
