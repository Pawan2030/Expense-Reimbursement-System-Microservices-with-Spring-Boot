import axios from 'axios';

const AUTH_BASE = import.meta.env.VITE_AUTH_BASE_URL as string;

export async function loginApi(username: string, password: string): Promise<string> {
  const res = await axios.post(`${AUTH_BASE}/api/auth/login`, { username, password });
  return res.data.token as string;
}

export type RegisterPayload = {
  username: string;
  password: string;
  role: 'EMPLOYEE' | 'MANAGER';
  employeeId?: number; // required for employee accounts
  managerId?: number;  // required when role = EMPLOYEE
};

export async function registerApi(payload: RegisterPayload): Promise<void> {
  await axios.post(`${AUTH_BASE}/api/auth/register`, payload);
}
