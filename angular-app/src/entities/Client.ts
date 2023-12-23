import { Order } from "./Order";

export class Client {
    id : number | undefined;
    fullName : string | undefined;
    email : string | undefined;
    phoneNumber : string | undefined;
    isClient : boolean | undefined;
    address: string | undefined;
    numberOfOrders: number | undefined;
    orders: Order[] | undefined;
}

