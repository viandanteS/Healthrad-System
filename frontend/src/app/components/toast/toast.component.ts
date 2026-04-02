import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed top-5 right-5 z-50 flex flex-col gap-3 pointer-events-none">
      @for (toast of toastService.toasts(); track toast.id) {
        <div 
          class="pointer-events-auto flex items-center gap-3 px-5 py-3 rounded-xl shadow-xl text-white font-semibold text-sm min-w-[280px] animate-slideIn"
          [ngClass]="{
            'bg-red-500':    toast.type === 'error',
            'bg-green-500':  toast.type === 'success',
            'bg-amber-500':  toast.type === 'warning',
            'bg-blue-500':   toast.type === 'info'
          }">
          <span class="text-lg">
            {{ toast.type === 'error' ? '⛔' : toast.type === 'success' ? '✅' : toast.type === 'warning' ? '⚠️' : 'ℹ️' }}
          </span>
          <span class="flex-1">{{ toast.message }}</span>
          <button (click)="toastService.dismiss(toast.id)" class="opacity-70 hover:opacity-100 text-lg leading-none">×</button>
        </div>
      }
    </div>
  `,
  styles: [`
    @keyframes slideIn {
      from { transform: translateX(120%); opacity: 0; }
      to   { transform: translateX(0);    opacity: 1; }
    }
    .animate-slideIn { animation: slideIn 0.3s ease-out; }
  `]
})
export class ToastComponent {
  toastService = inject(ToastService);
}
