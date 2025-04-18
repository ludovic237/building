import { Component, OnInit, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AppService } from '@services/app.service';
import { DomHandlerService } from '@services/dom-handler.service';
import { Settings, SettingsService } from '@services/settings.service';
import { customers } from '../../common/data/customers';
import { RentDialogComponent } from './rent-dialog/rent-dialog.component';
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
import {Rent} from "../../model/data";

@Component({
    selector: 'app-rents',
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
    templateUrl: './rents.component.html'
})
export class RentsComponent implements OnInit {

  public rents: any[] = [];
  public locataires: any[] = [
    { id: 1, name: 'John Doe' }, // Matches locataireId: 1 in rents
    { id: 2, name: 'Jane Smith' }, // Matches locataireId: 2 in rents
    { id: 3, name: 'Alice Johnson' },
    { id: 4, name: 'Bob Brown' }
  ];
  public logements = [
    { id: 101, name: 'Apartment A' }, // Matches logementId: 101 in rents
    { id: 102, name: 'Apartment B' }, // Matches logementId: 102 in rents
    { id: 103, name: 'Apartment C' },
    { id: 104, name: 'Apartment D' }
  ];
  public page: number = 1;
  public count: number = 5;


  constructor(public appService: AppService, public dialog: MatDialog, public settingsService: SettingsService) {

  }

  ngOnInit(): void {
    // Mock data for rents
    this.rents = [
      {
        id: 1,
        logementId: 101,
        locataireId: 1,
        mois: 'January',
        annee: 2023,
        montant: 500,
        statut: 'payé',
        datePaiement: '2023-01-15'
      },
      {
        id: 2,
        logementId: 102,
        locataireId: 2,
        mois: 'February',
        annee: 2023,
        montant: 700,
        statut: 'non payé',
        datePaiement: null
      }
    ];
  }

  public onPageChanged(event: any) {
    this.page = event;

  }

  public openRentDialog(data: Rent | null): void {
    const dialogRef = this.dialog.open(RentDialogComponent, {
      data: {
        rent: data,
        locataires: this.locataires,
        logements: this.logements
      },
      panelClass: ['theme-dialog'],
      autoFocus: false
    });

    dialogRef.afterClosed().subscribe((rent: Rent) => {
      if (rent) {
        const index = this.rents.findIndex(r => r.id === rent.id);
        if (index !== -1) {
          this.rents[index] = rent; // Update existing rent
        } else {
          rent.id = this.rents.length + 1; // Assign new ID
          this.rents.push(rent); // Add new rent
        }
      }
    });
  }

  public removeRent(rent: Rent): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: '400px',
      data: {
        title: 'Confirm Action',
        message: 'Are you sure you want to remove this rent?'
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        this.rents = this.rents.filter(r => r.id !== rent.id);
      }
    });
  }

  getTenantName(locataireId: number): string {
    const tenant = this.locataires.find(l => l.id === locataireId);
    return tenant ? tenant.name : 'Unknown';
  }

  getHousingUnitName(logementId: number): string {
    const logement = this.logements.find(l => l.id === logementId);
    return logement ? logement.name : 'Unknown';
  }
}
