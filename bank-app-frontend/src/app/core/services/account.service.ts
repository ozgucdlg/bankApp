import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Account } from '../../shared/models/account.model';
import { Transaction } from '../../shared/models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  constructor(private http: HttpClient) { }

  getAccountDetails(accountId: number): Observable<Account> {
    return this.http.get<Account>(`${environment.apiUrl}/api/accounts/${accountId}`);
  }

  updateAccount(accountId: number, account: Account): Observable<Account> {
    return this.http.put<Account>(`${environment.apiUrl}/api/accounts/${accountId}`, account);
  }

  deposit(accountId: number, amount: number): Observable<Account> {
    return this.http.post<Account>(`${environment.apiUrl}/api/accounts/${accountId}/deposit`, { amount });
  }

  withdraw(accountId: number, amount: number): Observable<Account> {
    return this.http.post<Account>(`${environment.apiUrl}/api/accounts/${accountId}/withdraw`, { amount });
  }

  transfer(fromAccountId: number, toAccountId: number, amount: number): Observable<Transaction> {
    return this.http.post<Transaction>(`${environment.apiUrl}/api/transactions`, {
      fromAccountId,
      toAccountId,
      amount
    });
  }

  getTransactions(accountId: number): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${environment.apiUrl}/api/transactions/account/${accountId}`);
  }
} 