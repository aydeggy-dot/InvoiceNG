import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Phone } from 'lucide-react';
import { authApi } from '@/api/auth';
import { Button, Input, Card } from '@/components/ui';
import toast from 'react-hot-toast';

const phoneSchema = z.object({
  phone: z
    .string()
    .min(11, 'Phone number must be at least 11 digits')
    .max(14, 'Phone number is too long')
    .regex(/^(0|\+?234)[789][01]\d{8}$/, 'Enter a valid Nigerian phone number'),
});

type PhoneFormData = z.infer<typeof phoneSchema>;

export function LoginPage() {
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<PhoneFormData>({
    resolver: zodResolver(phoneSchema),
    defaultValues: {
      phone: '',
    },
  });

  const requestOtpMutation = useMutation({
    mutationFn: (phone: string) => authApi.requestOtp(formatPhoneNumber(phone)),
    onSuccess: (_, phone) => {
      const formatted = formatPhoneNumber(phone);
      toast.success('OTP sent to your phone');
      navigate('/verify-otp', { state: { phone: formatted } });
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to send OTP. Please try again.';
      toast.error(message);
    },
  });

  const formatPhoneNumber = (phone: string): string => {
    // Remove spaces and dashes
    let cleaned = phone.replace(/[\s-]/g, '');

    // Convert 0xxx to 234xxx
    if (cleaned.startsWith('0')) {
      cleaned = '234' + cleaned.slice(1);
    }

    // Remove + if present
    if (cleaned.startsWith('+')) {
      cleaned = cleaned.slice(1);
    }

    return cleaned;
  };

  const onSubmit = (data: PhoneFormData) => {
    requestOtpMutation.mutate(data.phone);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 to-primary-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Logo */}
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-primary-600 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <span className="text-white font-bold text-2xl">IN</span>
          </div>
          <h1 className="text-2xl font-bold text-gray-900">InvoiceNG</h1>
          <p className="text-gray-600 mt-1">Invoice & Payment Collection for Nigerian SMEs</p>
        </div>

        <Card className="p-6">
          <div className="text-center mb-6">
            <h2 className="text-xl font-semibold text-gray-900">Welcome</h2>
            <p className="text-gray-600 text-sm mt-1">
              Enter your phone number to get started
            </p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              label="Phone Number"
              placeholder="08012345678"
              leftIcon={<Phone className="h-5 w-5" />}
              error={errors.phone?.message}
              {...register('phone')}
            />

            <p className="text-xs text-gray-500">
              We'll send you a one-time password (OTP) to verify your number
            </p>

            <Button
              type="submit"
              className="w-full"
              size="lg"
              loading={requestOtpMutation.isPending}
            >
              Continue
            </Button>
          </form>
        </Card>

        <p className="text-center text-xs text-gray-500 mt-6">
          By continuing, you agree to our Terms of Service and Privacy Policy
        </p>
      </div>
    </div>
  );
}
