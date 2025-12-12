import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { format } from 'date-fns';
import {
  MessageSquare,
  Phone,
  User,
  Send,
  AlertCircle,
  CheckCircle,
  X,
} from 'lucide-react';
import toast from 'react-hot-toast';

import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Badge } from '@/components/ui/Badge';
import { Loading } from '@/components/ui/Loading';
import { EmptyState } from '@/components/ui/EmptyState';
import { conversationsApi } from '@/api/conversations';
import type { Conversation, ConversationMessage } from '@/types';
import { cn } from '@/utils/cn';

export function ConversationsPage() {
  const queryClient = useQueryClient();
  const [selectedConversation, setSelectedConversation] = useState<Conversation | null>(null);
  const [messageInput, setMessageInput] = useState('');
  const [filter, setFilter] = useState<'all' | 'active' | 'handoff'>('all');

  const { data: conversationsData, isLoading } = useQuery({
    queryKey: ['conversations', filter],
    queryFn: () => conversationsApi.getAll({
      status: filter === 'active' ? 'active' : undefined,
      handedOff: filter === 'handoff' ? true : undefined,
      limit: 50,
    }),
  });

  const { data: messages, isLoading: messagesLoading } = useQuery({
    queryKey: ['conversation-messages', selectedConversation?.id],
    queryFn: () => selectedConversation ? conversationsApi.getMessages(selectedConversation.id) : Promise.resolve([]),
    enabled: !!selectedConversation,
    refetchInterval: 5000,
  });

  const sendMessageMutation = useMutation({
    mutationFn: ({ id, content }: { id: string; content: string }) =>
      conversationsApi.sendMessage(id, content),
    onSuccess: () => {
      setMessageInput('');
      queryClient.invalidateQueries({ queryKey: ['conversation-messages', selectedConversation?.id] });
      toast.success('Message sent');
    },
    onError: () => {
      toast.error('Failed to send message');
    },
  });

  const resolveHandoffMutation = useMutation({
    mutationFn: (id: string) => conversationsApi.resolveHandoff(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['conversations'] });
      toast.success('Handoff resolved');
    },
  });

  const closeConversationMutation = useMutation({
    mutationFn: (id: string) => conversationsApi.close(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['conversations'] });
      setSelectedConversation(null);
      toast.success('Conversation closed');
    },
  });

  const conversations = conversationsData?.data || [];

  const handleSendMessage = () => {
    if (!selectedConversation || !messageInput.trim()) return;
    sendMessageMutation.mutate({
      id: selectedConversation.id,
      content: messageInput.trim(),
    });
  };

  const getStateColor = (state: string): 'default' | 'primary' | 'success' | 'warning' | 'danger' | 'gray' => {
    switch (state) {
      case 'greeting': return 'default';
      case 'browsing': return 'primary';
      case 'cart': return 'warning';
      case 'checkout': return 'warning';
      case 'completed': return 'success';
      default: return 'default';
    }
  };

  if (isLoading) {
    return <Loading />;
  }

  return (
    <div className="h-[calc(100vh-8rem)] flex flex-col">
      <div className="mb-4">
        <h1 className="text-2xl font-bold text-gray-900">Conversations</h1>
        <p className="text-gray-600">Manage WhatsApp customer conversations</p>
      </div>

      {/* Filter tabs */}
      <div className="flex gap-2 mb-4">
        {(['all', 'active', 'handoff'] as const).map((f) => (
          <Button
            key={f}
            variant={filter === f ? 'primary' : 'outline'}
            size="sm"
            onClick={() => setFilter(f)}
          >
            {f === 'all' ? 'All' : f === 'active' ? 'Active' : 'Needs Attention'}
          </Button>
        ))}
      </div>

      <div className="flex-1 flex gap-4 min-h-0">
        {/* Conversations list */}
        <Card className="w-96 flex flex-col overflow-hidden">
          <div className="p-4 border-b border-gray-200">
            <h2 className="font-semibold text-gray-900">
              {conversations.length} Conversation{conversations.length !== 1 ? 's' : ''}
            </h2>
          </div>
          <div className="flex-1 overflow-y-auto">
            {conversations.length === 0 ? (
              <EmptyState
                icon={<MessageSquare className="w-8 h-8 text-gray-400" />}
                title="No conversations"
                description="Conversations will appear here when customers message you"
              />
            ) : (
              conversations.map((conversation) => (
                <div
                  key={conversation.id}
                  onClick={() => setSelectedConversation(conversation)}
                  className={cn(
                    'p-4 border-b border-gray-100 cursor-pointer hover:bg-gray-50 transition-colors',
                    selectedConversation?.id === conversation.id && 'bg-primary-50'
                  )}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center">
                        <User className="w-5 h-5 text-gray-500" />
                      </div>
                      <div>
                        <p className="font-medium text-gray-900">
                          {conversation.customerName || 'Unknown'}
                        </p>
                        <p className="text-sm text-gray-500">{conversation.customerPhone}</p>
                      </div>
                    </div>
                    {conversation.isHandedOff && (
                      <AlertCircle className="w-5 h-5 text-amber-500" />
                    )}
                  </div>
                  <div className="mt-2 flex items-center gap-2">
                    <Badge variant={getStateColor(conversation.state)}>
                      {conversation.state}
                    </Badge>
                    {conversation.messageCount > 0 && (
                      <span className="text-xs text-gray-500">
                        {conversation.messageCount} messages
                      </span>
                    )}
                  </div>
                  {conversation.lastMessageAt && (
                    <p className="mt-1 text-xs text-gray-400">
                      {format(new Date(conversation.lastMessageAt), 'MMM d, h:mm a')}
                    </p>
                  )}
                </div>
              ))
            )}
          </div>
        </Card>

        {/* Chat view */}
        <Card className="flex-1 flex flex-col overflow-hidden">
          {selectedConversation ? (
            <>
              {/* Chat header */}
              <div className="p-4 border-b border-gray-200 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center">
                    <User className="w-5 h-5 text-gray-500" />
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">
                      {selectedConversation.customerName || 'Unknown Customer'}
                    </p>
                    <p className="text-sm text-gray-500 flex items-center gap-1">
                      <Phone className="w-3 h-3" />
                      {selectedConversation.customerPhone}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  {selectedConversation.isHandedOff && (
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => resolveHandoffMutation.mutate(selectedConversation.id)}
                      disabled={resolveHandoffMutation.isPending}
                    >
                      <CheckCircle className="w-4 h-4 mr-1" />
                      Resolve
                    </Button>
                  )}
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => closeConversationMutation.mutate(selectedConversation.id)}
                    disabled={closeConversationMutation.isPending}
                  >
                    <X className="w-4 h-4 mr-1" />
                    Close
                  </Button>
                </div>
              </div>

              {/* Messages */}
              <div className="flex-1 overflow-y-auto p-4 space-y-4">
                {messagesLoading ? (
                  <Loading />
                ) : messages && messages.length > 0 ? (
                  messages.map((message: ConversationMessage) => (
                    <div
                      key={message.id}
                      className={cn(
                        'flex',
                        message.direction === 'outbound' ? 'justify-end' : 'justify-start'
                      )}
                    >
                      <div
                        className={cn(
                          'max-w-[70%] rounded-lg px-4 py-2',
                          message.direction === 'outbound'
                            ? 'bg-primary-600 text-white'
                            : 'bg-gray-100 text-gray-900'
                        )}
                      >
                        <p className="text-sm whitespace-pre-wrap">{message.content}</p>
                        <p
                          className={cn(
                            'text-xs mt-1',
                            message.direction === 'outbound' ? 'text-primary-200' : 'text-gray-500'
                          )}
                        >
                          {format(new Date(message.createdAt), 'h:mm a')}
                        </p>
                      </div>
                    </div>
                  ))
                ) : (
                  <EmptyState
                    icon={<MessageSquare className="w-8 h-8 text-gray-400" />}
                    title="No messages yet"
                    description="Messages will appear here"
                  />
                )}
              </div>

              {/* Message input */}
              <div className="p-4 border-t border-gray-200">
                <div className="flex gap-2">
                  <Input
                    value={messageInput}
                    onChange={(e) => setMessageInput(e.target.value)}
                    placeholder="Type a message..."
                    onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
                    className="flex-1"
                  />
                  <Button
                    onClick={handleSendMessage}
                    disabled={!messageInput.trim() || sendMessageMutation.isPending}
                  >
                    <Send className="w-4 h-4" />
                  </Button>
                </div>
              </div>
            </>
          ) : (
            <div className="flex-1 flex items-center justify-center">
              <EmptyState
                icon={<MessageSquare className="w-8 h-8 text-gray-400" />}
                title="Select a conversation"
                description="Choose a conversation from the list to view messages"
              />
            </div>
          )}
        </Card>
      </div>
    </div>
  );
}
