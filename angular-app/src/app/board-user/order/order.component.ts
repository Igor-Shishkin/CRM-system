import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from 'src/app/_services/order.service';
import { Order } from 'src/entities/Order';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css']
})
export class OrderComponent implements OnInit{
orderId = -1;
order!: Order;

constructor(
  private router: Router ,
  private route: ActivatedRoute,
  private orderService: OrderService
) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.orderId = +params['id'];
    });
    console.log('orderID: ' + this.orderId)
    this.orderService.getOrder(this.orderId).subscribe({
      next: data => {
        this.order = data;
      }, error: err => {
        console.log(err)
      }
    })
  }

}
