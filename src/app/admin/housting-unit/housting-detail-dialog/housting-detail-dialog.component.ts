import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatTabsModule} from '@angular/material/tabs';
import {FlexLayoutModule} from '@ngbracket/ngx-layout';
import {MatTableModule} from "@angular/material/table";

@Component({
  selector: 'app-housting-detail-dialog',
  imports: [
    ReactiveFormsModule,
    FlexLayoutModule,
    MatTableModule,
    MatTabsModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDialogModule
  ],
  templateUrl: './housting-detail-dialog.component.html',
  styleUrl: './housting-detail-dialog.component.scss'
})
export class HoustingDetailDialogComponent implements OnInit {
  public form: FormGroup;
  public tenant: any;
  public financial: any;
  public issues: any[];
  public additional: any;

  constructor(
    public dialogRef: MatDialogRef<HoustingDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder
  ) {
  }

  ngOnInit(): void {
    // Initialize the form
    this.form = this.fb.group({
      id: [0],
      username: ['', Validators.required],
      email: [null],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phone: [null],
      billing: this.fb.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        email: ['', Validators.required],
        phone: ['', Validators.required],
        address: ['', Validators.required]
      })
    });

    // Load data from the injected dialog data

    this.tenant = this.data.tenantInformation || {};
    this.financial = this.data.financialInformation || { billingCycles: [], subscriptions: [] };
    this.issues = this.data.issueTracking || [];
    this.additional = this.data.additionalInformation || { invoices: [] };
  }

  public onSubmit(): void {
    if (this.form.valid) {
      this.dialogRef.close(this.form.value);
    }
  }

  public closeDialog(): void {
    this.dialogRef.close();
  }
}
