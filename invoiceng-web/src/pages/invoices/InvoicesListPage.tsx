import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { Plus, Search, FileText } from 'lucide-react';
import { invoicesApi } from '@/api/invoices';
import { Button, Input, Card, Badge, Select, EmptyState } from '@/components/ui';
import { formatCurrency, formatDate } from '@/utils/format';

const statusOptions = [
  { value: '', label: 'All Status' },
  { value: 'DRAFT', label: 'Draft' },
  { value: 'PENDING', label: 'Pending' },
  { value: 'PAID', label: 'Paid' },
  { value: 'PARTIALLY_PAID', label: 'Partially Paid' },
  { value: 'OVERDUE', label: 'Overdue' },
  { value: 'CANCELLED', label: 'Cancelled' },
];

export function InvoicesListPage() {
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  const { data: invoices, isLoading } = useQuery({
    queryKey: ['invoices'],
    queryFn: invoicesApi.getAll,
  });

  const filteredInvoices = invoices?.filter((invoice) => {
    const matchesSearch =
      invoice.invoiceNumber.toLowerCase().includes(search.toLowerCase()) ||
      invoice.customer?.name.toLowerCase().includes(search.toLowerCase());
    const matchesStatus = !statusFilter || invoice.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

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
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Invoices</h1>
          <p className="text-gray-600 mt-1">Manage your invoices</p>
        </div>
        <Link to="/invoices/new">
          <Button leftIcon={<Plus className="h-4 w-4" />}>
            New Invoice
          </Button>
        </Link>
      </div>

      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="flex-1 max-w-md">
          <Input
            placeholder="Search invoices..."
            leftIcon={<Search className="h-5 w-5" />}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <div className="w-full sm:w-48">
          <Select
            options={statusOptions}
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          />
        </div>
      </div>

      {/* List */}
      {isLoading ? (
        <div className="space-y-4">
          {[...Array(5)].map((_, i) => (
            <Card key={i} className="p-4 animate-pulse">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-gray-200 rounded-full" />
                  <div>
                    <div className="h-4 bg-gray-200 rounded w-24 mb-2" />
                    <div className="h-3 bg-gray-200 rounded w-32" />
                  </div>
                </div>
                <div className="text-right">
                  <div className="h-4 bg-gray-200 rounded w-20 mb-2" />
                  <div className="h-5 bg-gray-200 rounded w-16" />
                </div>
              </div>
            </Card>
          ))}
        </div>
      ) : filteredInvoices && filteredInvoices.length > 0 ? (
        <div className="space-y-3">
          {filteredInvoices.map((invoice) => (
            <Link key={invoice.id} to={`/invoices/${invoice.id}`}>
              <Card className="p-4 hover:shadow-md transition-shadow">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3 min-w-0">
                    <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center flex-shrink-0">
                      <FileText className="h-5 w-5 text-gray-600" />
                    </div>
                    <div className="min-w-0">
                      <p className="font-medium text-gray-900">{invoice.invoiceNumber}</p>
                      <p className="text-sm text-gray-500 truncate">
                        {invoice.customer?.name || 'Unknown Customer'}
                      </p>
                    </div>
                  </div>
                  <div className="text-right flex-shrink-0 ml-4">
                    <p className="font-semibold text-gray-900">
                      {formatCurrency(invoice.total)}
                    </p>
                    <div className="flex items-center gap-2 justify-end mt-1">
                      <span className="text-xs text-gray-500">
                        {formatDate(invoice.issueDate)}
                      </span>
                      {getStatusBadge(invoice.status)}
                    </div>
                  </div>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      ) : (
        <EmptyState
          icon={<FileText className="h-12 w-12" />}
          title="No invoices yet"
          description="Create your first invoice to start collecting payments"
          action={
            <Link to="/invoices/new">
              <Button>Create Invoice</Button>
            </Link>
          }
        />
      )}
    </div>
  );
}
