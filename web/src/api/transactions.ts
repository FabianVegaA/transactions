import axios from "axios";

type Transaction = {
  trade: string;
  userName: string;
  amount: number;
};

export async function fetchTransactions(): Promise<Transaction[]> {
  return axios.get("/api/transactions").then((res) => res.data);
}

export async function createOrUpdateTransaction(
  transaction: Transaction,
): Promise<Transaction> {
  return axios.post("/api/transactions", transaction).then((res) => res.data);
}
