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
import {SubscriptionService} from "@services/subscription.service";

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
  public locataires: any[] = [];
  public services: any[] = [];
  public page: number = 1;
  public count: number = 5;

  constructor(public dialog: MatDialog,
              public subscriptionService:SubscriptionService) {}

  ngOnInit(): void {
    this.getAllSubscription();

  }

  private getAllSubscription() {
    this.subscriptionService.getSubscriptions().subscribe({
      next: (data) => {
        this.subscriptions = data;
        console.log('Get subscription:', data);
      },
      error: (err) => {
        console.error('Error  subscription:', err);
      }
    });
  }

  public openSubscriptionDialog(data: any): void {
    const dialogRef = this.dialog.open(SubscriptionDialogComponent, {
      data: data,
      panelClass: ['theme-dialog'],
      autoFocus: false
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      console.log("subscription");
      this.getAllSubscription();
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
      this.getAllSubscription();
      // if (dialogResult) {
      //   this.subscriptions = this.subscriptions.filter(s => s.id !== subscription.id);
      // }
    });
  }

  getTenantName(tenantId: number): string {
    const tenant = this.locataires.find(l => l.id === tenantId);
    return tenant ? tenant.name : 'Unknown';
  }

  getServiceName(serviceId: number): string {
    const service = this.services.find(s => s.id === serviceId);
    return service ? service.nom : 'Unknown';
  }


}
