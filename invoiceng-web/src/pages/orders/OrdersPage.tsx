import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { format } from 'date-fns';
import {
  ShoppingCart,
  Truck,
  CheckCircle,
  XCircle,
  Clock,
  CreditCard,
  MapPin,
  Phone,
  Eye
} from 'lucide-react';
import toast from 'react-hot-toast';

import { Card, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Loading } from '@/components/ui/Loading';
import { EmptyState } from '@/components/ui/EmptyState';
import { Modal } from '@/components/ui/Modal';
import { Input } from '@/components/ui/Input';
import { ordersApi } from '@/api/orders';
import type { WhatsAppOrder } from '@/types';
import { formatCurrency } from '@/utils/format';

export function OrdersPage() {
  const queryClient = useQueryClient();
  const [filter, setFilter] = useState<string>('all');
  const [selectedOrder, setSelectedOrder] = useState<WhatsAppOrder | null>(null);
  const [trackingNumber, setTrackingNumber] = useState('');
  const [showShipModal, setShowShipModal] = useState(false);

  const { data: ordersData, isLoading } = useQuery({
    queryKey: ['orders', filter],
    queryFn: () => ordersApi.getAll({
      paymentStatus: filter === 'paid' || filter === 'pending' ? filter : undefined,
      fulfillmentStatus: filter === 'shipped' || filter === 'delivered' ? filter : undefined,
      limit: 50,
    }),
  });

  const markPaidMutation = useMutation({
    mutationFn: (id: string) => ordersApi.markAsPaid(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      toast.success('Order marked as paid');
    },
    onError: () => toast.error('Failed to update order'),
  });

  const shipMutation = useMutation({
    mutationFn: ({ id, trackingNumber }: { id: string; trackingNumber?: string }) =>
      ordersApi.ship(id, trackingNumber),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      setShowShipModal(false);
      setTrackingNumber('');
      toast.success('Order marked as shipped');
    },
    onError: () => toast.error('Failed to update order'),
  });

  const deliverMutation = useMutation({
    mutationFn: (id: string) => ordersApi.deliver(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      toast.success('Order marked as delivered');
    },
    onError: () => toast.error('Failed to update order'),
  });

  const cancelMutation = useMutation({
    mutationFn: (id: string) => ordersApi.cancel(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      setSelectedOrder(null);
      toast.success('Order cancelled');
    },
    onError: () => toast.error('Failed to cancel order'),
  });

  const orders = ordersData?.data || [];

  const getPaymentStatusBadge = (status: string) => {
    switch (status) {
      case 'paid': return <Badge variant="success">Paid</Badge>;
      case 'pending': return <Badge variant="warning">Pending</Badge>;
      case 'failed': return <Badge variant="danger">Failed</Badge>;
      default: return <Badge>{status}</Badge>;
    }
  };

  const getFulfillmentStatusBadge = (status: string) => {
    switch (status) {
      case 'pending': return <Badge variant="default"><Clock className="w-3 h-3 mr-1" />Pending</Badge>;
      case 'shipped': return <Badge variant="primary"><Truck className="w-3 h-3 mr-1" />Shipped</Badge>;
      case 'delivered': return <Badge variant="success"><CheckCircle className="w-3 h-3 mr-1" />Delivered</Badge>;
      case 'cancelled': return <Badge variant="danger"><XCircle className="w-3 h-3 mr-1" />Cancelled</Badge>;
      default: return <Badge>{status}</Badge>;
    }
  };

  if (isLoading) {
    return <Loading />;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Orders</h1>
        <p className="text-gray-600">Manage WhatsApp orders and fulfillment</p>
      </div>

      {/* Filter tabs */}
      <div className="flex gap-2 flex-wrap">
        {[
          { value: 'all', label: 'All Orders' },
          { value: 'pending', label: 'Payment Pending' },
          { value: 'paid', label: 'Paid' },
          { value: 'shipped', label: 'Shipped' },
          { value: 'delivered', label: 'Delivered' },
        ].map((f) => (
          <Button
            key={f.value}
            variant={filter === f.value ? 'primary' : 'outline'}
            size="sm"
            onClick={() => setFilter(f.value)}
          >
            {f.label}
          </Button>
        ))}
      </div>

      {/* Orders table */}
      <Card>
        <CardContent className="p-0">
          {orders.length === 0 ? (
            <div className="p-8">
              <EmptyState
                icon={<ShoppingCart className="w-8 h-8 text-gray-400" />}
                title="No orders found"
                description="Orders from WhatsApp conversations will appear here"
              />
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Order</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Customer</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Total</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Payment</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fulfillment</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {orders.map((order) => (
                    <tr key={order.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3">
                        <span className="font-medium text-gray-900">#{order.orderNumber}</span>
                      </td>
                      <td className="px-4 py-3">
                        <div>
                          <p className="text-sm font-medium text-gray-900">{order.customerName}</p>
                          <p className="text-sm text-gray-500">{order.customerPhone}</p>
                        </div>
                      </td>
                      <td className="px-4 py-3">
                        <span className="font-medium text-gray-900">{formatCurrency(order.total)}</span>
                      </td>
                      <td className="px-4 py-3">{getPaymentStatusBadge(order.paymentStatus)}</td>
                      <td className="px-4 py-3">{getFulfillmentStatusBadge(order.fulfillmentStatus)}</td>
                      <td className="px-4 py-3 text-sm text-gray-500">
                        {format(new Date(order.createdAt), 'MMM d, yyyy')}
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex gap-2">
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => setSelectedOrder(order)}
                          >
                            <Eye className="w-4 h-4" />
                          </Button>
                          {order.paymentStatus === 'pending' && (
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => markPaidMutation.mutate(order.id)}
                              disabled={markPaidMutation.isPending}
                            >
                              <CreditCard className="w-4 h-4" />
                            </Button>
                          )}
                          {order.paymentStatus === 'paid' && order.fulfillmentStatus === 'pending' && (
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => {
                                setSelectedOrder(order);
                                setShowShipModal(true);
                              }}
                            >
                              <Truck className="w-4 h-4" />
                            </Button>
                          )}
                          {order.fulfillmentStatus === 'shipped' && (
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => deliverMutation.mutate(order.id)}
                              disabled={deliverMutation.isPending}
                            >
                              <CheckCircle className="w-4 h-4" />
                            </Button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Order detail modal */}
      <Modal
        isOpen={!!selectedOrder && !showShipModal}
        onClose={() => setSelectedOrder(null)}
        title={`Order #${selectedOrder?.orderNumber}`}
      >
        {selectedOrder && (
          <div className="space-y-4">
            <div className="flex gap-2">
              {getPaymentStatusBadge(selectedOrder.paymentStatus)}
              {getFulfillmentStatusBadge(selectedOrder.fulfillmentStatus)}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <h4 className="text-sm font-medium text-gray-500">Customer</h4>
                <p className="text-gray-900">{selectedOrder.customerName}</p>
                <p className="text-sm text-gray-500 flex items-center gap-1">
                  <Phone className="w-3 h-3" />
                  {selectedOrder.customerPhone}
                </p>
              </div>
              <div>
                <h4 className="text-sm font-medium text-gray-500">Delivery Address</h4>
                <p className="text-gray-900 flex items-start gap-1">
                  <MapPin className="w-3 h-3 mt-1" />
                  {selectedOrder.deliveryAddress}
                </p>
              </div>
            </div>

            <div>
              <h4 className="text-sm font-medium text-gray-500 mb-2">Items</h4>
              <div className="border border-gray-200 rounded-lg overflow-hidden">
                <table className="w-full">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-3 py-2 text-left text-xs font-medium text-gray-500">Item</th>
                      <th className="px-3 py-2 text-right text-xs font-medium text-gray-500">Qty</th>
                      <th className="px-3 py-2 text-right text-xs font-medium text-gray-500">Price</th>
                      <th className="px-3 py-2 text-right text-xs font-medium text-gray-500">Total</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200">
                    {selectedOrder.items?.map((item: any, idx: number) => (
                      <tr key={idx}>
                        <td className="px-3 py-2 text-sm text-gray-900">{item.productName}</td>
                        <td className="px-3 py-2 text-sm text-gray-500 text-right">{item.quantity}</td>
                        <td className="px-3 py-2 text-sm text-gray-500 text-right">{formatCurrency(item.price)}</td>
                        <td className="px-3 py-2 text-sm text-gray-900 text-right">{formatCurrency(item.total)}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot className="bg-gray-50">
                    <tr>
                      <td colSpan={3} className="px-3 py-2 text-sm font-medium text-gray-900">Total</td>
                      <td className="px-3 py-2 text-sm font-bold text-gray-900 text-right">
                        {formatCurrency(selectedOrder.total)}
                      </td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            </div>

            {selectedOrder.fulfillmentStatus !== 'cancelled' && selectedOrder.fulfillmentStatus !== 'delivered' && (
              <div className="flex justify-end gap-2 pt-4 border-t">
                <Button
                  variant="outline"
                  onClick={() => cancelMutation.mutate(selectedOrder.id)}
                  disabled={cancelMutation.isPending}
                >
                  <XCircle className="w-4 h-4 mr-1" />
                  Cancel Order
                </Button>
              </div>
            )}
          </div>
        )}
      </Modal>

      {/* Ship modal */}
      <Modal
        isOpen={showShipModal}
        onClose={() => {
          setShowShipModal(false);
          setTrackingNumber('');
        }}
        title="Ship Order"
      >
        <div className="space-y-4">
          <p className="text-gray-600">
            Add an optional tracking number for order #{selectedOrder?.orderNumber}
          </p>
          <Input
            label="Tracking Number (optional)"
            value={trackingNumber}
            onChange={(e) => setTrackingNumber(e.target.value)}
            placeholder="Enter tracking number"
          />
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => setShowShipModal(false)}>
              Cancel
            </Button>
            <Button
              onClick={() => selectedOrder && shipMutation.mutate({
                id: selectedOrder.id,
                trackingNumber: trackingNumber || undefined,
              })}
              disabled={shipMutation.isPending}
            >
              <Truck className="w-4 h-4 mr-1" />
              Mark as Shipped
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
