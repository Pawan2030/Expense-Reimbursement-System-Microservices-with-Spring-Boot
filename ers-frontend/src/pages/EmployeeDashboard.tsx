import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { createReimbursement, deleteReimbursement, listByEmployee, type ReimbursementResponseDTO } from '../api/expense';
import ReimbursementForm from '../components/ReimbursementForm';
import ReimbursementTable from '../components/ReimbursementTable';
import type { AxiosError } from 'axios';

export default function EmployeeDashboard() {
  const { claims } = useAuth();
  const employeeId = claims?.employeeId;
  const managerId = claims?.managerId;
  const [reims, setReims] = useState<ReimbursementResponseDTO[]>([]);

 
useEffect(() => {
  // Prevent back navigation from cached pages
  window.history.replaceState(null, '', window.location.href);
  window.onpopstate = () => {
    window.history.go(1);
  };
}, []);


  useEffect(() => {
    if (!employeeId) return;
    listByEmployee(employeeId)
      .then(setReims)
      .catch((e: unknown) => {
        const err = e as AxiosError<{ message?: string }>;
        alert(err.response?.data?.message || 'Failed to load');
      });
  }, [employeeId]);

  async function onCreate(payload: { employeeId: number; managerId: number; amount: number; description: string }) {
    try {
      const created = await createReimbursement(payload);
      setReims(prev => [created, ...prev]);
    } catch (e: unknown) {
      const err = e as AxiosError<{ message?: string }>;
      alert(err.response?.data?.message || 'Create failed');
    }
  }

  async function onDelete(id: number) {
    if (!employeeId) return; // guard
    try {
      await deleteReimbursement(id, employeeId);
      setReims(prev => prev.filter(r => r.id !== id));
    } catch (e: unknown) {
      const err = e as AxiosError<{ message?: string }>;
      alert(err.response?.data?.message || 'Delete failed (only PENDING allowed)');
    }
  }

  return (
    <div className="page">
      <h2>Employee Dashboard</h2>

      {employeeId && managerId && (
        <ReimbursementForm employeeId={employeeId} managerId={managerId} onSubmit={onCreate} />
      )}

      <section className="card">
        <h3>My Reimbursements</h3>
        <ReimbursementTable data={reims} onDelete={onDelete} />
      </section>
    </div>
  );
}
