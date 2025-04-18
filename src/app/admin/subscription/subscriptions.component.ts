import { Component, OnInit, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { customers } from '../../common/data/customers';
import { SubscriptionDialogComponent } from './subscription-dialog/subscription-dialog.component';
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
import {Subscription} from "../../model/data";

@Component({
    selector: 'app-subscriptions',
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
    templateUrl: './subscriptions.component.html'
})
export class SubscriptionsComponent implements OnInit {


  public subscriptions: any[] = [];
  public locataires: any[] = [
    { id: 1, name: 'John Doe' },
    { id: 2, name: 'Jane Smith' }
  ];
  public services: any[] = [
    { id: 1, nom: 'Internet' },
    { id: 2, nom: 'Cleaning' }
  ];
  public page: number = 1;
  public count: number = 5;

  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {
    // Mock data for subscriptions
    this.subscriptions = [
      {
        id: 1,
        locataireId: 1,
        serviceId: 1,
        dateDebut: '2023-01-01',
        dateFin: '2023-12-31',
        statut: 'actif'
      },
      {
        id: 2,
        locataireId: 2,
        serviceId: 2,
        dateDebut: '2023-02-01',
        dateFin: '2023-11-30',
        statut: 'inactif'
      }
    ];
  }

  public openSubscriptionDialog(data: any): void {
    const dialogRef = this.dialog.open(SubscriptionDialogComponent, {
      data: {
        subscription: data,
        locataires: this.locataires,
        services: this.services
      },
      panelClass: ['theme-dialog'],
      autoFocus: false
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      console.log("subscription");
      const subscription = {
        id: result.id || this.subscriptions.length + 1, // Génère un nouvel ID si non défini
        locataireId: result.locataireId,
        serviceId: result.serviceId,
        dateDebut: new Date(result.dateDebut).toISOString().split('T')[0], // Convert to YYYY-MM-DD
        dateFin: new Date(result.dateFin).toISOString().split('T')[0],     // Convert to YYYY-MM-DD
        statut: result.statut
      };
      console.log(subscription);

      if (subscription) {
        const index = this.subscriptions.findIndex(s => s.id === subscription.id);
        if (index !== -1) {
          this.subscriptions[index] = subscription; // Update existing subscription
        } else {
          subscription.id = this.subscriptions.length + 1; // Assign new ID
          this.subscriptions.push(subscription); // Add new subscription
        }
      }
      console.log("new this.subscriptions");
      console.log(this.subscriptions);
    });

  }

  public removeSubscription(subscription: any): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: '400px',
      data: {
        title: 'Confirm Action',
        message: 'Are you sure you want to remove this subscription?'
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        this.subscriptions = this.subscriptions.filter(s => s.id !== subscription.id);
      }
    });
  }

  getTenantName(locataireId: number): string {
    const tenant = this.locataires.find(l => l.id === locataireId);
    return tenant ? tenant.name : 'Unknown';
  }

  getServiceName(serviceId: number): string {
    const service = this.services.find(s => s.id === serviceId);
    return service ? service.nom : 'Unknown';
  }

}
