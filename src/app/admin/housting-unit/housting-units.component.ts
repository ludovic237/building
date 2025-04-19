import {Component, OnInit, inject} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {AppService} from '@services/app.service';
import {DomHandlerService} from '@services/dom-handler.service';
import {Settings, SettingsService} from '@services/settings.service';
import {customers} from '../../common/data/customers';
import {HoustingUnitDialogComponent} from './housting-unit-dialog/housting-unit-dialog.component';
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
import {HoustingUnit} from "../../model/data";
import {HoustingUnitService} from "@services/housting-unit.service";
import {MatSnackBar} from "@angular/material/snack-bar";

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
  public selectedHousingUnit: HoustingUnit | null = null;

  // public page: number = 1;
  // public count: number = 5;
  public countries: any[] = [];
  public page: any;
  public count = 6;
  domHandlerService = inject(DomHandlerService);
  public settings: Settings;

  constructor(
    public houstingUnitService: HoustingUnitService,
    public snackBar: MatSnackBar,
    public appService: AppService,
    public dialog: MatDialog,
    public settingsService: SettingsService) {
    this.settings = this.settingsService.settings;
  }

  ngOnInit(): void {
    this.housingUnits = [];
    this.getHousingUnits()
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
      if (houstingUnit) {
        this.houstingUnitService.createHousingUnit(houstingUnit).subscribe({
          next: (response) => {
            console.log('Housing unit created successfully:', response);
            this.snackBar.open('Housing unit created successfully!', '×', {
              panelClass: 'success',
              verticalPosition: 'top',
              duration: 3000
            });
          },
          error: (err) => {
            console.error('Error creating housing unit:', err);
            this.snackBar.open('Failed to create housing unit.', '×', {
              panelClass: 'error',
              verticalPosition: 'top',
              duration: 3000
            });
          }
        });
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

  public getHousingUnits() {
    this.houstingUnitService.getHousingUnits().subscribe(data => {
      this.housingUnits = data.map(unit => ({
        ...unit,
        tenants: unit.tenants ?? [] // Ensure tenants is an empty array if null
      }));
    });
  }

  public openHoustingUnitDialogUpdate(id: number): void {
    this.houstingUnitService.getHousingUnitById(id).subscribe(data => {
      const dialogRef = this.dialog.open(HoustingUnitDialogComponent, {
        data: data,
        panelClass: ['theme-dialog'],
        autoFocus: false,
        direction: (this.settings.rtl) ? 'rtl' : 'ltr'
      });

      dialogRef.afterClosed().subscribe(houstingUnit => {
        this.getHousingUnits()
      });
    });
  }


}
