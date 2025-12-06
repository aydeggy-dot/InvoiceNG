import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import {
  FileText,
  Users,
  TrendingUp,
  Clock,
  Plus,
  ArrowRight,
  AlertCircle
} from 'lucide-react';
import { dashboardApi } from '@/api/dashboard';
import { Card, Button, Badge, Loading } from '@/components/ui';
import { formatCurrency } from '@/utils/format';

export function DashboardPage() {
  const { data: stats, isLoading: statsLoading } = useQuery({
    queryKey: ['dashboard', 'stats'],
    queryFn: dashboardApi.getStats,
  });

  const { data: recentInvoices, isLoading: invoicesLoading } = useQuery({
    queryKey: ['dashboard', 'recent-invoices'],
    queryFn: () => dashboardApi.getRecentInvoices(5),
  });

  if (statsLoading) {
    return <Loading />;
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600 mt-1">Overview of your business</p>
        </div>
        <Link to="/invoices/new">
          <Button leftIcon={<Plus className="h-4 w-4" />}>
            New Invoice
          </Button>
        </Link>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="p-5">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Revenue</p>
              <p className="text-2xl font-bold text-gray-900 mt-1">
                {formatCurrency(stats?.overview?.totalRevenue || 0)}
              </p>
            </div>
            <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
              <TrendingUp className="h-6 w-6 text-green-600" />
            </div>
          </div>
        </Card>

        <Card className="p-5">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Pending Amount</p>
              <p className="text-2xl font-bold text-gray-900 mt-1">
                {formatCurrency(stats?.overview?.pendingAmount || 0)}
              </p>
            </div>
            <div className="w-12 h-12 bg-yellow-100 rounded-full flex items-center justify-center">
              <Clock className="h-6 w-6 text-yellow-600" />
            </div>
          </div>
        </Card>

        <Card className="p-5">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Invoices</p>
              <p className="text-2xl font-bold text-gray-900 mt-1">
                {stats?.overview?.totalInvoices || 0}
              </p>
            </div>
            <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
              <FileText className="h-6 w-6 text-blue-600" />
            </div>
          </div>
        </Card>

        <Card className="p-5">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Customers</p>
              <p className="text-2xl font-bold text-gray-900 mt-1">
                {stats?.overview?.totalCustomers || 0}
              </p>
            </div>
            <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center">
              <Users className="h-6 w-6 text-purple-600" />
            </div>
          </div>
        </Card>
      </div>

      {/* Invoice Stats */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <Card className="p-4 text-center">
          <p className="text-2xl font-bold text-gray-900">{stats?.overview?.paidInvoices || 0}</p>
          <p className="text-sm text-gray-600">Paid</p>
        </Card>
        <Card className="p-4 text-center">
          <p className="text-2xl font-bold text-yellow-600">{stats?.overview?.pendingInvoices || 0}</p>
          <p className="text-sm text-gray-600">Pending</p>
        </Card>
        <Card className="p-4 text-center">
          <p className="text-2xl font-bold text-red-600">{stats?.overview?.overdueInvoices || 0}</p>
          <p className="text-sm text-gray-600">Overdue</p>
        </Card>
        <Card className="p-4 text-center">
          <p className="text-2xl font-bold text-gray-400">{stats?.overview?.draftInvoices || 0}</p>
          <p className="text-sm text-gray-600">Draft</p>
        </Card>
      </div>

      {/* Recent Invoices */}
      <Card>
        <div className="p-4 border-b border-gray-200 flex items-center justify-between">
          <h2 className="font-semibold text-gray-900">Recent Invoices</h2>
          <Link
            to="/invoices"
            className="text-sm text-primary-600 hover:text-primary-700 flex items-center gap-1"
          >
            View all <ArrowRight className="h-4 w-4" />
          </Link>
        </div>

        {invoicesLoading ? (
          <div className="p-8">
            <Loading />
          </div>
        ) : recentInvoices && recentInvoices.length > 0 ? (
          <div className="divide-y divide-gray-100">
            {recentInvoices.map((invoice) => (
              <Link
                key={invoice.id}
                to={`/invoices/${invoice.id}`}
                className="flex items-center justify-between p-4 hover:bg-gray-50 transition-colors"
              >
                <div className="flex items-center gap-3 min-w-0">
                  <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center flex-shrink-0">
                    <FileText className="h-5 w-5 text-gray-600" />
                  </div>
                  <div className="min-w-0">
                    <p className="font-medium text-gray-900 truncate">
                      {invoice.invoiceNumber}
                    </p>
                    <p className="text-sm text-gray-500 truncate">
                      {invoice.customer?.name || 'Unknown Customer'}
                    </p>
                  </div>
                </div>
                <div className="text-right flex-shrink-0 ml-4">
                  <p className="font-medium text-gray-900">
                    {formatCurrency(invoice.total)}
                  </p>
                  <StatusBadge status={invoice.status} />
                </div>
              </Link>
            ))}
          </div>
        ) : (
          <div className="p-8 text-center">
            <AlertCircle className="h-12 w-12 text-gray-300 mx-auto mb-3" />
            <p className="text-gray-600">No invoices yet</p>
            <Link to="/invoices/new">
              <Button variant="outline" size="sm" className="mt-3">
                Create your first invoice
              </Button>
            </Link>
          </div>
        )}
      </Card>
    </div>
  );
}

function StatusBadge({ status }: { status: string }) {
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
    PARTIALLY_PAID: 'Partial',
    PENDING: 'Pending',
    OVERDUE: 'Overdue',
    DRAFT: 'Draft',
    CANCELLED: 'Cancelled',
  };

  return (
    <Badge variant={variants[status] || 'default'} size="sm">
      {labels[status] || status}
    </Badge>
  );
}
