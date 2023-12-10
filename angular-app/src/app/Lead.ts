import { Order } from "./Order";

export interface Lead {
    id : number;
    fullName : string;
    email : string;
    phoneNumber : string;
    isClient : boolean;
    address: string;
    numberOfOrders: number;
    orders: Order[];
}

