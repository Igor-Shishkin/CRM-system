import { ItemForCalculation } from "./ItemForCalculation";

export class Order {
    orderId: number | undefined;
    realNeed: string | undefined;
    estimateBudged: number | undefined;
    isProjectApproved: boolean | undefined;
    wasMeetingInOffice: boolean | undefined;
    resultPrice: number | undefined;
    hasBeenPaid: boolean | undefined;
    address: string | undefined;
    isCalculationPromised: boolean | undefined;
    isProjectShown: string | undefined;
    isCalculationShown: string | undefined;
    dateOfCreation: Date | undefined;
    dateOfLastChange: Date | undefined;
    isMeasurementsTaken: boolean | undefined;
    isMeasurementOffered: boolean | undefined;
    isAgreementPrepared: boolean | undefined;
    isAgreementSigned: boolean | undefined;

    clientFullName: string | undefined;
    clientPhoneNumber: string | undefined;
    clientId: number | undefined;
    clientEmail:string | undefined;

    calculations: ItemForCalculation[] | undefined;
} 


