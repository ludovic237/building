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
// import * as moment from 'moment';

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
    // const startDate = moment().startOf('day').format('YYYY-MM-DDTHH:mm:ss'); // Start of the day at 00:00
    // const endDate = moment().endOf('day').format('YYYY-MM-DDTHH:mm:ss'); // End of the day at 23:59

    const now = new Date();
    // const startDate = new Date(now.setHours(0, 0, 0, 0)).toISOString(); // Début de la journée à 00:00
    // const endDate = new Date(now.setHours(23, 59, 59, 999)).toISOString(); // Fin de la journée à 23:59

    // const startDate = new Date(now.setHours(0, 0, 0, 0)).toISOString().slice(0, -1); // Remove 'Z'
    // const endDate = new Date(now.setHours(23, 59, 59, 999)).toISOString().slice(0, -1); // Remove 'Z'

    // const startDate = new Date(now.setHours(0, 0, 0, 0))
    //   .toISOString()
    //   .slice(0, 19); // Format as 'yyyy-MM-ddTHH:mm:ss'
    // const endDate = new Date(now.setHours(23, 59, 59, 999))
    //   .toISOString()
    //   .slice(0, 19); // Format as 'yyyy-MM-ddTHH:mm:ss'

    const startDate = new Date(now.getFullYear(), 0, 1)
      .toISOString()
      .slice(0, 19); // Format as 'yyyy-MM-ddTHH:mm:ss'
    const endDate = new Date(now.getFullYear(), 11, 31, 23, 59, 59, 999)
      .toISOString()
      .slice(0, 19); // Format as 'yyyy-MM-ddTHH:mm:ss'

    this.dashboardService.getDashboards(startDate, endDate).subscribe(
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
