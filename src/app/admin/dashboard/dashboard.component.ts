import { Component } from '@angular/core';
import { TilesComponent } from './tiles/tiles.component';
import { InfoCardsComponent } from './info-cards/info-cards.component';
import { MontlySalesComponent } from './montly-sales/montly-sales.component';
import { LatestOrdersComponent } from './latest-orders/latest-orders.component';
import { AnalyticsComponent } from './analytics/analytics.component';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import {DashboardService} from "@services/dashboard.service";

@Component({
    selector: 'app-dashboard',
    imports: [
        TilesComponent,
        InfoCardsComponent,
        MontlySalesComponent,
        LatestOrdersComponent,
        AnalyticsComponent,
        FlexLayoutModule,
        MatCardModule,
        MatIconModule
    ],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {

  dashboardData: any;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.fetchDashboardData();
  }

  private fetchDashboardData(): void {
    this.dashboardService.getDashboards().subscribe(
      (data) => {
        this.dashboardData = data;
        console.log('Dashboard data:', this.dashboardData);
      },
      (error) => {
        console.error('Error fetching dashboard data:', error);
      }
    );
  }
}
