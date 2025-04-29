import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import {PaymentService} from "@services/payment.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ServiceService} from "@services/service.service";

@Component({
    selector: 'app-confirm-dialog',
    imports: [
        MatDialogModule,
        MatButtonModule,
        FlexLayoutModule
    ],
    templateUrl: './confirm-dialog.component.html'
})
export class ConfirmDialogComponent {

  constructor(
    public paymentService: PaymentService,
    public serviceService: ServiceService,
              private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) { }

  onConfirm(): void {

    this.serviceService.deleteService(this.data.id).subscribe({
      next: () => {
        this.dialogRef.close(true);
        this.snackBar.open(`Payment with ID ${this.data.id} has been deleted`, 'Close', {
          duration: 2000,
        });
      },
      error: (err) => {
        console.error('Error deleting payment:', err);
      }
    });
  }

  onDismiss(): void {
    this.dialogRef.close(false);
  }

}
