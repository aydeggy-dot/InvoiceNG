import { useEffect } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Building2, CreditCard, User } from 'lucide-react';
import { usersApi } from '@/api/users';
import { useAuthStore } from '@/stores/authStore';
import { Button, Input, Card, Select } from '@/components/ui';
import { formatPhone } from '@/utils/format';
import toast from 'react-hot-toast';

const NIGERIAN_BANKS = [
  { value: '', label: 'Select Bank' },
  { value: '044', label: 'Access Bank' },
  { value: '023', label: 'Citibank Nigeria' },
  { value: '050', label: 'Ecobank Nigeria' },
  { value: '070', label: 'Fidelity Bank' },
  { value: '011', label: 'First Bank of Nigeria' },
  { value: '214', label: 'First City Monument Bank' },
  { value: '058', label: 'Guaranty Trust Bank' },
  { value: '030', label: 'Heritage Bank' },
  { value: '301', label: 'Jaiz Bank' },
  { value: '082', label: 'Keystone Bank' },
  { value: '526', label: 'Parallex Bank' },
  { value: '076', label: 'Polaris Bank' },
  { value: '101', label: 'Providus Bank' },
  { value: '221', label: 'Stanbic IBTC Bank' },
  { value: '068', label: 'Standard Chartered Bank' },
  { value: '232', label: 'Sterling Bank' },
  { value: '100', label: 'Suntrust Bank' },
  { value: '032', label: 'Union Bank of Nigeria' },
  { value: '033', label: 'United Bank for Africa' },
  { value: '215', label: 'Unity Bank' },
  { value: '035', label: 'Wema Bank' },
  { value: '057', label: 'Zenith Bank' },
  { value: '999991', label: 'OPay' },
  { value: '999992', label: 'Kuda Bank' },
  { value: '999993', label: 'PalmPay' },
  { value: '999994', label: 'Moniepoint' },
];

const settingsSchema = z.object({
  email: z.string().email('Enter a valid email').optional().or(z.literal('')),
  businessName: z.string().min(2, 'Business name is required'),
  businessAddress: z.string().optional(),
  bankCode: z.string().optional(),
  accountNumber: z
    .string()
    .regex(/^\d{10}$/, 'Account number must be 10 digits')
    .optional()
    .or(z.literal('')),
  accountName: z.string().optional(),
});

type SettingsFormData = z.infer<typeof settingsSchema>;

export function SettingsPage() {
  const queryClient = useQueryClient();
  const { user, setUser } = useAuthStore();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isDirty },
  } = useForm<SettingsFormData>({
    resolver: zodResolver(settingsSchema),
    defaultValues: {
      email: '',
      businessName: '',
      businessAddress: '',
      bankCode: '',
      accountNumber: '',
      accountName: '',
    },
  });

  useEffect(() => {
    if (user) {
      reset({
        email: user.email || '',
        businessName: user.businessName || '',
        businessAddress: user.businessAddress || '',
        bankCode: user.bankCode || '',
        accountNumber: user.accountNumber || '',
        accountName: user.accountName || '',
      });
    }
  }, [user, reset]);

  const updateMutation = useMutation({
    mutationFn: usersApi.updateMe,
    onSuccess: (updatedUser) => {
      setUser(updatedUser);
      queryClient.invalidateQueries({ queryKey: ['user'] });
      toast.success('Settings saved');
    },
    onError: () => {
      toast.error('Failed to save settings');
    },
  });

  const onSubmit = (data: SettingsFormData) => {
    const selectedBank = NIGERIAN_BANKS.find((b) => b.value === data.bankCode);
    updateMutation.mutate({
      email: data.email || undefined,
      businessName: data.businessName,
      businessAddress: data.businessAddress || undefined,
      bankCode: data.bankCode || undefined,
      bankName: selectedBank?.label || undefined,
      accountNumber: data.accountNumber || undefined,
      accountName: data.accountName || undefined,
    });
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Settings</h1>
        <p className="text-gray-600 mt-1">Manage your account and business settings</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        {/* Account Info */}
        <Card className="p-6">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center">
              <User className="h-5 w-5 text-primary-600" />
            </div>
            <div>
              <h2 className="font-semibold text-gray-900">Account</h2>
              <p className="text-sm text-gray-500">Your account information</p>
            </div>
          </div>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Phone Number
              </label>
              <div className="px-3 py-2 bg-gray-50 rounded-lg border border-gray-200 text-gray-600">
                {user?.phone ? formatPhone(user.phone) : 'Not set'}
              </div>
              <p className="text-xs text-gray-500 mt-1">
                Phone number cannot be changed
              </p>
            </div>

            <Input
              label="Email Address"
              type="email"
              placeholder="you@example.com"
              error={errors.email?.message}
              {...register('email')}
            />
          </div>
        </Card>

        {/* Business Info */}
        <Card className="p-6">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
              <Building2 className="h-5 w-5 text-blue-600" />
            </div>
            <div>
              <h2 className="font-semibold text-gray-900">Business</h2>
              <p className="text-sm text-gray-500">Your business details</p>
            </div>
          </div>

          <div className="space-y-4">
            <Input
              label="Business Name"
              placeholder="Your Business Name"
              error={errors.businessName?.message}
              {...register('businessName')}
            />

            <Input
              label="Business Address"
              placeholder="123 Main Street, Lagos"
              error={errors.businessAddress?.message}
              {...register('businessAddress')}
            />
          </div>
        </Card>

        {/* Bank Details */}
        <Card className="p-6">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
              <CreditCard className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <h2 className="font-semibold text-gray-900">Bank Account</h2>
              <p className="text-sm text-gray-500">Your bank details for receiving payments</p>
            </div>
          </div>

          <div className="space-y-4">
            <Select
              label="Bank"
              options={NIGERIAN_BANKS}
              error={errors.bankCode?.message}
              {...register('bankCode')}
            />

            <Input
              label="Account Number"
              placeholder="0123456789"
              maxLength={10}
              error={errors.accountNumber?.message}
              {...register('accountNumber')}
            />

            <Input
              label="Account Name"
              placeholder="Account Holder Name"
              error={errors.accountName?.message}
              {...register('accountName')}
            />
          </div>
        </Card>

        <Button
          type="submit"
          className="w-full"
          size="lg"
          loading={updateMutation.isPending}
          disabled={!isDirty}
        >
          Save Settings
        </Button>
      </form>
    </div>
  );
}
