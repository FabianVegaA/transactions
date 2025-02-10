import React, { useMemo } from "react";
import "./App.css";
import {
  QueryClient,
  QueryClientProvider,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import zod from "zod";
import { v4 as uuidv4 } from "uuid";
import { useForm } from "react-hook-form";
import { Box, Button } from "@mui/material";
import TextField from "@mui/material/TextField";
import Paper from "@mui/material/Paper";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/DeleteOutlined";
import SaveIcon from "@mui/icons-material/Save";
import CancelIcon from "@mui/icons-material/Close";
import { DataGrid, GridColDef, GridActionsCellItem } from "@mui/x-data-grid";

import axios from "axios";
import { match } from "ts-pattern";
import { toast, ToastContainer } from "react-toastify";

type Transaction = {
  id: number;
  trade: string;
  userName: string;
  amount: number;
  createdAt: Date;
};
type TransactionRequest = Omit<Transaction, "id" | "createdAt">;

const schemaTransactionResponse = zod.object({
  id: zod.number(),
  trade: zod.string(),
  userName: zod.string(),
  amount: zod.number(),
  createdAt: zod.string().transform((val) => new Date(val)),
});

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <TransactionManager />
    </QueryClientProvider>
  );
}

function TransactionManager() {
  const notifyError = () =>
    toast("An error occurred while making the request, please try again", {
      type: "error",
    });
  const [formMode, setFormMode] = React.useState<"add" | "edit">("add");
  const [selectedTransaction, setSelectedTransaction] =
    React.useState<Transaction | null>(null);

  const queryClient = useQueryClient();
  const client = useMemo(() => {
    return axios.create({
      baseURL: "http://localhost:8080/api",
      headers: {
        "X-api-key": uuidv4(), // For each connection we generate a new API key
        "Content-Type": "application/json",
        Accept: "application/json",
      },
    });
  }, []);
  const {
    handleSubmit,
    register,
    formState: { isLoading },
    setValue,
    resetField,
  } = useForm<TransactionRequest>();

  const getTransactions = useQuery({
    queryKey: ["transactions"],
    queryFn: async () => {
      return client.get("/transactions").then((res) => {
        return res.data.map((transaction: Transaction) =>
          schemaTransactionResponse.parse(transaction),
        );
      });
    },
  });

  const addTransaction = useMutation({
    mutationFn: async (transaction: TransactionRequest) =>
      client.post("/transactions", transaction).then((res) => {
        return schemaTransactionResponse.parse(res.data);
      }),
    onError: () => notifyError,
    onSuccess: () => queryClient.invalidateQueries(["transactions"]),
  });

  const updateTransaction = useMutation({
    mutationFn: async (transaction: {
      id: number;
      trade: string;
      userName: string;
      amount: number;
    }) =>
      client.put(`/transactions/${transaction.id}`, transaction).then((res) => {
        return schemaTransactionResponse.parse(res.data);
      }),
    onError: () => notifyError,
    onSuccess: () => queryClient.invalidateQueries(["transactions"]),
  });

  const deleteTransaction = useMutation({
    mutationFn: async (id: number) => client.delete(`/transactions/${id}`),
    onError: () => notifyError,
    onSuccess: () => queryClient.invalidateQueries(["transactions"]),
  });

  const columns: GridColDef[] = [
    { field: "id", headerName: "ID", width: 90 },
    { field: "trade", headerName: "Trade", width: 150 },
    { field: "userName", headerName: "User Name", width: 150 },
    { field: "amount", headerName: "Amount", width: 150 },
    { field: "createdAt", headerName: "Created At", width: 150 },
    {
      field: "actions",
      headerName: "Actions",
      type: "actions",
      getActions: ({ row: transaction }) => {
        return [
          <GridActionsCellItem
            icon={<EditIcon />}
            label="Edit"
            className="textPrimary"
            color="inherit"
            onClick={() => {
              setFormMode("edit");
              setValue("trade", transaction.trade);
              setValue("userName", transaction.userName);
              setValue("amount", transaction.amount);
              setSelectedTransaction(transaction);
            }}
          />,
          <GridActionsCellItem
            icon={<DeleteIcon />}
            label="Delete"
            onClick={() => deleteTransaction.mutate(transaction.id)}
            color="inherit"
          />,
        ];
      },
    },
  ];

  const paginationModel = { page: 0, pageSize: 5 };

  const cleanAndToggleForm = () => {
    resetField("trade");
    resetField("userName");
    resetField("amount");
    setFormMode("add");
  };

  return (
    <Box
      sx={{
        display: "grid",
        gap: 2,
        padding: 2,
        gridTemplateColumns: "auto",
      }}
    >
      <Paper
        sx={{
          display: "grid",
          justifyContent: "center",
        }}
      >
        <Box
          component="form"
          noValidate
          autoComplete="off"
          onSubmit={handleSubmit((data) =>
            match(formMode)
              .with("add", () => addTransaction.mutate(data))
              .with("edit", () => {
                updateTransaction.mutate({
                  id: selectedTransaction?.id!,
                  trade: data.trade,
                  userName: data.userName,
                  amount: data.amount,
                });
                cleanAndToggleForm();
              })
              .exhaustive(),
          )}
          sx={{
            display: "grid",
            padding: 2,
            gap: 1,
            maxWidth: 400,
            gridTemplateColumns: { xs: "auto", md: "1fr 1fr" },
            gridTemplateRows: { xs: "1fr 1fr 1fr 1fr", md: "1fr 1fr 1fr" },
          }}
        >
          <TextField
            {...register("trade", { required: true })}
            id="trade"
            label="Trade"
            required
          />
          <TextField
            {...register("userName", { required: true })}
            id="userName"
            label="User Name"
            required
          />
          <TextField
            {...register("amount", { required: true })}
            id="amount"
            label="Amount"
            required
            type={"number"}
            sx={{ gridColumn: "1 / -1" }}
          />
          {match(formMode)
            .with("add", () => (
              <Button
                type="submit"
                disabled={isLoading}
                variant="contained"
                sx={{ gridColumn: "1 / -1", rowStart: 4 }}
              >
                <AddIcon />
                Add
              </Button>
            ))
            .with("edit", () => (
              <>
                <Button
                  type="submit"
                  disabled={isLoading}
                  variant="contained"
                  sx={{ rowStart: 4, gridColumn: "1 / 2" }}
                >
                  <SaveIcon />
                  Save
                </Button>
                <Button
                  type="reset"
                  variant="contained"
                  sx={{ rowStart: 4, gridColumn: "2 / -1" }}
                  onClick={cleanAndToggleForm}
                >
                  <CancelIcon />
                  Cancel
                </Button>
              </>
            ))
            .exhaustive()}
        </Box>
      </Paper>
      <Paper
        sx={{
          display: "grid",
          gap: 2,
          padding: 2,
        }}
      >
        <DataGrid
          rows={getTransactions.data || []}
          columns={columns}
          initialState={{ pagination: { paginationModel } }}
          pageSizeOptions={[5, 10]}
          sx={{ border: 0 }}
        />
      </Paper>
      <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />
    </Box>
  );
}

export default App;
