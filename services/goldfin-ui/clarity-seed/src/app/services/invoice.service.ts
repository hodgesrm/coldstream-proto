/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';

export class Invoice {
  identifier: string;
  effective_date: string;
  vendor: string;
  subtotal_amount: number;
  tax: number;
  total_amount: number;
  currency: string;
}

const MOCK_INVOICES: Invoice[] = [
  {
    identifier: 'INV-14066-486955',
    effective_date: '2017-04-25',
    vendor: 'Internap Corporation',
    subtotal_amount: 16932.40,
    tax: 0.00,
    total_amount: 16932.40,
    currency: 'USD'
  },
  {
    identifier: 'INV-14066-486956',
    effective_date: '2017-05-26',
    vendor: 'Internap Corporation',
    subtotal_amount: 16351.90,
    tax: 0.00,
    total_amount: 16351.90,
    currency: 'USD'
  },
  {
    identifier: 'WE666184',
    effective_date: '2017-04-30',
    vendor: 'OVH.com',
    subtotal_amount: 409.36,
    tax: 0.00,
    total_amount: 409.36,
    currency: 'USD'
  },
  {
    identifier: 'WE666187',
    effective_date: '2017-05-31',
    vendor: 'OVH.com',
    subtotal_amount: 423.01,
    tax: 0.00,
    total_amount: 423.01,
    currency: 'USD'
  },
];

@Injectable()
export class InvoiceService {
  getInvoices(): Promise<Invoice[]> {
    return Promise.resolve(MOCK_INVOICES);
  }

  getInvoice(identifier: string): Invoice {
    return MOCK_INVOICES.find(invoice => invoice.identifier === identifier);
  } 
}
