import { Message } from '../components/Mailbox';
import {jwtDecode} from 'jwt-decode';
import axios from 'axios';
import { handleSendMessage } from '@/components/Mailbox';
// Configure your Spring API base URL here
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/messages';
type teacher = {
  id:number;
  name:string;
  email:string;
  role:string;
}

class MailService {
  private async makeRequest(endpoint: string, options: RequestInit = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    
    const defaultHeaders = {
      'Content-Type': 'application/json',
      // Add any authentication headers here if needed
       'Authorization': localStorage.getItem('jwt') || '',
    };

    const response = await fetch(url, {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers,
      },
    });

    if (!response.ok) {
      throw new Error(`API request failed: ${response.status} ${response.statusText}`);
    }

    return response.json();
  }

  async getInboxMessages(userId:number): Promise<Message[]> {
    try {
      // Adjust the endpoint to match your Spring API
	const token=localStorage.getItem('jwt');
      const messages = await this.makeRequest('/history/'+userId);
      //add sender and recipient fields to the response
      if(userId !==1){
      Promise.all(messages.map(async (msg: any) => { const user=await axios.get("http://localhost:8080/api/users/"+msg.sender,{headers:{Authorization: token}});
      const reciepent=await axios.get("http://localhost:8080/api/users/"+userId,{headers:{Authorization: token}});
      msg.senderName=user.data.name;msg.recipientName=reciepent.data.name; return msg;}));}else{
      Promise.all(messages.map(async (msg: any) => { const user=await axios.get("http://localhost:8080/api/users/"+msg.sender,{headers:{Authorization: token}});return {...msg,senderName:user.data.name,recipientName:'Admin'};}));}

      
      return messages.map((msg: any) => ({
        id: msg.id,
        subject: msg.subject,
        content: msg.content,
        userId: msg.sender || '1',
        recipient: msg.recipient,
        timestamp: msg.timestamp || msg.createdAt,
        isRead: msg.isRead || msg.read || false,
        recipientName: msg?.recipientName || 'ADMIN',
        senderName: msg?.senderName || 'Admin',
      }));
    } catch (error) {
      console.error('Error fetching inbox messages:', error);
      // Return mock data for development/testing
      return this.getMockInboxMessages();
    }
  }

  async getSentMessages(userId:number): Promise<Message[]> {
    try {
      // Adjust the endpoint to match your Spring API
      const messages = await this.makeRequest('/sent/'+userId);
      return messages.map((msg: any) => ({
        id: msg.id,
        subject: msg.subject,
        content: msg.content,
        sender: msg.sender,
        recipient: msg.recipient || 'admin',
        timestamp: msg.timestamp || msg.createdAt,
        isRead: true, // Sent messages are always "read"
      }));
    } catch (error) {
      console.error('Error fetching sent messages:', error);
      // Return mock data for development/testing
      return this.getMockSentMessages();
    }
  }

  async sendMessage(userId:number,sender:string,subject: string, content: string,isAdmin:Boolean): Promise<void> {
    try {
      // Adjust the endpoint and payload to match your Spring API
      await this.makeRequest('', {
        method: 'POST',
        body: JSON.stringify({
          content,
          is_admin:isAdmin,
          subject,
          sender,
          recipient: 'admin',// Always sending to admin
          userId: userId, // Assuming userId is the ID of the sender
}),
      });
    } catch (error) {
      console.error('Error sending message:', error);
      throw error;
    }
  }

  async markAsRead(messageId: number): Promise<void> {
    try {
      // Adjust the endpoint to match your Spring API
      await this.makeRequest(`/read/${messageId}`, {
        method: 'PUT',
      });
    } catch (error) {
      console.error('Error marking message as read:', error);
      throw error;
    }
  }

  // Mock data for development/testing
  private getMockInboxMessages(): Message[] {
    return [
      {
        id: 1,
        subject: "Welcome to the System",
        content: "Hello! Welcome to our messaging system. If you have any questions or need assistance, feel free to reach out to us using this mailbox feature.\n\nWe're here to help and will respond to your messages as quickly as possible.\n\nBest regards,\nAdmin Team",
        sender: "admin",
        recipient: "user",
        timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
        isRead: false,
      },
      {
        id: 2,
        subject: "System Maintenance Notice",
        content: "We will be performing scheduled maintenance on the system this weekend from 2:00 AM to 6:00 AM.\n\nDuring this time, some features may be temporarily unavailable. We apologize for any inconvenience.\n\nThank you for your understanding.",
        sender: "admin",
        recipient: "user",
        timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
        isRead: true,
      },
    ];
  }

  private getMockSentMessages(): Message[] {
    return [
      {
        id: 3,
        subject: "Question about features",
        content: "Hi Admin,\n\nI have a question about some of the features available in the system. Could you please provide more information about how to use the advanced settings?\n\nThank you!",
        sender: "user",
        recipient: "admin",
        timestamp: new Date(Date.now() - 3 * 60 * 60 * 1000).toISOString(),
        isRead: true,
      },
    ];
  }
}

export const mailService = new MailService();
