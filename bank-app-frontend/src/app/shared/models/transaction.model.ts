import { Account } from './account.model';

export interface Transaction {
    id: number;
    fromAccount: Account;
    toAccount: Account;
    amount: number;
    timestamp: string;
    description: string;
    status: TransactionStatus;
}

export enum TransactionStatus {
    PENDING = 'PENDING',
    COMPLETED = 'COMPLETED',
    FAILED = 'FAILED'
} 