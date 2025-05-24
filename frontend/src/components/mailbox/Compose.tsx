
import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Card } from '@/components/ui/card';
import { Send, User } from 'lucide-react';

interface ComposeProps {
  onSendMessage: (subject: string, content: string) => Promise<boolean>;
}

export const Compose = ({ onSendMessage }: ComposeProps) => {
  const [subject, setSubject] = useState('');
  const [content, setContent] = useState('');
  const [isSending, setIsSending] = useState(false);

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!subject.trim() || !content.trim()) {
      return;
    }

    setIsSending(true);
    const success = await onSendMessage(subject.trim(), content.trim());
    
    if (success) {
      setSubject('');
      setContent('');
    }
    
    setIsSending(false);
  };

  return (
    <div className="max-w-2xl">
      <div className="flex items-center space-x-3 mb-6">
        <Send className="w-6 h-6 text-blue-600" />
        <h2 className="text-lg font-semibold text-gray-900">Compose Message</h2>
      </div>

      <Card className="p-6">
        <form onSubmit={handleSend} className="space-y-4">
          <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
            <User className="w-5 h-5 text-gray-600" />
            <div>
              <p className="text-sm font-medium text-gray-900">To: Admin</p>
              <p className="text-xs text-gray-600">System Administrator</p>
            </div>
          </div>

          <div>
            <label htmlFor="subject" className="block text-sm font-medium text-gray-700 mb-2">
              Subject *
            </label>
            <Input
              id="subject"
              type="text"
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
              placeholder="Enter message subject"
              required
              className="w-full"
            />
          </div>

          <div>
            <label htmlFor="content" className="block text-sm font-medium text-gray-700 mb-2">
              Message *
            </label>
            <Textarea
              id="content"
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="Type your message here..."
              required
              rows={6}
              className="w-full resize-none"
            />
            <p className="text-xs text-gray-500 mt-2">
              {content.length}/1000 characters
            </p>
          </div>

          <div className="flex items-center justify-between pt-4">
            <p className="text-sm text-gray-600">
              Fields marked with * are required
            </p>
            <div className="flex space-x-3">
              <Button
                type="button"
                variant="outline"
                onClick={() => {
                  setSubject('');
                  setContent('');
                }}
                disabled={isSending}
              >
                Clear
              </Button>
              <Button
                type="submit"
                disabled={!subject.trim() || !content.trim() || isSending}
                className="flex items-center space-x-2"
              >
                {isSending ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    <span>Sending...</span>
                  </>
                ) : (
                  <>
                    <Send className="w-4 h-4" />
                    <span>Send Message</span>
                  </>
                )}
              </Button>
            </div>
          </div>
        </form>
      </Card>

      <div className="mt-6 p-4 bg-blue-50 rounded-lg border border-blue-200">
        <div className="flex items-start space-x-3">
          <div className="w-5 h-5 bg-blue-600 rounded-full flex items-center justify-center mt-0.5">
            <span className="text-white text-xs font-bold">i</span>
          </div>
          <div>
            <h4 className="text-sm font-medium text-blue-900 mb-1">Message Guidelines</h4>
            <ul className="text-xs text-blue-800 space-y-1">
              <li>• Messages are sent directly to the system administrator</li>
              <li>• Please be clear and specific about your request or issue</li>
              <li>• Response time is typically within 24 hours</li>
              <li>• Keep messages professional and respectful</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};
