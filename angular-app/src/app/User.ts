import { Lead } from "./Lead";

export interface User {
    id : number;
    username : string;
    email : string;
    roles : string [];
    clientsNumber : number;
}
