import { Order } from "./Order";

export interface Lid {
    id : number;
    fullName : string;
    email : string;
    phoneNumber : string;
    isClient : boolean;
    address: string;
    orders: Order[];
}