import { Component, OnInit, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AppService } from '@services/app.service';
import { DomHandlerService } from '@services/dom-handler.service';
import { Settings, SettingsService } from '@services/settings.service';
import { customers } from '../../common/data/customers';
import { TenantDialogComponent } from './tenant-dialog/tenant-dialog.component';
import { ConfirmDialogComponent } from '@shared-components/confirm-dialog/confirm-dialog.component';
import { MatCardModule } from '@angular/material/card';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NgxPaginationModule } from 'ngx-pagination';
import { MatDividerModule } from '@angular/material/divider';
import { PipesModule } from '../../theme/pipes/pipes.module';
import { MatTooltipModule } from '@angular/material/tooltip';
import {RentDialogComponent} from "../rent/rent-dialog/rent-dialog.component";
import {CommonModule} from "@angular/common";

@Component({
    selector: 'app-tenants',
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
    templateUrl: './tenants.component.html'
})
export class TenantsComponent implements OnInit {
  public customers: any[] = [];
  public stores = [
    { id: 1, name: 'Store 1' },
    { id: 2, name: 'Store 2' }
  ]
  public tenants: any[] = [
    { id: 1, name: 'John Doe', apartment: 'A101', entryDate: '2023-01-15', deposit: 500, paymentStatus: 'Paid' },
    { id: 2, name: 'Jane Smith', apartment: 'B202', entryDate: '2023-02-01', deposit: 700, paymentStatus: 'Unpaid' }
  ];
  // public page: number = 1;
  // public count: number = 5;
  public countries: any[] = [];
  public page: any;
  public count = 6;
  domHandlerService = inject(DomHandlerService);
  public settings: Settings;

  constructor(public appService: AppService, public dialog: MatDialog, public settingsService: SettingsService) {
    this.settings = this.settingsService.settings;
  }

  ngOnInit(): void {
    this.countries = this.appService.getCountries();
    this.customers = customers;
  }

  public onPageChanged(event: any) {
    this.page = event;
    this.domHandlerService.winScroll(0, 0);
  }

  // public openTenantDialog(data: any) {
  //   const dialogRef = this.dialog.open(TenantDialogComponent, {
  //     data: {
  //       customer: data,
  //       stores: this.stores,
  //       countries: this.countries
  //     },
  //     panelClass: ['theme-dialog'],
  //     autoFocus: false,
  //     direction: (this.settings.rtl) ? 'rtl' : 'ltr'
  //   });
  //   dialogRef.afterClosed().subscribe(customer => {
  //     if (customer) {
  //       const index: number = this.customers.findIndex(x => x.id == customer.id);
  //       if (index !== -1) {
  //         this.customers[index] = customer;
  //       }
  //       else {
  //         let last_customer = this.customers[this.customers.length - 1];
  //         customer.id = last_customer.id + 1;
  //         this.customers.push(customer);
  //       }
  //     }
  //   });
  // }

  public remove(customer: any) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Confirm Action",
        message: "Are you sure you want remove this customer?"
      }
    });
    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        const index: number = this.customers.indexOf(customer);
        if (index !== -1) {
          this.customers.splice(index, 1);
        }
      }
    });
  }


  public openTenantDialog(data: any): void {
    const dialogRef = this.dialog.open(TenantDialogComponent, {
      data: {
        customer: data,
        stores: this.stores,
        countries: this.countries
      },
      panelClass: ['theme-dialog'],
      autoFocus: false,
      direction: (this.settings.rtl) ? 'rtl' : 'ltr'
    });

    dialogRef.afterClosed().subscribe(tenant => {
      const usersDial=  [
        { id: 1, name: 'John Doe' },
        { id: 2, name: 'Jane Smith' }
      ];
      const logementsDial = [
        { id: 101, name: 'Apartment A', basePrice: 500 },
        { id: 102, name: 'Apartment B', basePrice: 700 }
      ];
      if (tenant) {
        const formattedTenant = {
          id: this.tenants.length + 1, // Generate a new ID
          name: usersDial.find(user => user.id === tenant.userId)?.name || 'Unknown',
          apartment: logementsDial.find(logement => logement.id === tenant.logementId)?.name || 'Unknown',
          entryDate: tenant.dateEntree,
          deposit: tenant.depotGarantie,
          paymentStatus: tenant.statut
        };
        console.log('New Tenant Data:', formattedTenant);
        // this.tenants.push(tenant); // Add the new tenant to the list
        const index: number = this.tenants.findIndex(x => x.id === tenant.id);
        if (index !== -1) {
          this.tenants[index] = tenant; // Mise à jour d'un locataire existant
        } else {
          tenant.id = this.tenants.length + 1; // Attribution d'un nouvel ID
          this.tenants.push(formattedTenant); // Ajout d'un nouveau locataire
        }
        console.log("this.tenants");
        console.log(this.tenants);
      }
    });
  }

  public removeTenant(tenant: any): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: '400px',
      data: {
        title: 'Confirm Action',
        message: 'Are you sure you want to remove this tenant?'
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        const index: number = this.tenants.indexOf(tenant);
        if (index !== -1) {
          this.tenants.splice(index, 1); // Suppression du locataire
        }
      }
    });
  }

}
