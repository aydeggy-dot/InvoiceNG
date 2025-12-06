import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { Plus, Search, Phone, Mail, MoreVertical, Edit, Trash2, FileText } from 'lucide-react';
import { customersApi } from '@/api/customers';
import { Button, Input, Card, Modal, EmptyState } from '@/components/ui';
import { formatPhone } from '@/utils/format';
import { CustomerFormModal } from './CustomerFormModal';
import type { Customer } from '@/types';
import toast from 'react-hot-toast';

export function CustomersListPage() {
  const queryClient = useQueryClient();
  const [search, setSearch] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState<Customer | null>(null);
  const [deletingCustomer, setDeletingCustomer] = useState<Customer | null>(null);
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);

  const { data: customers, isLoading } = useQuery({
    queryKey: ['customers'],
    queryFn: () => customersApi.getAll(),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => customersApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['customers'] });
      toast.success('Customer deleted');
      setDeletingCustomer(null);
    },
    onError: () => {
      toast.error('Failed to delete customer');
    },
  });

  const filteredCustomers = customers?.filter(
    (c) =>
      c.name.toLowerCase().includes(search.toLowerCase()) ||
      c.phone.includes(search) ||
      c.email?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Customers</h1>
          <p className="text-gray-600 mt-1">Manage your customer list</p>
        </div>
        <Button
          leftIcon={<Plus className="h-4 w-4" />}
          onClick={() => setShowCreateModal(true)}
        >
          Add Customer
        </Button>
      </div>

      {/* Search */}
      <div className="max-w-md">
        <Input
          placeholder="Search customers..."
          leftIcon={<Search className="h-5 w-5" />}
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {/* List */}
      {isLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {[...Array(6)].map((_, i) => (
            <Card key={i} className="p-4 animate-pulse">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-gray-200 rounded-full" />
                <div className="flex-1">
                  <div className="h-4 bg-gray-200 rounded w-3/4 mb-2" />
                  <div className="h-3 bg-gray-200 rounded w-1/2" />
                </div>
              </div>
            </Card>
          ))}
        </div>
      ) : filteredCustomers && filteredCustomers.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredCustomers.map((customer) => (
            <Card key={customer.id} className="p-4 relative">
              <div className="flex items-start gap-3">
                <div className="w-12 h-12 bg-primary-100 rounded-full flex items-center justify-center flex-shrink-0">
                  <span className="text-primary-700 font-semibold text-lg">
                    {customer.name.charAt(0).toUpperCase()}
                  </span>
                </div>
                <div className="flex-1 min-w-0">
                  <h3 className="font-medium text-gray-900 truncate">{customer.name}</h3>
                  <div className="flex items-center gap-1 text-sm text-gray-500 mt-1">
                    <Phone className="h-3.5 w-3.5" />
                    <span className="truncate">{formatPhone(customer.phone)}</span>
                  </div>
                  {customer.email && (
                    <div className="flex items-center gap-1 text-sm text-gray-500 mt-0.5">
                      <Mail className="h-3.5 w-3.5" />
                      <span className="truncate">{customer.email}</span>
                    </div>
                  )}
                </div>

                {/* Menu */}
                <div className="relative">
                  <button
                    onClick={() => setOpenMenuId(openMenuId === customer.id ? null : customer.id)}
                    className="p-1 rounded hover:bg-gray-100"
                  >
                    <MoreVertical className="h-5 w-5 text-gray-400" />
                  </button>

                  {openMenuId === customer.id && (
                    <>
                      <div
                        className="fixed inset-0 z-10"
                        onClick={() => setOpenMenuId(null)}
                      />
                      <div className="absolute right-0 mt-1 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-20">
                        <Link
                          to={`/invoices/new?customerId=${customer.id}`}
                          className="flex items-center gap-2 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                          onClick={() => setOpenMenuId(null)}
                        >
                          <FileText className="h-4 w-4" />
                          Create Invoice
                        </Link>
                        <button
                          onClick={() => {
                            setEditingCustomer(customer);
                            setOpenMenuId(null);
                          }}
                          className="flex items-center gap-2 w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                        >
                          <Edit className="h-4 w-4" />
                          Edit
                        </button>
                        <button
                          onClick={() => {
                            setDeletingCustomer(customer);
                            setOpenMenuId(null);
                          }}
                          className="flex items-center gap-2 w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                        >
                          <Trash2 className="h-4 w-4" />
                          Delete
                        </button>
                      </div>
                    </>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      ) : (
        <EmptyState
          icon={<Plus className="h-12 w-12" />}
          title="No customers yet"
          description="Add your first customer to get started with invoicing"
          action={
            <Button onClick={() => setShowCreateModal(true)}>
              Add Customer
            </Button>
          }
        />
      )}

      {/* Create/Edit Modal */}
      <CustomerFormModal
        open={showCreateModal || !!editingCustomer}
        onClose={() => {
          setShowCreateModal(false);
          setEditingCustomer(null);
        }}
        customer={editingCustomer}
      />

      {/* Delete Confirmation Modal */}
      <Modal
        open={!!deletingCustomer}
        onClose={() => setDeletingCustomer(null)}
        title="Delete Customer"
      >
        <p className="text-gray-600">
          Are you sure you want to delete <strong>{deletingCustomer?.name}</strong>?
          This action cannot be undone.
        </p>
        <div className="flex justify-end gap-3 mt-6">
          <Button variant="outline" onClick={() => setDeletingCustomer(null)}>
            Cancel
          </Button>
          <Button
            variant="danger"
            loading={deleteMutation.isPending}
            onClick={() => deletingCustomer && deleteMutation.mutate(deletingCustomer.id)}
          >
            Delete
          </Button>
        </div>
      </Modal>
    </div>
  );
}
