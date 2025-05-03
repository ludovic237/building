import { Component, OnInit, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AppService } from '@services/app.service';
import { DomHandlerService } from '@services/dom-handler.service';
import { Settings, SettingsService } from '@services/settings.service';
import { customers } from '../../common/data/customers';
import { InvoiceDialogComponent } from './invoice-dialog/invoice-dialog.component';
import { ConfirmDialogComponent } from '@shared-components/confirm-dialog/confirm-dialog.component';
import { MatCardModule } from '@angular/material/card';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NgxPaginationModule } from 'ngx-pagination';
import { MatDividerModule } from '@angular/material/divider';
import { PipesModule } from '../../theme/pipes/pipes.module';
import { MatTooltipModule } from '@angular/material/tooltip';
import {CommonModule} from "@angular/common";
import {Invoice} from "../../model/data";

@Component({
    selector: 'app-invoices',
    imports: [
      CommonModule,
        FlexLayoutModule,
        MatCardModule,
        MatButtonModule,
        MatDividerModule,
        MatIconModule,
        MatTooltipModule,
        NgxPaginationModule,
        PipesModule
    ],
    templateUrl: './invoices.component.html'
})
export class InvoicesComponent implements OnInit {

  public invoices: any[] = [];
  public locataires: any[] = [
    { id: 1, name: 'John Doe' },
    { id: 2, name: 'Jane Smith' },
    { id: 3, name: 'Alice Johnson' },
    { id: 4, name: 'Bob Brown' }
  ];
  public page: number = 1;
  public count: number = 5;

  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {
    // Mock data for invoices
    this.invoices = [
      {
        id: 1,
        tenantId: 1,
        type: 'eau',
        mois: 'January',
        montant: 100,
        status: 'payée',
        datePaiement: '2023-01-15'
      },
      {
        id: 2,
        tenantId: 2,
        type: 'électricité',
        mois: 'February',
        montant: 200,
        status: 'impayée',
        datePaiement: null
      }
    ];
  }

  public onPageChanged(event: any): void {
    this.page = event;
  }

  public openInvoiceDialog(data: Invoice | null): void {
    const dialogRef = this.dialog.open(InvoiceDialogComponent, {
      data: {
        invoice: data,
        locataires: this.locataires
      },
      panelClass: ['theme-dialog'],
      autoFocus: false
    });

    dialogRef.afterClosed().subscribe((invoice: Invoice) => {
      if (invoice) {
        const index = this.invoices.findIndex(i => i.id === invoice.id);
        if (index !== -1) {
          this.invoices[index] = invoice; // Update existing invoice
        } else {
          invoice.id = this.invoices.length + 1; // Assign new ID
          this.invoices.push(invoice); // Add new invoice
        }
      }
    });
  }

  public removeInvoice(invoice: Invoice): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: '400px',
      data: {
        title: 'Confirm Action',
        message: 'Are you sure you want to remove this invoice?'
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        this.invoices = this.invoices.filter(i => i.id !== invoice.id);
      }
    });
  }

  getTenantName(tenantId: number): string {
    const tenant = this.locataires.find(l => l.id === tenantId);
    return tenant ? tenant.name : 'Unknown';
  }
}
