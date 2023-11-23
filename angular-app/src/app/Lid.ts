import { Order } from "./Order";

export interface Lid {
    id : number;
    name : string;
    surname : string;
    email : string;
    phoneNumber : string;
    isClient : boolean;
    orders: Order[];
}