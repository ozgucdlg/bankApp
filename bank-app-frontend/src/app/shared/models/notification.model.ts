export interface Notification {
    id: number;
    recipient: string;
    subject: string;
    content: string;
    notificationType: 'EMAIL' | 'CONSOLE';
    sentAt: string;
    status: 'PENDING' | 'SENT' | 'FAILED';
} 