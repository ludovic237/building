import { Component, OnInit, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AppService } from '@services/app.service';
import { DomHandlerService } from '@services/dom-handler.service';
import { Settings, SettingsService } from '@services/settings.service';
import { customers } from '../../common/data/customers';
import { HoustingUnitDialogComponent } from './housting-unit-dialog/housting-unit-dialog.component';
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
import {HousingUnit} from "../../model/data";

@Component({
    selector: 'app-housting-units',
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
    templateUrl: './housting-units.component.html'
})
export class HoustingUnitsComponent implements OnInit {
  public housingUnits: any[] = [];
  public selectedHousingUnit: HousingUnit | null = null;

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
    this.housingUnits = [
      {
        id: 1,
        numeroAppartement: 'A101',
        etage: 1,
        superficie: 50,
        adresse: '123 Rue Principale',
        type: 'Studio',
        tenants: [
          { id: 1, name: 'John Doe', entryDate: '2023-01-15', deposit: 500, paymentStatus: 'Paid' },
          { id: 2, name: 'Jane Smith', entryDate: '2023-02-01', deposit: 300, paymentStatus: 'Partial' }
        ]
      },
      {
        id: 2,
        numeroAppartement: 'B202',
        etage: 2,
        superficie: 75,
        adresse: '456 Avenue Centrale',
        type: 'T2',
        tenants: []
      }
    ];
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
        // const index: number = this.customers.indexOf(customer);
        // if (index !== -1) {
        //   this.customers.splice(index, 1);
        // }
      }
    });
  }


  public openHoustingUnitDialog(data: any): void {
    const dialogRef = this.dialog.open(HoustingUnitDialogComponent, {
      data: {
        customer: data,
      },
      panelClass: ['theme-dialog'],
      autoFocus: false,
      direction: (this.settings.rtl) ? 'rtl' : 'ltr'
    });

    dialogRef.afterClosed().subscribe(houstingUnit => {
      houstingUnit.tenants=[];
      if (houstingUnit) {
        if (data) {
          // Modification du locataire
          const index: number = this.housingUnits.indexOf(data);
          if (index !== -1) {
            this.housingUnits[index] = houstingUnit;
          }
        } else {
          // Ajout d'un nouveau locataire
          this.housingUnits.push(houstingUnit);
        }
      }
    });
  }

  public removeHoustingUnit(tenant: any): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: '400px',
      data: {
        title: 'Confirm Action',
        message: 'Are you sure you want to remove this tenant?'
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        const index: number = this.housingUnits.indexOf(tenant);
        if (index !== -1) {
          this.housingUnits.splice(index, 1); // Suppression du locataire
        }
      }
    });
  }

}
