import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  ArrowLeft,
  Send,
  Share2,
  MoreVertical,
  Check,
  X,
  MessageCircle,
  Copy,
} from 'lucide-react';
import { invoicesApi } from '@/api/invoices';
import { Button, Card, Badge, Modal, Loading } from '@/components/ui';
import { formatCurrency, formatDate, formatPhone } from '@/utils/format';
import { shareToWhatsApp, generatePaymentLink } from '@/utils/whatsapp';
import toast from 'react-hot-toast';

export function InvoiceDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [showActionsMenu, setShowActionsMenu] = useState(false);
  const [showCancelModal, setShowCancelModal] = useState(false);
  const [showShareModal, setShowShareModal] = useState(false);

  const { data: invoice, isLoading, error } = useQuery({
    queryKey: ['invoice', id],
    queryFn: () => invoicesApi.getById(id!),
    enabled: !!id,
  });

  const sendMutation = useMutation({
    mutationFn: () => invoicesApi.send(id!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['invoice', id] });
      queryClient.invalidateQueries({ queryKey: ['invoices'] });
      toast.success('Invoice sent');
    },
    onError: () => {
      toast.error('Failed to send invoice');
    },
  });

  const cancelMutation = useMutation({
    mutationFn: () => invoicesApi.cancel(id!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['invoice', id] });
      queryClient.invalidateQueries({ queryKey: ['invoices'] });
      toast.success('Invoice cancelled');
      setShowCancelModal(false);
    },
    onError: () => {
      toast.error('Failed to cancel invoice');
    },
  });

  const markPaidMutation = useMutation({
    mutationFn: () => invoicesApi.markPaid(id!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['invoice', id] });
      queryClient.invalidateQueries({ queryKey: ['invoices'] });
      toast.success('Invoice marked as paid');
    },
    onError: () => {
      toast.error('Failed to mark invoice as paid');
    },
  });

  if (isLoading) {
    return <Loading />;
  }

  if (error || !invoice) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-600">Invoice not found</p>
        <Button variant="outline" onClick={() => navigate('/invoices')} className="mt-4">
          Back to Invoices
        </Button>
      </div>
    );
  }

  const getStatusBadge = (status: string) => {
    const variants: Record<string, 'success' | 'warning' | 'error' | 'default'> = {
      PAID: 'success',
      PARTIALLY_PAID: 'warning',
      PENDING: 'warning',
      OVERDUE: 'error',
      DRAFT: 'default',
      CANCELLED: 'default',
    };

    const labels: Record<string, string> = {
      PAID: 'Paid',
      PARTIALLY_PAID: 'Partially Paid',
      PENDING: 'Pending',
      OVERDUE: 'Overdue',
      DRAFT: 'Draft',
      CANCELLED: 'Cancelled',
    };

    return (
      <Badge variant={variants[status] || 'default'}>
        {labels[status] || status}
      </Badge>
    );
  };

  const canSend = invoice.status === 'DRAFT';
  const canCancel = ['DRAFT', 'PENDING'].includes(invoice.status);
  const canMarkPaid = ['PENDING', 'PARTIALLY_PAID', 'OVERDUE'].includes(invoice.status);
  const canShare = invoice.status !== 'DRAFT' && invoice.status !== 'CANCELLED';

  const paymentLink = generatePaymentLink(invoice.id);

  const handleCopyLink = () => {
    navigator.clipboard.writeText(paymentLink);
    toast.success('Payment link copied');
  };

  const handleWhatsAppShare = () => {
    if (!invoice.customer?.phone) {
      toast.error('Customer phone number not available');
      return;
    }
    shareToWhatsApp(invoice.customer.phone, invoice);
    setShowShareModal(false);
  };

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button
            onClick={() => navigate(-1)}
            className="p-2 rounded-lg hover:bg-gray-100"
          >
            <ArrowLeft className="h-5 w-5" />
          </button>
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-bold text-gray-900">{invoice.invoiceNumber}</h1>
              {getStatusBadge(invoice.status)}
            </div>
            <p className="text-gray-600 mt-1">
              Created {formatDate(invoice.createdAt)}
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          {canShare && (
            <Button
              variant="outline"
              onClick={() => setShowShareModal(true)}
              leftIcon={<Share2 className="h-4 w-4" />}
            >
              Share
            </Button>
          )}

          {canSend && (
            <Button
              onClick={() => sendMutation.mutate()}
              loading={sendMutation.isPending}
              leftIcon={<Send className="h-4 w-4" />}
            >
              Send
            </Button>
          )}

          {/* More actions */}
          <div className="relative">
            <Button
              variant="outline"
              onClick={() => setShowActionsMenu(!showActionsMenu)}
            >
              <MoreVertical className="h-4 w-4" />
            </Button>

            {showActionsMenu && (
              <>
                <div
                  className="fixed inset-0 z-10"
                  onClick={() => setShowActionsMenu(false)}
                />
                <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-20">
                  {canMarkPaid && (
                    <button
                      onClick={() => {
                        markPaidMutation.mutate();
                        setShowActionsMenu(false);
                      }}
                      className="flex items-center gap-2 w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                    >
                      <Check className="h-4 w-4" />
                      Mark as Paid
                    </button>
                  )}
                  {canCancel && (
                    <button
                      onClick={() => {
                        setShowCancelModal(true);
                        setShowActionsMenu(false);
                      }}
                      className="flex items-center gap-2 w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                    >
                      <X className="h-4 w-4" />
                      Cancel Invoice
                    </button>
                  )}
                </div>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Customer Info */}
      <Card className="p-6">
        <h3 className="font-semibold text-gray-900 mb-4">Customer</h3>
        <div className="flex items-center gap-4">
          <div className="w-12 h-12 bg-primary-100 rounded-full flex items-center justify-center">
            <span className="text-primary-700 font-semibold text-lg">
              {invoice.customer?.name?.charAt(0) || '?'}
            </span>
          </div>
          <div>
            <p className="font-medium text-gray-900">{invoice.customer?.name || 'Unknown'}</p>
            <p className="text-sm text-gray-500">
              {invoice.customer?.phone ? formatPhone(invoice.customer.phone) : 'No phone'}
            </p>
            {invoice.customer?.email && (
              <p className="text-sm text-gray-500">{invoice.customer.email}</p>
            )}
          </div>
        </div>
      </Card>

      {/* Invoice Details */}
      <Card className="p-6">
        <div className="grid grid-cols-2 gap-4 mb-6">
          <div>
            <p className="text-sm text-gray-500">Issue Date</p>
            <p className="font-medium">{formatDate(invoice.issueDate)}</p>
          </div>
          <div>
            <p className="text-sm text-gray-500">Due Date</p>
            <p className="font-medium">{formatDate(invoice.dueDate)}</p>
          </div>
        </div>

        <h3 className="font-semibold text-gray-900 mb-4">Items</h3>
        <div className="space-y-3">
          {invoice.items?.map((item, index) => (
            <div
              key={index}
              className="flex justify-between items-start py-3 border-b border-gray-100 last:border-0"
            >
              <div className="flex-1">
                <p className="font-medium text-gray-900">{item.name}</p>
                <p className="text-sm text-gray-500">
                  {item.quantity} × {formatCurrency(item.price)}
                </p>
              </div>
              <p className="font-medium text-gray-900 ml-4">
                {formatCurrency(item.total)}
              </p>
            </div>
          ))}
        </div>

        <div className="border-t border-gray-200 mt-4 pt-4">
          <div className="flex justify-between items-center">
            <span className="text-lg font-semibold text-gray-900">Total</span>
            <span className="text-2xl font-bold text-primary-600">
              {formatCurrency(invoice.total)}
            </span>
          </div>
          {invoice.paidAmount > 0 && invoice.paidAmount < invoice.total && (
            <div className="flex justify-between items-center mt-2 text-sm">
              <span className="text-gray-600">Paid</span>
              <span className="text-green-600 font-medium">
                {formatCurrency(invoice.paidAmount)}
              </span>
            </div>
          )}
          {invoice.paidAmount > 0 && invoice.paidAmount < invoice.total && (
            <div className="flex justify-between items-center mt-1 text-sm">
              <span className="text-gray-600">Balance</span>
              <span className="text-red-600 font-medium">
                {formatCurrency(invoice.total - invoice.paidAmount)}
              </span>
            </div>
          )}
        </div>

        {invoice.notes && (
          <div className="mt-6 pt-4 border-t border-gray-200">
            <p className="text-sm text-gray-500">Notes</p>
            <p className="mt-1 text-gray-700">{invoice.notes}</p>
          </div>
        )}
      </Card>

      {/* Cancel Modal */}
      <Modal
        open={showCancelModal}
        onClose={() => setShowCancelModal(false)}
        title="Cancel Invoice"
      >
        <p className="text-gray-600">
          Are you sure you want to cancel invoice <strong>{invoice.invoiceNumber}</strong>?
          This action cannot be undone.
        </p>
        <div className="flex justify-end gap-3 mt-6">
          <Button variant="outline" onClick={() => setShowCancelModal(false)}>
            Keep Invoice
          </Button>
          <Button
            variant="danger"
            loading={cancelMutation.isPending}
            onClick={() => cancelMutation.mutate()}
          >
            Cancel Invoice
          </Button>
        </div>
      </Modal>

      {/* Share Modal */}
      <Modal
        open={showShareModal}
        onClose={() => setShowShareModal(false)}
        title="Share Invoice"
      >
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Payment Link
            </label>
            <div className="flex gap-2">
              <div className="flex-1 px-3 py-2 bg-gray-50 rounded-lg border border-gray-200 text-sm text-gray-600 truncate">
                {paymentLink}
              </div>
              <Button variant="outline" onClick={handleCopyLink}>
                <Copy className="h-4 w-4" />
              </Button>
            </div>
          </div>

          <div className="border-t border-gray-200 pt-4">
            <Button
              className="w-full"
              onClick={handleWhatsAppShare}
              leftIcon={<MessageCircle className="h-4 w-4" />}
            >
              Share via WhatsApp
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
