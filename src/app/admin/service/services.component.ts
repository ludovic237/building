import { Component, OnInit, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AppService } from '@services/app.service';
import { DomHandlerService } from '@services/dom-handler.service';
import { Settings, SettingsService } from '@services/settings.service';
import { customers } from '../../common/data/customers';
import { ServiceDialogComponent } from './service-dialog/service-dialog.component';
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
import {Service} from "../../model/data";

@Component({
    selector: 'app-services',
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
    templateUrl: './services.component.html'
})
export class ServicesComponent implements OnInit {

  public services: any[] = [];
  public locataires: any[] = [
    { id: 1, name: 'John Doe' },
    { id: 2, name: 'Jane Smith' }
  ];
  public page: number = 1;
  public count: number = 5;

  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {
    // Mock data for services
    this.services = [
      {
        id: 1,
        nom: 'Internet',
        description: 'High-speed internet connection',
        prixMensuel: 50
      },
      {
        id: 2,
        nom: 'Cleaning',
        description: 'Weekly cleaning service',
        prixMensuel: 100
      }
    ];
  }

  public openServiceDialog(data: any): void {
    const dialogRef = this.dialog.open(ServiceDialogComponent, {
      data: {
        service: data
      },
      panelClass: ['theme-dialog'],
      autoFocus: false
    });

    dialogRef.afterClosed().subscribe((service: any) => {
      if (service) {
        const index = this.services.findIndex(s => s.id === service.id);
        if (index !== -1) {
          this.services[index] = service; // Update existing service
        } else {
          service.id = this.services.length + 1; // Assign new ID
          this.services.push(service); // Add new service
        }
      }
    });
  }

  public removeService(service: any): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: '400px',
      data: {
        title: 'Confirm Action',
        message: 'Are you sure you want to remove this service?'
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        this.services = this.services.filter(s => s.id !== service.id);
      }
    });
  }
}
