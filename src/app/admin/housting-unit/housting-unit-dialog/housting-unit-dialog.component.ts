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
  selector: 'app-housting-unit-dialog',
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
  templateUrl: './housting-unit-dialog.component.html',
  styleUrl: './housting-unit-dialog.component.scss'
})
export class HoustingUnitDialogComponent implements OnInit {

  public form: FormGroup;
  public housingTypes: string[] = ['Studio', 'T2', 'T3', 'T4'];

  constructor(public dialogRef: MatDialogRef<HoustingUnitDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              public fb: FormBuilder) {
    this.form = this.fb.group({
      numeroAppartement: [data?.numeroAppartement || '', Validators.required],
      etage: [data?.etage || '', [Validators.required, Validators.min(0)]],
      superficie: [data?.superficie || '', [Validators.required, Validators.min(1)]],
      adresse: [data?.adresse || '', Validators.required],
      type: [data?.type || '', Validators.required]
    });
  }

  ngOnInit(): void {

  }

  public onSubmit(): void {
    console.log('Form Values simple:', this.form.value); // Log des valeurs saisies
      if (this.form.valid) {
      console.log('Form Values:', this.form.value); // Log des valeurs saisies
      this.dialogRef.close(this.form.value);
    }
  }

}
