import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { format } from 'date-fns';
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import {
  MessageSquare,
  ShoppingCart,
  DollarSign,
  TrendingUp,
  Users,
  Package,
  AlertTriangle,
  ArrowUpRight,
  ArrowDownRight,
} from 'lucide-react';

import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Badge } from '@/components/ui/Badge';
import { analyticsApi } from '@/api/analytics';
import { formatCurrency } from '@/utils/format';
import { cn } from '@/utils/cn';

interface KpiCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  icon: React.ElementType;
  trend?: number;
  variant?: 'default' | 'success' | 'warning' | 'danger';
}

function KpiCard({ title, value, subtitle, icon: Icon, trend, variant = 'default' }: KpiCardProps) {
  const colorClasses = {
    default: 'bg-primary-50 text-primary-600',
    success: 'bg-green-50 text-green-600',
    warning: 'bg-amber-50 text-amber-600',
    danger: 'bg-red-50 text-red-600',
  };

  return (
    <Card>
      <CardContent className="p-6">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <p className="text-sm font-medium text-gray-500">{title}</p>
            <p className="mt-2 text-3xl font-bold text-gray-900">{value}</p>
            {subtitle && (
              <p className="mt-1 text-sm text-gray-500">{subtitle}</p>
            )}
            {trend !== undefined && (
              <div className={cn(
                'mt-2 flex items-center text-sm',
                trend >= 0 ? 'text-green-600' : 'text-red-600'
              )}>
                {trend >= 0 ? (
                  <ArrowUpRight className="w-4 h-4 mr-1" />
                ) : (
                  <ArrowDownRight className="w-4 h-4 mr-1" />
                )}
                {Math.abs(trend)}% from last period
              </div>
            )}
          </div>
          <div className={cn('p-3 rounded-lg', colorClasses[variant])}>
            <Icon className="w-6 h-6" />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

export function AnalyticsPage() {
  const [timeRange, setTimeRange] = useState(30);

  const { data: analytics, isLoading } = useQuery({
    queryKey: ['analytics', timeRange],
    queryFn: () => analyticsApi.getAnalytics(timeRange),
  });

  if (isLoading) {
    return <Loading />;
  }

  if (!analytics) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500">No analytics data available</p>
      </div>
    );
  }

  // Prepare chart data
  const revenueChartData = analytics.revenueByDay?.map((d) => ({
    date: format(new Date(d.date), 'MMM d'),
    revenue: d.value || 0,
    orders: d.count || 0,
  })) || [];

  const conversationChartData = analytics.conversationsByDay?.map((d) => ({
    date: format(new Date(d.date), 'MMM d'),
    conversations: d.count || 0,
  })) || [];

  const ordersChartData = analytics.ordersByDay?.map((d) => ({
    date: format(new Date(d.date), 'MMM d'),
    orders: d.count || 0,
  })) || [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Analytics</h1>
          <p className="text-gray-600">Track your business performance</p>
        </div>
        <div className="flex gap-2">
          {[
            { value: 7, label: '7 days' },
            { value: 30, label: '30 days' },
            { value: 90, label: '90 days' },
          ].map((range) => (
            <Button
              key={range.value}
              variant={timeRange === range.value ? 'primary' : 'outline'}
              size="sm"
              onClick={() => setTimeRange(range.value)}
            >
              {range.label}
            </Button>
          ))}
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard
          title="Total Revenue"
          value={formatCurrency(analytics.totalRevenue || 0)}
          subtitle={`${analytics.paidOrders || 0} paid orders`}
          icon={DollarSign}
          variant="success"
        />
        <KpiCard
          title="Conversion Rate"
          value={`${(analytics.conversionRate || 0).toFixed(1)}%`}
          subtitle={`${analytics.convertedConversations || 0} of ${analytics.totalConversations || 0} conversations`}
          icon={TrendingUp}
          variant={analytics.conversionRate >= 10 ? 'success' : 'warning'}
        />
        <KpiCard
          title="Active Conversations"
          value={analytics.activeConversations || 0}
          subtitle={`${analytics.handoffConversations || 0} need attention`}
          icon={MessageSquare}
          variant={analytics.handoffConversations > 0 ? 'warning' : 'default'}
        />
        <KpiCard
          title="Average Order Value"
          value={formatCurrency(analytics.averageOrderValue || 0)}
          subtitle={`${analytics.totalOrders || 0} total orders`}
          icon={ShoppingCart}
          variant="default"
        />
      </div>

      {/* Charts Row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue Chart */}
        <Card>
          <CardHeader>
            <CardTitle>Revenue Over Time</CardTitle>
          </CardHeader>
          <CardContent>
            {revenueChartData.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <AreaChart data={revenueChartData}>
                  <defs>
                    <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#10b981" stopOpacity={0.2} />
                      <stop offset="95%" stopColor="#10b981" stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                  <XAxis dataKey="date" stroke="#6b7280" fontSize={12} />
                  <YAxis stroke="#6b7280" fontSize={12} tickFormatter={(v) => `₦${v / 1000}k`} />
                  <Tooltip
                    formatter={(value: number) => [formatCurrency(value), 'Revenue']}
                    contentStyle={{ backgroundColor: '#fff', border: '1px solid #e5e7eb' }}
                  />
                  <Area
                    type="monotone"
                    dataKey="revenue"
                    stroke="#10b981"
                    strokeWidth={2}
                    fill="url(#colorRevenue)"
                  />
                </AreaChart>
              </ResponsiveContainer>
            ) : (
              <div className="h-[300px] flex items-center justify-center text-gray-500">
                No revenue data for this period
              </div>
            )}
          </CardContent>
        </Card>

        {/* Orders Chart */}
        <Card>
          <CardHeader>
            <CardTitle>Orders Over Time</CardTitle>
          </CardHeader>
          <CardContent>
            {ordersChartData.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={ordersChartData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                  <XAxis dataKey="date" stroke="#6b7280" fontSize={12} />
                  <YAxis stroke="#6b7280" fontSize={12} />
                  <Tooltip
                    formatter={(value: number) => [value, 'Orders']}
                    contentStyle={{ backgroundColor: '#fff', border: '1px solid #e5e7eb' }}
                  />
                  <Bar dataKey="orders" fill="#6366f1" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="h-[300px] flex items-center justify-center text-gray-500">
                No order data for this period
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      {/* Charts Row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Conversations Chart */}
        <Card>
          <CardHeader>
            <CardTitle>Conversations Over Time</CardTitle>
          </CardHeader>
          <CardContent>
            {conversationChartData.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={conversationChartData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                  <XAxis dataKey="date" stroke="#6b7280" fontSize={12} />
                  <YAxis stroke="#6b7280" fontSize={12} />
                  <Tooltip
                    formatter={(value: number) => [value, 'Conversations']}
                    contentStyle={{ backgroundColor: '#fff', border: '1px solid #e5e7eb' }}
                  />
                  <Line
                    type="monotone"
                    dataKey="conversations"
                    stroke="#8b5cf6"
                    strokeWidth={2}
                    dot={{ fill: '#8b5cf6', strokeWidth: 2 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            ) : (
              <div className="h-[300px] flex items-center justify-center text-gray-500">
                No conversation data for this period
              </div>
            )}
          </CardContent>
        </Card>

        {/* Order Status Breakdown */}
        <Card>
          <CardHeader>
            <CardTitle>Order Status Breakdown</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <StatusRow
                label="Pending Payment"
                count={analytics.pendingOrders || 0}
                total={analytics.totalOrders || 1}
                color="bg-amber-500"
              />
              <StatusRow
                label="Paid"
                count={analytics.paidOrders || 0}
                total={analytics.totalOrders || 1}
                color="bg-blue-500"
              />
              <StatusRow
                label="Shipped"
                count={analytics.shippedOrders || 0}
                total={analytics.totalOrders || 1}
                color="bg-purple-500"
              />
              <StatusRow
                label="Delivered"
                count={analytics.deliveredOrders || 0}
                total={analytics.totalOrders || 1}
                color="bg-green-500"
              />
              <StatusRow
                label="Cancelled"
                count={analytics.cancelledOrders || 0}
                total={analytics.totalOrders || 1}
                color="bg-red-500"
              />
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Bottom Row - Summary Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Conversation Stats */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <MessageSquare className="w-5 h-5" />
              Conversation Metrics
            </CardTitle>
          </CardHeader>
          <CardContent>
            <dl className="space-y-3">
              <StatItem label="Total Conversations" value={analytics.totalConversations || 0} />
              <StatItem label="Active" value={analytics.activeConversations || 0} />
              <StatItem label="Converted" value={analytics.convertedConversations || 0} variant="success" />
              <StatItem label="Abandoned" value={analytics.abandonedConversations || 0} variant="warning" />
              <StatItem label="Handed Off" value={analytics.handoffConversations || 0} variant="danger" />
            </dl>
          </CardContent>
        </Card>

        {/* Message Stats */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Users className="w-5 h-5" />
              Message Metrics
            </CardTitle>
          </CardHeader>
          <CardContent>
            <dl className="space-y-3">
              <StatItem label="Total Messages" value={analytics.totalMessages || 0} />
              <StatItem label="Inbound (Customer)" value={analytics.inboundMessages || 0} />
              <StatItem label="Outbound (AI/Agent)" value={analytics.outboundMessages || 0} />
              <StatItem
                label="Response Ratio"
                value={analytics.inboundMessages > 0
                  ? `${((analytics.outboundMessages / analytics.inboundMessages) * 100).toFixed(0)}%`
                  : 'N/A'
                }
              />
            </dl>
          </CardContent>
        </Card>

        {/* Product Stats */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Package className="w-5 h-5" />
              Product Metrics
            </CardTitle>
          </CardHeader>
          <CardContent>
            <dl className="space-y-3">
              <StatItem label="Total Products" value={analytics.totalProducts || 0} />
              <StatItem label="Active Products" value={analytics.activeProducts || 0} variant="success" />
              <StatItem label="Out of Stock" value={analytics.outOfStockProducts || 0} variant="danger" />
              {analytics.outOfStockProducts > 0 && (
                <div className="pt-2 border-t">
                  <div className="flex items-center gap-2 text-amber-600 text-sm">
                    <AlertTriangle className="w-4 h-4" />
                    <span>Some products need restocking</span>
                  </div>
                </div>
              )}
            </dl>
          </CardContent>
        </Card>
      </div>

      {/* Top Products */}
      {analytics.topProducts && analytics.topProducts.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>Top Selling Products</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Rank</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Product</th>
                    <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Orders</th>
                    <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Revenue</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {analytics.topProducts.map((product, index) => (
                    <tr key={product.productName} className="hover:bg-gray-50">
                      <td className="px-4 py-3">
                        <Badge variant={index === 0 ? 'success' : index === 1 ? 'warning' : 'default'}>
                          #{index + 1}
                        </Badge>
                      </td>
                      <td className="px-4 py-3 font-medium text-gray-900">{product.productName}</td>
                      <td className="px-4 py-3 text-right text-gray-600">{product.orderCount}</td>
                      <td className="px-4 py-3 text-right font-medium text-gray-900">
                        {formatCurrency(product.revenue)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}

interface StatusRowProps {
  label: string;
  count: number;
  total: number;
  color: string;
}

function StatusRow({ label, count, total, color }: StatusRowProps) {
  const percentage = total > 0 ? (count / total) * 100 : 0;

  return (
    <div>
      <div className="flex justify-between text-sm mb-1">
        <span className="text-gray-600">{label}</span>
        <span className="font-medium text-gray-900">{count}</span>
      </div>
      <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
        <div
          className={cn('h-full rounded-full', color)}
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  );
}

interface StatItemProps {
  label: string;
  value: number | string;
  variant?: 'default' | 'success' | 'warning' | 'danger';
}

function StatItem({ label, value, variant = 'default' }: StatItemProps) {
  const colorClasses = {
    default: 'text-gray-900',
    success: 'text-green-600',
    warning: 'text-amber-600',
    danger: 'text-red-600',
  };

  return (
    <div className="flex justify-between items-center">
      <dt className="text-sm text-gray-500">{label}</dt>
      <dd className={cn('text-sm font-semibold', colorClasses[variant])}>{value}</dd>
    </div>
  );
}
