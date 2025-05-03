import {Component, OnInit, inject} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {AppService} from '@services/app.service';
import {DomHandlerService} from '@services/dom-handler.service';
import {Settings, SettingsService} from '@services/settings.service';
import {customers} from '../../common/data/customers';
import {TenantDialogComponent} from './tenant-dialog/tenant-dialog.component';
import {ConfirmDialogComponent} from '@shared-components/confirm-dialog/confirm-dialog.component';
import {MatCardModule} from '@angular/material/card';
import {FlexLayoutModule} from '@ngbracket/ngx-layout';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {NgxPaginationModule} from 'ngx-pagination';
import {MatDividerModule} from '@angular/material/divider';
import {PipesModule} from '../../theme/pipes/pipes.module';
import {MatTooltipModule} from '@angular/material/tooltip';
import {RentDialogComponent} from "../rent/rent-dialog/rent-dialog.component";
import {CommonModule} from "@angular/common";
import {TenantService} from "@services/tenant.service";
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {TenantInfoDialogComponent} from "./tenant-info-info-dialog/tenant-info-dialog.component";
import {MatChipsModule} from "@angular/material/chips";

@Component({
  selector: 'app-tenants',
  imports: [
    CommonModule,
    FlexLayoutModule,
    MatCardModule,
    MatChipsModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    MatTooltipModule,
    NgxPaginationModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    CommonModule,
    FormsModule,
    MatInputModule,
    PipesModule
  ],
  templateUrl: './tenants.component.html',
  styleUrl: './tenants.component.scss'
})
export class TenantsComponent implements OnInit {

  filteredTenants: any[] = []; // Filtered list for display
  searchQuery: string = ''; // Search query
  public customers: any[] = [];
  public stores = [
    {id: 1, name: 'Store 1'},
    {id: 2, name: 'Store 2'}
  ]
  public tenants: any[] = [];
  // public page: number = 1;
  // public count: number = 5;
  public countries: any[] = [];
  public page: any;
  public count = 6;
  domHandlerService = inject(DomHandlerService);
  public settings: Settings;

  constructor(
    public appService: AppService,
    public tenantService: TenantService,
    public dialog: MatDialog,
    public settingsService: SettingsService) {
    this.settings = this.settingsService.settings;
  }

  ngOnInit(): void {
    this.getTenantData();

  }

  ngOnChanges(): void {
    this.filterTenants();
  }

filterTenants(): void {
  if (!this.searchQuery.trim()) {
    // Reset to the full list if the search query is empty
    this.filteredTenants = [...this.tenants];
    return;
  }

  // Filter tenants based on the search query
  this.filteredTenants = this.tenants.filter(tenant =>
    tenant.userName?.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
    tenant.housingUnitName?.toLowerCase().includes(this.searchQuery.toLowerCase())
  );
}

  private getTenantData() {
    this.tenantService.getTenants().subscribe({
      next: (tenants) => {
        this.tenants = tenants;
        this.filteredTenants = this.tenants;
      },
      error: (err) => {
        console.error('Failed to load tenants:', err);
      }
    })
  }

  public onPageChanged(event: any) {
    this.page = event;
    this.domHandlerService.winScroll(0, 0);
  }

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
      data: data,
      panelClass: ['theme-dialog'],
      autoFocus: false,
      direction: (this.settings.rtl) ? 'rtl' : 'ltr'
    });

    dialogRef.afterClosed().subscribe(tenant => {
      if (tenant) {
        const formattedTenant = {
          id: this.tenants.length + 1, // Generate a new ID
          // name: usersDial.find(user => user.id === tenant.userId)?.name || 'Unknown',
          // apartment: logementsDial.find(logement => logement.id === tenant.housingUnitId)?.name || 'Unknown',
          entryDate: tenant.moveInDate,
          deposit: tenant.securityDeposit,
          paymentStatus: tenant.status
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

  public openDetailTenantDialog(data: any): void {
    const dialogRef = this.dialog.open(TenantInfoDialogComponent, {
      data: data,
      panelClass: ['theme-dialog'],
      autoFocus: false,
      // width: '600px',
      // height: 'auto',
      // width: '80%', // 80% of the window width
      // height: '70%' ,// 70% of the window height
      direction: (this.settings.rtl) ? 'rtl' : 'ltr'
    });

    dialogRef.afterClosed().subscribe(tenant => {
      if (tenant) {
        const formattedTenant = {
          id: this.tenants.length + 1, // Generate a new ID
          // name: usersDial.find(user => user.id === tenant.userId)?.name || 'Unknown',
          // apartment: logementsDial.find(logement => logement.id === tenant.housingUnitId)?.name || 'Unknown',
          entryDate: tenant.moveInDate,
          deposit: tenant.securityDeposit,
          paymentStatus: tenant.status
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
