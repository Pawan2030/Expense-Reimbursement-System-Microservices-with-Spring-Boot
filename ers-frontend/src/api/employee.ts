import axios from 'axios';
import { getAuthToken } from '../context/AuthContext';

const EMP_BASE = import.meta.env.VITE_EMPLOYEE_BASE_URL as string;

function authHeaders() {
  const token = getAuthToken();
  return { Authorization: `Bearer ${token}` };
}

export type EmployeeResponseDTO = {
  id: number;
  username: string;
  role: string;
  managerId: number;
  employeeId: number;
  createdAt: string;
  updatedAt: string;
};

export async function listEmployeesByManager(managerId: number): Promise<EmployeeResponseDTO[]> {
  const res = await axios.get(`${EMP_BASE}/api/employees?managerId=${managerId}`, {
    headers: authHeaders()
  });
  return res.data;
}

export async function deleteEmployee(id: number): Promise<void> {
  await axios.delete(`${EMP_BASE}/api/employees/${id}`, { headers: authHeaders() });
}
