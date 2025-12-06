import { useState, useEffect, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { ArrowLeft } from 'lucide-react';
import { authApi } from '@/api/auth';
import { useAuthStore } from '@/stores/authStore';
import { Button, Card } from '@/components/ui';
import toast from 'react-hot-toast';

const OTP_LENGTH = 6;

export function VerifyOtpPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const phone = location.state?.phone;
  const { setAuth } = useAuthStore();

  const [otp, setOtp] = useState<string[]>(Array(OTP_LENGTH).fill(''));
  const [resendTimer, setResendTimer] = useState(60);
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  useEffect(() => {
    if (!phone) {
      navigate('/login');
    }
  }, [phone, navigate]);

  useEffect(() => {
    if (resendTimer > 0) {
      const timer = setInterval(() => {
        setResendTimer((prev) => prev - 1);
      }, 1000);
      return () => clearInterval(timer);
    }
  }, [resendTimer]);

  const verifyOtpMutation = useMutation({
    mutationFn: (otpCode: string) => authApi.verifyOtp(phone, otpCode),
    onSuccess: (data) => {
      setAuth(data.token, data.refreshToken, data.user);
      toast.success('Welcome to InvoiceNG!');
      navigate('/');
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Invalid OTP. Please try again.';
      toast.error(message);
      setOtp(Array(OTP_LENGTH).fill(''));
      inputRefs.current[0]?.focus();
    },
  });

  const resendOtpMutation = useMutation({
    mutationFn: () => authApi.requestOtp(phone),
    onSuccess: () => {
      toast.success('OTP sent again');
      setResendTimer(60);
      setOtp(Array(OTP_LENGTH).fill(''));
      inputRefs.current[0]?.focus();
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to resend OTP';
      toast.error(message);
    },
  });

  const handleChange = (index: number, value: string) => {
    if (!/^\d*$/.test(value)) return;

    const newOtp = [...otp];
    newOtp[index] = value.slice(-1);
    setOtp(newOtp);

    // Auto-focus next input
    if (value && index < OTP_LENGTH - 1) {
      inputRefs.current[index + 1]?.focus();
    }

    // Auto-submit when all digits entered
    if (newOtp.every((digit) => digit) && value) {
      verifyOtpMutation.mutate(newOtp.join(''));
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  const handlePaste = (e: React.ClipboardEvent) => {
    e.preventDefault();
    const pastedData = e.clipboardData.getData('text').slice(0, OTP_LENGTH);
    if (!/^\d+$/.test(pastedData)) return;

    const newOtp = [...otp];
    pastedData.split('').forEach((digit, index) => {
      if (index < OTP_LENGTH) {
        newOtp[index] = digit;
      }
    });
    setOtp(newOtp);

    // Focus last filled input or next empty
    const lastFilledIndex = Math.min(pastedData.length - 1, OTP_LENGTH - 1);
    inputRefs.current[lastFilledIndex]?.focus();

    // Auto-submit if complete
    if (newOtp.every((digit) => digit)) {
      verifyOtpMutation.mutate(newOtp.join(''));
    }
  };

  const formatPhoneForDisplay = (phoneNumber: string): string => {
    if (phoneNumber.startsWith('234')) {
      return '+234 ' + phoneNumber.slice(3, 6) + ' ' + phoneNumber.slice(6, 9) + ' ' + phoneNumber.slice(9);
    }
    return phoneNumber;
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
        </div>

        <Card className="p-6">
          <button
            onClick={() => navigate('/login')}
            className="flex items-center gap-1 text-sm text-gray-600 hover:text-gray-900 mb-4"
          >
            <ArrowLeft className="h-4 w-4" />
            Back
          </button>

          <div className="text-center mb-6">
            <h2 className="text-xl font-semibold text-gray-900">Verify your number</h2>
            <p className="text-gray-600 text-sm mt-1">
              Enter the 6-digit code sent to{' '}
              <span className="font-medium">{formatPhoneForDisplay(phone || '')}</span>
            </p>
          </div>

          <div className="flex justify-center gap-2 mb-6" onPaste={handlePaste}>
            {otp.map((digit, index) => (
              <input
                key={index}
                ref={(el) => (inputRefs.current[index] = el)}
                type="text"
                inputMode="numeric"
                maxLength={1}
                value={digit}
                onChange={(e) => handleChange(index, e.target.value)}
                onKeyDown={(e) => handleKeyDown(index, e)}
                className="w-12 h-14 text-center text-xl font-semibold border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                disabled={verifyOtpMutation.isPending}
              />
            ))}
          </div>

          <Button
            className="w-full"
            size="lg"
            onClick={() => verifyOtpMutation.mutate(otp.join(''))}
            loading={verifyOtpMutation.isPending}
            disabled={!otp.every((digit) => digit)}
          >
            Verify
          </Button>

          <div className="text-center mt-4">
            {resendTimer > 0 ? (
              <p className="text-sm text-gray-500">
                Resend code in <span className="font-medium">{resendTimer}s</span>
              </p>
            ) : (
              <button
                onClick={() => resendOtpMutation.mutate()}
                disabled={resendOtpMutation.isPending}
                className="text-sm text-primary-600 hover:text-primary-700 font-medium"
              >
                {resendOtpMutation.isPending ? 'Sending...' : 'Resend code'}
              </button>
            )}
          </div>
        </Card>
      </div>
    </div>
  );
}
