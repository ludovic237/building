import {Component, Inject, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
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
import {CommonModule} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-service-dialog',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    MatTabsModule,
    MatIconModule,
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
  public billingModes: string[] = ['Monthly', 'Yearly', 'One-Time'];
  public validatedOptions: any[] = []; // List of validated options
  public selectedOptions: any[] = []; // List of validated options

  constructor(
    public dialogRef: MatDialogRef<ServiceDialogComponent>,
    private snackBar: MatSnackBar,
    private serviceService: ServiceService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder
  ) {
    this.form = this.fb.group({
      code: [data?.code || '', Validators.required],
      name: [data?.name || '', Validators.required],
      // price: [data?.price || '', Validators.required],
      description: [data?.description || '', Validators.required],
      isActive: [data?.isActive || true, Validators.required],
      billingMode: [data?.billingMode || '', Validators.required],
      options: this.fb.array([]),
      validatedOptions: [[]] // Form control for validated options
    });
  }

  ngOnInit(): void {
    if (this.data?.id) {
      // Fetch the service details for update
      this.serviceService.getServiceWithOptions(this.data.id).subscribe({
        next: (service) => {
          this.form.patchValue(service);
          service.activeOptions.forEach((option: any) => this.validatedOptions.push(option));
        },
        error: (err) => {
          this.snackBar.open('Failed to load service details.', 'Close', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
          console.error('Error loading service:', err);
        }
      });
    } else if (this.data?.options) {
      this.data.options.forEach((option: any) => this.addOption(option));
    }
  }

  get options(): FormArray {
    return this.form.get('options') as FormArray;
  }

  addOption(optionData: any = {name: '', price: null}): void {
    const optionGroup = this.fb.group({
      name: [optionData.name, Validators.required],
      price: [optionData.price, [Validators.required, Validators.min(0)]],
      quantity: [optionData.quantity, Validators.min(0)] // Optional quantity
    });
    this.options.push(optionGroup);
  }

  removeOption(index: number): void {
    this.options.removeAt(index);
  }

  toggleSelection(index: number): void {
    const item = this.validatedOptions[index];

    if (item.isSelected) {
      // Désactiver l'option : la retirer de selectedOptions
      item.isSelected = false;
      this.selectedOptions = this.selectedOptions.filter(option => option !== item);
    } else {
      // Activer l'option : l'ajouter à selectedOptions
      item.isSelected = true;
      this.selectedOptions.push(item);
    }

    console.log('Option toggled:', item);
    console.log('Selected options:', this.selectedOptions);
  }

  get isSaveDisabled(): boolean {
    const isFormInvalid = this.form.invalid;
    const noSelectedOptions = !this.validatedOptions.some(option => option.isSelected);
    // console.log('Form invalid:', isFormInvalid, 'No selected options:', noSelectedOptions);
    return isFormInvalid || noSelectedOptions;
  }

  validateOption(index: number): void {
    const option = this.options.at(index).value;
    console.log('Validating option:', option);
    if (option.name && option.price > 0) {
      option.isSelected = false; // Mark as selected
      this.validatedOptions.push(option); // Add to validated options
      this.snackBar.open('Option validated successfully!', 'Close', {
        duration: 2000,
        panelClass: ['success-snackbar']
      });
      this.removeOption(index);
    } else {
      this.snackBar.open('Invalid option. Please fill in all fields.', 'Close', {
        duration: 2000,
        panelClass: ['error-snackbar']
      });
    }
  }


  removeValidatedOption(index: number): void {
    if (index >= 0 && index < this.selectedOptions.length) {
      this.selectedOptions.splice(index, 1);
      this.snackBar.open('Option removed successfully!', 'Close', {
        duration: 2000,
        panelClass: ['success-snackbar']
      });
    } else {
      this.snackBar.open('Invalid option index.', 'Close', {
        duration: 2000,
        panelClass: ['error-snackbar']
      });
    }
  }


  onSubmit(): void {
    if (this.form.valid) {
      const activeOptions = this.validatedOptions;
      const serviceData = {
        ...this.form.value,
        activeOptions // Include active options in the submitted data
      };

      if (this.data?.id) {
        // Update existing service
        this.serviceService.updateServiceWithOptions(this.data.id, serviceData).subscribe({
          next: (response) => {
            this.snackBar.open('Service updated successfully!', 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
            this.dialogRef.close(response);
          },
          error: (err) => {
            this.snackBar.open('Failed to update the service. Please try again.', 'Close', {
              duration: 3000,
              panelClass: ['error-snackbar']
            });
            console.error('Error updating service:', err);
          }
        });
      } else {
        // Create new service
        this.saveService(serviceData);
      }
    } else {
      this.snackBar.open('Please fill in all required fields.', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
    }
  }

  private saveService(serviceData: Service): void {
    console.log('Service data to save:', serviceData);
    this.serviceService.createServiceData(serviceData).subscribe({
      next: (response) => {
        this.snackBar.open('Service saved successfully!', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.dialogRef.close(response);
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
