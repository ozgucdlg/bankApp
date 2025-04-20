import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AccountService } from '../../core/services/account.service';
import { TransactionService } from '../../core/services/transaction.service';
import { Account } from '../../shared/models/account.model';
import { Transaction } from '../../shared/models/transaction.model';
import { User } from '../../shared/models/user.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DashboardComponent implements OnInit {
  account: Account | null = null;
  recentTransactions: Transaction[] = [];
  filteredTransactions: Transaction[] = [];
  transferForm: FormGroup;
  error: string = '';
  success: string = '';
  loading: boolean = true;
  transactionFilter: 'all' | 'incoming' | 'outgoing' = 'all';

  constructor(
    private authService: AuthService,
    private accountService: AccountService,
    private transactionService: TransactionService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.transferForm = this.formBuilder.group({
      toAccountId: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      description: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadAccountData();
    this.loadRecentTransactions();
  }

  logout(): void {
    console.log('Logout clicked');
    if (confirm('Are you sure you want to logout?')) {
      console.log('Logout confirmed');
      
      // Disable the button and update UI
      const logoutBtn = document.getElementById('logoutBtn') as HTMLButtonElement;
      if (logoutBtn) {
        logoutBtn.disabled = true;
        logoutBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Logging out...';
      }
      
      // Show loading state
      this.loading = true;
      
      // Add a console log to track execution
      console.log('Dashboard: Calling AuthService.logout()');
      
      // Call logout with some basic error handling
      try {
        this.authService.logout();
        console.log('Dashboard: AuthService.logout() called successfully');
      } catch (error) {
        console.error('Dashboard: Error calling AuthService.logout():', error);
        // Re-enable the button if there's an error
        if (logoutBtn) {
          logoutBtn.disabled = false;
          logoutBtn.innerHTML = '<i class="fas fa-sign-out-alt me-2"></i>Logout';
        }
        this.loading = false;
      }
    }
  }

  loadAccountData(): void {
    const user: User | null = this.authService.currentUserValue;
    if (user) {
      this.accountService.getAccountDetails(user.accountId).subscribe({
        next: (account: Account) => {
          this.account = account;
          this.loading = false;
        },
        error: (error: any) => {
          console.error('Error loading account:', error);
          this.error = 'Failed to load account details';
          this.loading = false;
        }
      });
    } else {
      this.error = 'User not authenticated';
      this.loading = false;
    }
  }

  loadRecentTransactions(): void {
    const user: User | null = this.authService.currentUserValue;
    if (user) {
      this.transactionService.getRecentTransactions(user.accountId).subscribe({
        next: (transactions: Transaction[]) => {
          this.recentTransactions = transactions;
          this.filterTransactions(this.transactionFilter);
        },
        error: (error: any) => {
          console.error('Error loading transactions:', error);
          this.error = 'Failed to load recent transactions';
        }
      });
    }
  }

  onTransfer(): void {
    if (this.transferForm.valid && this.account) {
      const { toAccountId, amount, description } = this.transferForm.value;
      
      this.transactionService.createTransaction({
        fromAccountId: this.account.id,
        toAccountId: parseInt(toAccountId),
        amount: parseFloat(amount),
        description
      }).subscribe({
        next: () => {
          this.success = 'Transfer successful';
          this.transferForm.reset();
          this.loadAccountData();
          this.loadRecentTransactions();
        },
        error: (error: any) => {
          console.error('Transfer error:', error);
          this.error = error.error || 'Transfer failed';
        }
      });
    }
  }

  filterTransactions(filter: 'all' | 'incoming' | 'outgoing'): void {
    this.transactionFilter = filter;
    if (filter === 'all') {
      this.filteredTransactions = this.recentTransactions;
    } else {
      this.filteredTransactions = this.recentTransactions.filter(transaction => {
        const isIncoming = transaction.toAccount.id === this.account?.id;
        return filter === 'incoming' ? isIncoming : !isIncoming;
      });
    }
  }

  getMonthlyIncome(): number {
    const now = new Date();
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);
    return this.recentTransactions
      .filter(t => new Date(t.timestamp) >= monthStart && t.toAccount.id === this.account?.id)
      .reduce((sum, t) => sum + t.amount, 0);
  }

  getMonthlySpending(): number {
    const now = new Date();
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);
    return this.recentTransactions
      .filter(t => new Date(t.timestamp) >= monthStart && t.fromAccount.id === this.account?.id)
      .reduce((sum, t) => sum + t.amount, 0);
  }

  showQuickTransfer(): void {
    // TODO: Implement quick transfer modal
    console.log('Quick transfer not implemented yet');
  }

  downloadStatement(): void {
    // TODO: Implement statement download
    console.log('Statement download not implemented yet');
  }

  showRecentRecipients(): void {
    // TODO: Implement recent recipients modal
    console.log('Recent recipients not implemented yet');
  }

  loadMoreTransactions(): void {
    // TODO: Implement pagination
    console.log('Load more transactions not implemented yet');
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
