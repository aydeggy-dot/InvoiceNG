import { useEffect } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { customersApi } from '@/api/customers';
import { Modal, Input, Button } from '@/components/ui';
import type { Customer } from '@/types';
import toast from 'react-hot-toast';

const customerSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters'),
  phone: z
    .string()
    .min(11, 'Phone number must be at least 11 digits')
    .regex(/^(0|\+?234)?[789][01]\d{8}$/, 'Enter a valid Nigerian phone number'),
  email: z.string().email('Enter a valid email').optional().or(z.literal('')),
  address: z.string().optional(),
});

type CustomerFormData = z.infer<typeof customerSchema>;

interface CustomerFormModalProps {
  open: boolean;
  onClose: () => void;
  customer?: Customer | null;
}

export function CustomerFormModal({ open, onClose, customer }: CustomerFormModalProps) {
  const queryClient = useQueryClient();
  const isEditing = !!customer;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CustomerFormData>({
    resolver: zodResolver(customerSchema),
    defaultValues: {
      name: '',
      phone: '',
      email: '',
      address: '',
    },
  });

  useEffect(() => {
    if (customer) {
      reset({
        name: customer.name,
        phone: customer.phone.startsWith('234') ? '0' + customer.phone.slice(3) : customer.phone,
        email: customer.email || '',
        address: customer.address || '',
      });
    } else {
      reset({
        name: '',
        phone: '',
        email: '',
        address: '',
      });
    }
  }, [customer, reset]);

  const createMutation = useMutation({
    mutationFn: customersApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['customers'] });
      toast.success('Customer created');
      handleClose();
    },
    onError: () => {
      toast.error('Failed to create customer');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: CustomerFormData }) =>
      customersApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['customers'] });
      toast.success('Customer updated');
      handleClose();
    },
    onError: () => {
      toast.error('Failed to update customer');
    },
  });

  const handleClose = () => {
    reset();
    onClose();
  };

  const formatPhoneNumber = (phone: string): string => {
    let cleaned = phone.replace(/[\s-]/g, '');
    if (cleaned.startsWith('0')) {
      cleaned = '234' + cleaned.slice(1);
    }
    if (cleaned.startsWith('+')) {
      cleaned = cleaned.slice(1);
    }
    return cleaned;
  };

  const onSubmit = (data: CustomerFormData) => {
    const formattedData = {
      ...data,
      phone: formatPhoneNumber(data.phone),
      email: data.email || undefined,
      address: data.address || undefined,
    };

    if (isEditing && customer) {
      updateMutation.mutate({ id: customer.id, data: formattedData });
    } else {
      createMutation.mutate(formattedData);
    }
  };

  const isLoading = createMutation.isPending || updateMutation.isPending;

  return (
    <Modal
      open={open}
      onClose={handleClose}
      title={isEditing ? 'Edit Customer' : 'Add Customer'}
    >
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="Customer Name"
          placeholder="John Doe"
          error={errors.name?.message}
          {...register('name')}
        />

        <Input
          label="Phone Number"
          placeholder="08012345678"
          error={errors.phone?.message}
          {...register('phone')}
        />

        <Input
          label="Email (Optional)"
          type="email"
          placeholder="customer@email.com"
          error={errors.email?.message}
          {...register('email')}
        />

        <Input
          label="Address (Optional)"
          placeholder="123 Main Street, Lagos"
          error={errors.address?.message}
          {...register('address')}
        />

        <div className="flex justify-end gap-3 pt-4">
          <Button type="button" variant="outline" onClick={handleClose}>
            Cancel
          </Button>
          <Button type="submit" loading={isLoading}>
            {isEditing ? 'Update' : 'Add Customer'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
