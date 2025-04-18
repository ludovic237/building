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
  selector: 'app-rent-dialog',
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
  templateUrl: './rent-dialog.component.html',
  styleUrl: './rent-dialog.component.scss'
})
export class RentDialogComponent implements OnInit {

  public locataires: any[] = [];
  public logements: any[] = [];
  public form: FormGroup;
  protected logementBasePrice: number = 0;
  public minDate: Date = new Date();

  constructor(public dialogRef: MatDialogRef<RentDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              public fb: FormBuilder) {
    this.form = this.fb.group({
      locataireId: [data?.locataireId || '', Validators.required],
      logementId: [data?.logementId || '', Validators.required],
      mois: [data?.mois || '', Validators.required],
      annee: [data?.annee || '', [Validators.required, Validators.min(1900)]],
      montant: [data?.montant || '', [Validators.required, Validators.min(0)]],
      statut: [data?.statut || 'non payé', Validators.required],
      datePaiement: [data?.datePaiement || '']
    });
  }

  ngOnInit(): void {
    this.locataires = this.data.users || [
      { id: 1, name: 'John Doe' },
      { id: 2, name: 'Jane Smith' },
      { id: 3, name: 'Alice Johnson' },
      { id: 4, name: 'Bob Brown' }
    ];
    this.logements = this.data.logements || [
      { id: 101, name: 'Apartment A', basePrice: 500 },
      { id: 102, name: 'Apartment B', basePrice: 700 }
    ];
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
