
export interface HistoryMessage {
   messageText: string;
    dateOfCreation: Date;
    isImportant: boolean;
    isDone: boolean;
    note: string;
    lidId: number;
}

const historyWithDateString: HistoryMessage[] = [
    {
      messageText: "Message 1",
      dateOfCreation: new Date("2023-12-10T00:00:00"),
      isImportant: true,
      isDone: false,
      note: "Note 1",
      lidId: 1,
    },
  ];
    