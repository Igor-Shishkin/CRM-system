export interface Order {
    orderId: number;
    realNeed: string,
    estimateBudged: number,
    isProjectApproved: boolean,
    wasMeetingInOffice: boolean,
    resultPrice: number,
    hasBeenPaid: boolean,
    address: string,
    isCalculationPromised: boolean,
    isProjectShown: string,
    isCalculationShown: string,
    dateOfCreation: Date,
    clientId: number;
    measurementsTaken: boolean;
    measurementOffered: boolean;
    hasAgreementPrepared: boolean;
}