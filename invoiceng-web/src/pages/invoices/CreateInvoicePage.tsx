import { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { ArrowLeft, Plus, Trash2, Calendar } from 'lucide-react';
import { invoicesApi } from '@/api/invoices';
import { customersApi } from '@/api/customers';
import { Button, Input, Card, Select } from '@/components/ui';
import { formatCurrency } from '@/utils/format';
import { CustomerFormModal } from '../customers/CustomerFormModal';
import toast from 'react-hot-toast';

const invoiceItemSchema = z.object({
  name: z.string().min(1, 'Item name is required'),
  quantity: z.number().min(1, 'Quantity must be at least 1'),
  price: z.number().min(0.01, 'Price must be greater than 0'),
});

const invoiceSchema = z.object({
  customerId: z.string().min(1, 'Please select a customer'),
  dueDate: z.string().min(1, 'Due date is required'),
  items: z.array(invoiceItemSchema).min(1, 'Add at least one item'),
  notes: z.string().optional(),
});

type InvoiceFormData = z.infer<typeof invoiceSchema>;

export function CreateInvoicePage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const queryClient = useQueryClient();
  const preselectedCustomerId = searchParams.get('customerId');

  const [showCustomerModal, setShowCustomerModal] = useState(false);

  const { data: customers } = useQuery({
    queryKey: ['customers'],
    queryFn: () => customersApi.getAll(),
  });

  const nextWeek = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];

  const {
    register,
    control,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<InvoiceFormData>({
    resolver: zodResolver(invoiceSchema),
    defaultValues: {
      customerId: preselectedCustomerId || '',
      dueDate: nextWeek,
      items: [{ name: '', quantity: 1, price: 0 }],
      notes: '',
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'items',
  });

  const watchItems = watch('items');

  const total = watchItems.reduce((sum, item) => sum + (item.quantity * item.price), 0);

  const createMutation = useMutation({
    mutationFn: invoicesApi.create,
    onSuccess: (invoice) => {
      queryClient.invalidateQueries({ queryKey: ['invoices'] });
      toast.success('Invoice created successfully');
      navigate(`/invoices/${invoice.id}`);
    },
    onError: (error: any) => {
      console.error('Invoice creation error:', error);
      const message = error.response?.data?.message || error.message || 'Failed to create invoice';
      toast.error(message);
    },
  });

  const onSubmit = (data: InvoiceFormData) => {
    createMutation.mutate({
      customerId: data.customerId,
      dueDate: data.dueDate,
      items: data.items.map((item) => ({
        name: item.name,
        quantity: item.quantity,
        price: item.price,
      })),
      notes: data.notes || undefined,
    });
  };

  const customerOptions = [
    { value: '', label: 'Select a customer' },
    ...(customers?.map((c) => ({ value: c.id, label: c.name })) || []),
  ];

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center gap-4">
        <button
          onClick={() => navigate(-1)}
          className="p-2 rounded-lg hover:bg-gray-100"
        >
          <ArrowLeft className="h-5 w-5" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Create Invoice</h1>
          <p className="text-gray-600 mt-1">Create a new invoice for your customer</p>
        </div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        {/* Customer & Dates */}
        <Card className="p-6 space-y-4">
          <div className="flex items-end gap-4">
            <div className="flex-1">
              <Select
                label="Customer"
                options={customerOptions}
                error={errors.customerId?.message}
                {...register('customerId')}
              />
            </div>
            <Button
              type="button"
              variant="outline"
              onClick={() => setShowCustomerModal(true)}
            >
              <Plus className="h-4 w-4" />
            </Button>
          </div>

          <Input
            type="date"
            label="Due Date"
            leftIcon={<Calendar className="h-5 w-5" />}
            error={errors.dueDate?.message}
            {...register('dueDate')}
          />
        </Card>

        {/* Items */}
        <Card className="p-6">
          <h3 className="font-semibold text-gray-900 mb-4">Invoice Items</h3>

          <div className="space-y-4">
            {fields.map((field, index) => (
              <div key={field.id} className="space-y-3 pb-4 border-b border-gray-100 last:border-0">
                <div className="flex items-start gap-2">
                  <div className="flex-1">
                    <Input
                      placeholder="Item name"
                      error={errors.items?.[index]?.name?.message}
                      {...register(`items.${index}.name`)}
                    />
                  </div>
                  {fields.length > 1 && (
                    <button
                      type="button"
                      onClick={() => remove(index)}
                      className="p-2 text-red-500 hover:bg-red-50 rounded-lg"
                    >
                      <Trash2 className="h-5 w-5" />
                    </button>
                  )}
                </div>

                <div className="grid grid-cols-3 gap-3">
                  <Input
                    type="number"
                    label="Qty"
                    min={1}
                    error={errors.items?.[index]?.quantity?.message}
                    {...register(`items.${index}.quantity`, { valueAsNumber: true })}
                  />
                  <Input
                    type="number"
                    label="Unit Price (₦)"
                    min={0}
                    step="any"
                    error={errors.items?.[index]?.price?.message}
                    {...register(`items.${index}.price`, { valueAsNumber: true })}
                  />
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Amount
                    </label>
                    <div className="h-10 px-3 flex items-center bg-gray-50 rounded-lg border border-gray-200 text-gray-700 font-medium">
                      {formatCurrency(watchItems[index]?.quantity * watchItems[index]?.price || 0)}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={() => append({ name: '', quantity: 1, price: 0 })}
            className="mt-4"
            leftIcon={<Plus className="h-4 w-4" />}
          >
            Add Item
          </Button>

          {errors.items?.message && (
            <p className="text-sm text-red-600 mt-2">{errors.items.message}</p>
          )}
        </Card>

        {/* Notes & Total */}
        <Card className="p-6 space-y-4">
          <Input
            label="Notes (Optional)"
            placeholder="Additional notes for the customer..."
            {...register('notes')}
          />

          <div className="flex justify-between items-center pt-4 border-t border-gray-200">
            <span className="text-lg font-semibold text-gray-900">Total</span>
            <span className="text-2xl font-bold text-primary-600">
              {formatCurrency(total)}
            </span>
          </div>
        </Card>

        {/* Actions */}
        <div className="flex gap-3">
          <Button
            type="button"
            variant="outline"
            onClick={() => navigate(-1)}
            className="flex-1"
          >
            Cancel
          </Button>
          <Button
            type="submit"
            loading={createMutation.isPending}
            className="flex-1"
          >
            Create Invoice
          </Button>
        </div>
      </form>

      <CustomerFormModal
        open={showCustomerModal}
        onClose={() => setShowCustomerModal(false)}
      />
    </div>
  );
}
