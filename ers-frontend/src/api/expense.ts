import axios from 'axios';
import { getAuthToken } from '../context/AuthContext';

const EXP_BASE = import.meta.env.VITE_EXPENSE_BASE_URL as string;

function authHeaders() {
  const token = getAuthToken();
  return { Authorization: `Bearer ${token}` };
}

export type ReimbursementResponseDTO = {
  id: number;
  employeeId: number;
  managerId: number;
  amount: string;
  description: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt: string;
  updatedAt: string;
  actionAt?: string;
};

export async function listByEmployee(employeeId: number): Promise<ReimbursementResponseDTO[]> {
  const res = await axios.get(`${EXP_BASE}/api/reimbursements?employeeId=${employeeId}`, {
    headers: authHeaders()
  });
  return res.data;
}

export async function listByManager(managerId: number): Promise<ReimbursementResponseDTO[]> {
  const res = await axios.get(`${EXP_BASE}/api/reimbursements?managerId=${managerId}`, {
    headers: authHeaders()
  });
  return res.data;
}

export async function createReimbursement(payload: {
  employeeId: number;
  managerId: number;
  amount: number;
  description: string;
}): Promise<ReimbursementResponseDTO> {
  const res = await axios.post(`${EXP_BASE}/api/reimbursements`, payload, { headers: authHeaders() });
  return res.data;
}

export async function approveReimbursement(id: number): Promise<ReimbursementResponseDTO> {
  const res = await axios.put(`${EXP_BASE}/api/reimbursements/${id}/approve`, {}, { headers: authHeaders() });
  return res.data;
}

export async function rejectReimbursement(id: number): Promise<ReimbursementResponseDTO> {
  const res = await axios.put(`${EXP_BASE}/api/reimbursements/${id}/reject`, {}, { headers: authHeaders() });
  return res.data;
}

// Backend currently requires actorId query param for delete
export async function deleteReimbursement(id: number, actorId: number): Promise<void> {
  await axios.delete(`${EXP_BASE}/api/reimbursements/${id}?actorId=${actorId}`, { headers: authHeaders() });
}
