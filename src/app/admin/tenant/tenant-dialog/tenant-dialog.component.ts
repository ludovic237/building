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
              @Inject(MAT_DIALOG_DATA) public data: any,
              public fb: FormBuilder) {
    this.form = this.fb.group({
      userId: [data?.userId || '', Validators.required],
      logementId: [data?.logementId || '', Validators.required],
      dateEntree: [data?.dateEntree || '', Validators.required],
      dateSortie: [data?.dateSortie || ''],
      depotGarantie: [data?.depotGarantie || '', [Validators.required, Validators.min(0)]],
      statut: ['Unpaid'] // Default value
    });
  }

  ngOnInit(): void {
    this.users = this.data.users || [
      { id: 1, name: 'John Doe' },
      { id: 2, name: 'Jane Smith' }
    ];
    this.logements = this.data.logements || [
      { id: 101, name: 'Apartment A', basePrice: 500 },
      { id: 102, name: 'Apartment B', basePrice: 700 }
    ];
  }

onLogementChange(event: any): void {
  const selectedLogement = this.logements.find(logement => logement.id === event.value);
  this.logementBasePrice = selectedLogement ? selectedLogement.basePrice : 0;

  if (this.logementBasePrice > 0) {
    // Activer le champ et mettre à jour les validations
    this.form.get('depotGarantie')?.enable();
    this.form.get('depotGarantie')?.setValidators([
      Validators.required,
      Validators.min(0),
      Validators.max(this.logementBasePrice)
    ]);
  } else {
    // Désactiver le champ et réinitialiser sa valeur
    this.form.get('depotGarantie')?.reset(0);
    this.form.get('depotGarantie')?.disable();
  }

  this.form.get('depotGarantie')?.updateValueAndValidity();
  this.updateStatut();
}

  updateStatut(): void {
    const depotGarantie = this.form.get('depotGarantie')?.value || 0;
    const statut = depotGarantie >= this.logementBasePrice ? 'Paid' : 'Unpaid';
    this.form.get('statut')?.setValue(statut);
  }

  public onSubmit(): void {
    console.log('Form Values simple:', this.form.value); // Log des valeurs saisies
      if (this.form.valid) {
      console.log('Form Values:', this.form.value); // Log des valeurs saisies
      this.dialogRef.close(this.form.value);
    }
  }

}
