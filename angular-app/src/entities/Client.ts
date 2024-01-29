import { Order } from "./Order";

export class Client {
    clientId : number | undefined;
    fullName : string | undefined;
    email : string | undefined;
    phoneNumber : string | undefined;
    status : string | undefined;
    address: string | undefined;
    numberOfOrders: number | undefined;
    numberOfPaidOrders: number | undefined;
    orders: Order[] | undefined;
}

