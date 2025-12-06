import type { Invoice } from '@/types';
import { formatCurrency, formatDate } from './format';

/**
 * Generate payment link for invoice
 */
export function generatePaymentLink(invoiceId: string): string {
  const baseUrl = window.location.origin;
  return `${baseUrl}/pay/${invoiceId}`;
}

/**
 * Format phone number for WhatsApp
 */
export function formatPhoneForWhatsApp(phone: string): string {
  let cleaned = phone.replace(/\D/g, '');

  if (cleaned.startsWith('0')) {
    cleaned = '234' + cleaned.substring(1);
  }

  if (cleaned.startsWith('+')) {
    cleaned = cleaned.substring(1);
  }

  return cleaned;
}

/**
 * Generate WhatsApp message for invoice
 */
export function generateInvoiceMessage(invoice: Invoice): string {
  const paymentLink = generatePaymentLink(invoice.id);

  const itemsList = invoice.items
    ?.map((item) => `• ${item.name}: ${formatCurrency(item.total)}`)
    .join('\n') || '';

  const message = `
Hello ${invoice.customer?.name || 'Customer'},

You have a new invoice from *${invoice.businessName || 'InvoiceNG'}*:

*Invoice:* ${invoice.invoiceNumber}
*Date:* ${formatDate(invoice.issueDate)}
*Due:* ${formatDate(invoice.dueDate)}

*Items:*
${itemsList}

*Total:* ${formatCurrency(invoice.total)}

Pay securely online:
${paymentLink}

Thank you for your business!
`.trim();

  return message;
}

/**
 * Generate WhatsApp message for payment reminder
 */
export function generateReminderMessage(invoice: Invoice): string {
  const paymentLink = generatePaymentLink(invoice.id);
  const daysOverdue = Math.floor(
    (new Date().getTime() - new Date(invoice.dueDate).getTime()) / (1000 * 60 * 60 * 24)
  );

  const overdueText = daysOverdue > 0
    ? `This invoice is ${daysOverdue} day${daysOverdue > 1 ? 's' : ''} overdue.`
    : 'Payment is due soon.';

  const balance = invoice.total - (invoice.paidAmount || 0);

  const message = `
Hello ${invoice.customer?.name || 'Customer'},

This is a friendly reminder about your pending invoice.

*Invoice:* ${invoice.invoiceNumber}
*Balance Due:* ${formatCurrency(balance)}
*Due Date:* ${formatDate(invoice.dueDate)}

${overdueText}

Pay now:
${paymentLink}

Please ignore if you have already made payment.

Thank you!
`.trim();

  return message;
}

/**
 * Share invoice via WhatsApp
 */
export function shareToWhatsApp(phone: string, invoice: Invoice): void {
  const formattedPhone = formatPhoneForWhatsApp(phone);
  const message = generateInvoiceMessage(invoice);
  const encodedMessage = encodeURIComponent(message);

  const whatsappUrl = `https://wa.me/${formattedPhone}?text=${encodedMessage}`;
  window.open(whatsappUrl, '_blank');
}

/**
 * Send payment reminder via WhatsApp
 */
export function sendReminderToWhatsApp(phone: string, invoice: Invoice): void {
  const formattedPhone = formatPhoneForWhatsApp(phone);
  const message = generateReminderMessage(invoice);
  const encodedMessage = encodeURIComponent(message);

  const whatsappUrl = `https://wa.me/${formattedPhone}?text=${encodedMessage}`;
  window.open(whatsappUrl, '_blank');
}

/**
 * Check if WhatsApp sharing is available (web API)
 */
export function isWhatsAppAvailable(): boolean {
  return typeof window !== 'undefined';
}
