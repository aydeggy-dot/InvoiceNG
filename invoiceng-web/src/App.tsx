import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'react-hot-toast';

import { DashboardLayout } from '@/components/layout';
import { AuthGuard, GuestGuard } from '@/components/guards';

// Auth pages
import { LoginPage, VerifyOtpPage } from '@/pages/auth';

// Main pages
import { DashboardPage } from '@/pages/dashboard';
import { CustomersListPage } from '@/pages/customers';
import { InvoicesListPage, CreateInvoicePage, InvoiceDetailPage } from '@/pages/invoices';
import { SettingsPage } from '@/pages/settings';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 30000,
    },
  },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          {/* Auth routes */}
          <Route
            path="/login"
            element={
              <GuestGuard>
                <LoginPage />
              </GuestGuard>
            }
          />
          <Route
            path="/verify-otp"
            element={
              <GuestGuard>
                <VerifyOtpPage />
              </GuestGuard>
            }
          />

          {/* Protected routes */}
          <Route
            path="/"
            element={
              <AuthGuard>
                <DashboardLayout />
              </AuthGuard>
            }
          >
            <Route index element={<DashboardPage />} />
            <Route path="customers" element={<CustomersListPage />} />
            <Route path="invoices" element={<InvoicesListPage />} />
            <Route path="invoices/new" element={<CreateInvoicePage />} />
            <Route path="invoices/:id" element={<InvoiceDetailPage />} />
            <Route path="settings" element={<SettingsPage />} />
          </Route>
        </Routes>
      </BrowserRouter>

      <Toaster
        position="top-center"
        toastOptions={{
          duration: 3000,
          style: {
            background: '#333',
            color: '#fff',
          },
          success: {
            iconTheme: {
              primary: '#10b981',
              secondary: '#fff',
            },
          },
          error: {
            iconTheme: {
              primary: '#ef4444',
              secondary: '#fff',
            },
          },
        }}
      />
    </QueryClientProvider>
  );
}
