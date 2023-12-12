import { Client } from "./Client";

export interface User {
    id : number;
    username : string;
    email : string;
    roles : string [];
    clientsNumber : number;
}
