import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Send } from "lucide-react";
import { useToast } from "@/components/ui/use-toast";
import { useQuery } from "@tanstack/react-query";
import { useEffect } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
function ChatComponent () {
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState([
    {
      id: 1,
      text: "J'ai besoin de clarifications sur l'allocation des heures d'enseignement.",
      timestamp: "2024-03-15 10:30",
      sender: "user"
    },
    {
      id: 2,
      text: "Votre demande a été reçue. Un administrateur vous répondra bientôt.",
      timestamp: "2024-03-15 10:35",
      sender: "admin"
    }
  ]);
const [input, setInput] = useState("");

  const stompClient = new Client({
    webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
    connectHeaders: { Authorization: localStorage.getItem("jwt") },
    reconnectDelay: 5000
  });

  useEffect(() => {
    stompClient.onConnect = () => {
      stompClient.subscribe("/topic/messages", (msg) => {
        const received = JSON.parse(msg.body);
        setMessages(prev => [...prev, received]);
      });
    };
    stompClient.activate();
    return () => stompClient.deactivate();
  }, []);
  const handleMessageSend = () => {
    if (message.trim()) {
      setMessages([
        ...messages,
        {
          id: messages.length + 1,
          text: message,
          timestamp: new Date().toLocaleString(),
          sender: "user",
          role:"TEACHER",
        }
      ]);
      setMessage("");
    }
  };
const sendMessage = () => {
    stompClient.publish({
      destination: "/app/chat.send",
      body: JSON.stringify({ content: input })
    });
    setInput("");
  };

      return(
                <div className="h-[400px] flex flex-col">
                  <ScrollArea className="flex-1 pr-4 mb-4">
                    <div className="space-y-4">
                      {messages.map((msg) => (
                        <div key={msg.id} className={`flex ${msg.sender === "user" ? "justify-end" : "justify-start"}`}>
                          <div className={`max-w-[80%] rounded-lg p-3 ${msg.sender === "user" ? "bg-primary text-white" : "bg-gray-100"}`}>
                            <p>{msg.text}</p>
                            <p className="text-xs mt-1 opacity-70">{msg.timestamp}</p>
                          </div>
                        </div>
                      ))}
                    </div>
                  </ScrollArea>
                  <div className="flex gap-2">
                    <Textarea
                      placeholder="Écrivez votre message..."
                      value={message}
                      onChange={(e) => setMessage(e.target.value)}
                      className="flex-1"
                    />
                    <Button onClick={handleMessageSend} className="flex-shrink-0">
                      <Send className="w-4 h-4" />
                    </Button>
                  </div>
                </div>)
};
export default ChatComponent;

