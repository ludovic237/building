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
  selector: 'app-subscription-dialog',
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
  templateUrl: './subscription-dialog.component.html',
  styleUrl: './subscription-dialog.component.scss'
})
export class SubscriptionDialogComponent implements OnInit {

  public form: FormGroup;
  public locataires: any[] = [];
  public services: any[] = [];
  public statuts: string[] = ['actif', 'inactif'];

  constructor(public dialogRef: MatDialogRef<SubscriptionDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {
    this.form = this.fb.group({
      locataireId: [data?.locataireId || '', Validators.required],
      serviceId: [data?.serviceId || '', Validators.required],
      dateDebut: [data?.dateDebut || '', Validators.required],
      dateFin: [data?.dateFin || '', Validators.required],
      statut: [data?.statut || 'actif', Validators.required]
    });
  }

  ngOnInit(): void {
    this.locataires = this.data.locataires || [];
    this.services = this.data.services || [];
  }

  public onSubmit(): void {
    if (this.form.valid) {
      this.dialogRef.close(this.form.value);
    }
  }

}
