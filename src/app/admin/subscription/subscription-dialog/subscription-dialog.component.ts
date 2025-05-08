import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatTabsModule} from '@angular/material/tabs';
import {FlexLayoutModule} from '@ngbracket/ngx-layout';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {TenantService} from "@services/tenant.service";
import {ServiceService} from "@services/service.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {CommonModule} from "@angular/common";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatChipsModule} from "@angular/material/chips";
import {MatDividerModule} from "@angular/material/divider";
import {MatCardModule} from "@angular/material/card";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {SubscriptionService} from "@services/subscription.service";

@Component({
  selector: 'app-subscription-dialog',
  imports: [
    FormsModule,
    MatToolbarModule,
    MatCheckboxModule,
    MatIconModule,
    MatChipsModule,
    MatCardModule,
    MatDividerModule,
    CommonModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    MatTabsModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogModule
  ],
  templateUrl: './subscription-dialog.component.html',
  styleUrl: './subscription-dialog.component.scss'
})
export class SubscriptionDialogComponent implements OnInit {

  title: string = 'Create Subscription';
  selectedOption: any = null;
  isReadonly: boolean = false;
  selectedQuantity: number | null = null;
  addedOptions: any[] = [];

  public form: FormGroup;
  public locataires: any[] = [];
  public services: any[] = [];
  public statuss: string[] = ['actif', 'inactif'];

  public validatedOptions: any[] = []; // List of validated options
  public selectedOptions: any[] = []; // List of validated options

  public selectedServiceOptions: any[] = [];
  public totalPrice: number = 0;

  constructor(public dialogRef: MatDialogRef<SubscriptionDialogComponent>,
              private snackBar: MatSnackBar,
              private tenantService: TenantService,
              private serviceService: ServiceService,
              private subscriptionService: SubscriptionService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {
    console.log("constructor ");
    console.log(this.data);
    this.form = this.fb.group({
      tenantId: [data?.tenantId || '', Validators.required],
      serviceId: [data?.serviceId || '', Validators.required],
      dateDebut: [data?.dateDebut || '', Validators.required],
      dateFin: [data?.dateFin || '', Validators.required],
      status: [data?.status || 'actif', Validators.required],
      options: [[]],
    });
  }

  ngOnInit(): void {
    if (this.data?.id) {
      this.title = "Subscription detail"

      Object.keys(this.form.controls).forEach(controlName => {
        this.form.get(controlName)?.disable();
      });

      // Object.keys(this.form.controls).forEach(controlName => {
      //   const control = this.form.get(controlName);
      //   control?.setValue(control.value); // Retain the current value
      //   control?.disable({ onlySelf: true, emitEvent: false }); // Prevent modifications
      // });

      this.isReadonly = true;
    }

    // Initialize the form with data if available
    this.fetchTenants();

  }

  public onSubmit(): void {
    if (this.form.valid) {
      // Prepare the payload
      const payload = {
        tenantId: this.form.get('tenantId')?.value,
        serviceId: this.form.get('serviceId')?.value,
        dateDebut: this.form.get('dateDebut')?.value,
        dateFin: this.form.get('dateFin')?.value,
        status: this.form.get('status')?.value,
        options: this.addedOptions.map(option => ({
          id: option.id,
          quantity: option.quantity
        }))
      };
      this.subscriptionService.createSubscriptionWithDetails(payload).subscribe({
        next: (data) => {
          // Show success message
          this.snackBar.open('Subscription created successfully!', 'Close', {
            duration: 3000,
            verticalPosition: 'top'
          });
          this.dialogRef.close(payload);
        },
        error: (err) => {
          // Show error message
          this.snackBar.open('Failed to create subscription. Please try again.', 'Close', {
            duration: 3000,
            verticalPosition: 'top'
          });
          console.error('Error creating subscription:', err);
        }
      });
    }
  }

  fetchServices(): void {
    this.serviceService.getServiceAllWithOptions().subscribe({
      next: (data) => {
        this.services = data;
        if (this.data?.id) {
          // Wait for tenants and services to load before updating the form
          this.subscriptionService.getSubscriptionFormattedData(this.data.id).subscribe({
            next: (service) => {
              // Convert date strings to Date objects
              const dateDebut = new Date(service.dateDebut);
              const dateFin = new Date(service.dateFin);

              // Patch the form with the data
              this.form.patchValue({
                tenantId: service.tenantId,
                serviceId: service.serviceId,
                dateDebut: dateDebut,
                dateFin: dateFin,
                status: service.status,
              });

              // If options are returned, populate them
              if (service.options) {
                this.addedOptions = service.options.map((option: { optionId: number; quantity: number }) => {
                const detailedOption = this.services
                    .find(s => s.id === service.serviceId)
                    ?.activeOptions.find((o: { id: number }) => o.id === option.optionId);

                  return {
                    id: option.optionId,
                    quantity: option.quantity,
                    name: detailedOption?.name || 'Unknown',
                    price: detailedOption?.price || 0
                  };
                });
                // Update the selected service options
                if (service.serviceId) {
                  this.onServiceChange(service.serviceId);
                }
              }
            },
            error: (err) => {
              this.snackBar.open('Failed to load service details.', 'Close', {
                duration: 3000,
                panelClass: ['error-snackbar']
              });
              console.error('Error loading service:', err);
            }
          });
        }
      },
      error: (err) => {
        console.error('Error fetching housing units:', err);
      }
    });
  }

  private fetchTenants(): void {
    this.tenantService.getTenants().subscribe({
      next: (data) => {
        this.locataires = data;
        this.fetchServices();
      },
      error: (err) => {
        console.error('Error fetching users:', err);
      }
    });
  }

  onServiceChange(serviceId: number): void {
    const selectedService = this.services.find(service => service.id === serviceId);
    this.selectedServiceOptions = selectedService?.activeOptions || [];

    // Clear existing quantity controls
    Object.keys(this.form.controls).forEach(controlName => {
      if (controlName.startsWith('quantity_')) {
        this.form.removeControl(controlName);
      }
    });

    // Add quantity controls for each option
    this.selectedServiceOptions.forEach(option => {
      this.form.addControl('quantity_' + option.id, this.fb.control(0, [Validators.min(0), Validators.max(option.quantity)]));
    });

    this.form.get('options')?.setValue([]);
    this.calculateTotalPrice();
  }

  onOptionsChange(): void {
    this.calculateTotalPrice();
  }

  removeOption(optionToRemove: any): void {
    this.addedOptions = this.addedOptions.filter(option => option.id !== optionToRemove.id);
    this.calculateTotalPrice();
  }

  calculateTotalPrice(): void {
    // Get the selected service
    const selectedService = this.services.find(service => service.id === this.form.get('serviceId')?.value);

    // Calculate the service price (if it exists)
    const servicePrice = selectedService?.price || 0;

    // Calculate the total price of added options
    const optionsPrice = this.addedOptions.reduce((sum, option) => sum + (option.price * option.quantity), 0);

    // Update the total price
    this.totalPrice = servicePrice + optionsPrice;
  }

  addOption(): void {
    if (this.selectedOption && this.selectedQuantity) {
      const existingOption = this.addedOptions.find(option => option.id === this.selectedOption.id);
      if (existingOption) {
        // Update the quantity of the existing option
        existingOption.quantity = this.selectedQuantity;
      } else {
        // Add the new option
        this.addedOptions.push({
          ...this.selectedOption,
          quantity: this.selectedQuantity
        });
      }
      // Recalculate the total price
      this.calculateTotalPrice();

      // Reset the selection
      this.selectedOption = null;
      this.selectedQuantity = null;
    }
  }

}
