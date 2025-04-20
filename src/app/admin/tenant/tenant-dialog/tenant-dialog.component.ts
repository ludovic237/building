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
import {UserService} from "@services/user.service";
import {HoustingUnitService} from "@services/housting-unit.service";
import {TenantService} from "@services/tenant.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-tenant-dialog',
  imports: [
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
  templateUrl: './tenant-dialog.component.html',
  styleUrl: './tenant-dialog.component.scss'
})
export class TenantDialogComponent implements OnInit {

  public users: any[] = [];
  public logements: any[] = [];
  public form: FormGroup;
  protected logementBasePrice: number = 0;
  public minDate: Date = new Date();

  constructor(public dialogRef: MatDialogRef<TenantDialogComponent>,
              private housingUnitService: HoustingUnitService,
              private tenantService: TenantService,
              private snackBar: MatSnackBar,
              private usersService: UserService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              public fb: FormBuilder) {
    this.form = this.fb.group({
      userId: [data?.userId || '', Validators.required],
      housingUnitId: [data?.housingUnitId || '', Validators.required],
      moveInDate: [data?.moveInDate || '', Validators.required],
      moveOutDate: [data?.moveOutDate || ''],
      securityDeposit: [data?.securityDeposit || '', [Validators.required, Validators.min(0)]],
      status: ['Unpaid'] // Default value
    });
  }

  ngOnInit(): void {
    console.log("this.data");
    console.log(this.data);
    this.fetchhousingUnits();
    this.fetchUsers();
    if (this.data) {
      this.form.patchValue({
        userId: this.data.userId,
        housingUnitId: this.data.housingUnitId,
        moveInDate: this.data.moveInDate,
        moveOutDate: this.data.moveOutDate,
        securityDeposit: this.data.securityDeposit
      });
      this.logementBasePrice = this.data.houstinUnitPrice;
      this.form.updateValueAndValidity();;
    }
  }

  private fetchUsers(): void {
    this.usersService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
      },
      error: (err) => {
        console.error('Error fetching users:', err);
      }
    });
  }

  private fetchhousingUnits(): void {
    this.housingUnitService.gethousingUnits().subscribe({
      next: (data) => {
        this.logements = data;
      },
      error: (err) => {
        console.error('Error fetching housing units:', err);
      }
    });
  }

  onLogementChange(event: any): void {
    const selectedLogement = this.logements.find(logement => logement.id === event.value);
    this.logementBasePrice = selectedLogement ? selectedLogement.price : 0;

    if (this.logementBasePrice > 0) {
      // Activer le champ et mettre à jour les validations
      this.form.get('securityDeposit')?.enable();
      this.form.get('securityDeposit')?.setValidators([
        Validators.required,
        Validators.min(0),
        Validators.max(this.logementBasePrice)
      ]);
    } else {
      // Désactiver le champ et réinitialiser sa valeur
      this.form.get('securityDeposit')?.reset(0);
      this.form.get('securityDeposit')?.disable();
    }

    this.form.get('securityDeposit')?.updateValueAndValidity();
    this.updatestatus();
  }

  updatestatus(): void {
    const securityDeposit = this.form.get('securityDeposit')?.value || 0;
    const status = securityDeposit >= this.logementBasePrice ? 'Paid' : 'Unpaid';
    this.form.get('status')?.setValue(status);
  }

  public onSubmit(): void {
    console.log('Form Values simple:', this.form.value); // Log des valeurs saisies
    if (this.form.valid) {
      console.log('Form Values:', this.form.value); // Log des valeurs saisies

      const tenant = this.form.value;
      if (this.data) {
        console.log("tenant");
        console.log(tenant);
        this.tenantService.updateTenant(this.data.id,tenant).subscribe({
          next: (createdUnit) => {
            this.dialogRef.close(createdUnit);
          },
          error: (err) => {
            console.error('Error creating housing unit:', err);
          }
        });
      }
      else {
        this.tenantService.createTenant(tenant).subscribe({
          next: (response) => {
            console.log('Housing unit created successfully:', response);
            this.snackBar.open('Housing unit created successfully!', '×', {
              panelClass: 'success',
              verticalPosition: 'top',
              duration: 3000
            });
            this.dialogRef.close(response);
          },
          error: (err) => {
            console.error('Error creating housing unit:', err);
            this.snackBar.open('Failed to create housing unit.', '×', {
              panelClass: 'error',
              verticalPosition: 'top',
              duration: 3000
            });
          }
        });
      }

    }
  }

}
