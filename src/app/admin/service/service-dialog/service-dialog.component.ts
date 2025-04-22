import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatTabsModule} from '@angular/material/tabs';
import {FlexLayoutModule} from '@ngbracket/ngx-layout';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {ServiceService} from "@services/service.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Service} from "../../../model/data";

@Component({
  selector: 'app-service-dialog',
  imports: [
    ReactiveFormsModule,
    FlexLayoutModule,
    MatTabsModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatDialogModule
  ],
  templateUrl: './service-dialog.component.html',
  styleUrl: './service-dialog.component.scss'
})
export class ServiceDialogComponent implements OnInit {

  public form: FormGroup;
  public billingModes: string[] = ['Monthly', 'Yearly', 'One-Time']; // Example billing modes

  constructor(public dialogRef: MatDialogRef<ServiceDialogComponent>,
              private snackBar: MatSnackBar,
              private serviceService: ServiceService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              public fb: FormBuilder) {
    this.form = this.fb.group({
      code: [data?.code || '', Validators.required],
      name: [data?.name || '', Validators.required],
      description: [data?.description || '', Validators.required],
      isActive: [data?.isActive || true, [Validators.required]],
      billingMode: [data?.billingMode || '', Validators.required]
    });
  }

  ngOnInit(): void {
    console.log("Service dialog");
    console.log(this.data);
    if (this.data) {
      this.form.patchValue({
        code: this.data.code,
        name: this.data.name,
        description: this.data.description,
        billingMode: this.data.billingMode,
        isActive: this.data.isActive
      });
    }
  }

  public onSubmit(): void {
    if (this.form.valid) {
      this.saveService(this.form.value);
    } else {
      this.snackBar.open('Please fill in all required fields.', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
    }
  }

  private saveService(serviceData: Service): void {
    this.serviceService.createService(serviceData).subscribe({
      next: (response) => {
        this.snackBar.open('Service saved successfully!', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.dialogRef.close(response); // Close the modal on success
      },
      error: (err) => {
        this.snackBar.open('Failed to save the service. Please try again.', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
        console.error('Error saving service:', err);
      }
    });
  }
}
