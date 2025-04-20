import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Transaction } from '../../shared/models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  constructor(private http: HttpClient) {}

  getRecentTransactions(accountId: number): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${environment.apiUrl}/transactions/account/${accountId}`);
  }

  createTransaction(transaction: {
    fromAccountId: number;
    toAccountId: number;
    amount: number;
    description: string;
  }): Observable<Transaction> {
    return this.http.post<Transaction>(`${environment.apiUrl}/transactions/transfer`, transaction);
  }

  getTransactionDetails(transactionId: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${environment.apiUrl}/transactions/${transactionId}`);
  }
} 