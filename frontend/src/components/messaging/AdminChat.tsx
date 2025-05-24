import React, { useState, useEffect, useRef } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Send, Users, Shield } from 'lucide-react';
import { useWebSocket } from '@/hooks/useWebSocket';

interface Message {
  id: string;
  content: string;
  sender: string;
  userId: string;
  timestamp: Date;
  isAdmin: boolean;
}

interface User {
  userId: string;
  username: string;
  isOnline: boolean;
}

export const AdminChat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUser, setSelectedUser] = useState<string | null>(null);
  const [newMessage, setNewMessage] = useState('');
  const [isConnected, setIsConnected] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const { sendMessage, isConnected: wsConnected } = useWebSocket({
    url: 'ws://localhost:8080/admin-chat',
    onMessage: (data: any) => {
      if (data.type === 'MESSAGE') {
        const chatMessage: Message = {
          id: data.id || Date.now().toString(),
          content: data.content,
          sender: data.sender,
          userId: data.userId,
          timestamp: new Date(data.timestamp),
          isAdmin: data.isAdmin || false
        };
        setMessages(prev => [...prev, chatMessage]);
      } else if (data.type === 'USER_LIST') {
        setUsers(data.users);
      }
    },
    onConnect: () => setIsConnected(true),
    onDisconnect: () => setIsConnected(false)
  });

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSendMessage = () => {
    if (newMessage.trim() && wsConnected && selectedUser) {
      const messagePayload = {
        type: 'ADMIN_MESSAGE',
        content: newMessage,
        targetUserId: selectedUser,
        timestamp: new Date().toISOString()
      };
      
      sendMessage(messagePayload);
      setNewMessage('');
    }
  };

  const filteredMessages = selectedUser 
    ? messages.filter(msg => msg.userId === selectedUser || msg.isAdmin)
    : [];

  return (
    <div className="flex h-[600px] w-full max-w-4xl gap-4">
      {/* Users List */}
      <Card className="w-64">
        <CardHeader className="pb-3">
          <CardTitle className="flex items-center gap-2">
            <Users className="w-5 h-5" />
            Online Users
          </CardTitle>
        </CardHeader>
        <CardContent className="p-0">
          <ScrollArea className="h-[500px]">
            {users.map((user) => (
              <div
                key={user.userId}
                onClick={() => setSelectedUser(user.userId)}
                className={`p-3 cursor-pointer border-b hover:bg-gray-50 ${
                  selectedUser === user.userId ? 'bg-blue-50 border-blue-200' : ''
                }`}
              >
                <div className="flex items-center gap-2">
                  <div className={`w-2 h-2 rounded-full ${user.isOnline ? 'bg-green-500' : 'bg-gray-400'}`} />
                  <span className="text-sm font-medium">{user.username}</span>
                </div>
              </div>
            ))}
          </ScrollArea>
        </CardContent>
      </Card>

      {/* Chat Area */}
      <Card className="flex-1">
        <CardHeader className="pb-3">
          <CardTitle className="flex items-center gap-2">
            <Shield className="w-5 h-5" />
            Admin Chat
            {selectedUser && (
              <span className="text-sm font-normal text-gray-600">
                - {users.find(u => u.userId === selectedUser)?.username}
              </span>
            )}
            <div className={`ml-auto w-3 h-3 rounded-full ${isConnected ? 'bg-green-500' : 'bg-red-500'}`} />
          </CardTitle>
        </CardHeader>
        
        <CardContent className="flex-1 flex flex-col p-0">
          <ScrollArea className="flex-1 px-4">
            <div className="space-y-4 pb-4">
              {selectedUser ? (
                filteredMessages.map((message) => (
                  <div
                    key={message.id}
                    className={`flex ${message.isAdmin ? 'justify-end' : 'justify-start'}`}
                  >
                    <div
                      className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${
                        message.isAdmin
                          ? 'bg-blue-500 text-white'
                          : 'bg-gray-100 text-gray-900'
                      }`}
                    >
                      <p className="text-sm">{message.content}</p>
                      <p className="text-xs mt-1 opacity-70">
                        {message.timestamp.toLocaleTimeString()}
                      </p>
                    </div>
                  </div>
                ))
              ) : (
                <div className="text-center text-gray-500 mt-8">
                  Select a user to start chatting
                </div>
              )}
              <div ref={messagesEndRef} />
            </div>
          </ScrollArea>
          
          <div className="border-t p-4">
            <div className="flex gap-2">
              <Input
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                placeholder={selectedUser ? "Type your response..." : "Select a user first..."}
                disabled={!isConnected || !selectedUser}
                className="flex-1"
                onKeyPress={(e) => {
                  if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    handleSendMessage();
                  }
                }}
              />
              <Button
                onClick={handleSendMessage}
                disabled={!newMessage.trim() || !isConnected || !selectedUser}
                size="icon"
              >
                <Send className="w-4 h-4" />
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};
