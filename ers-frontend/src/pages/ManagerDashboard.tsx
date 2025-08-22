import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { listByManager, approveReimbursement, rejectReimbursement, type ReimbursementResponseDTO } from '../api/expense';
import { listEmployeesByManager, type EmployeeResponseDTO } from '../api/employee';
import ReimbursementTable from '../components/ReimbursementTable';

export default function ManagerDashboard() {
  const { claims } = useAuth();
  const [reims, setReims] = useState<ReimbursementResponseDTO[]>([]);
  const [emps, setEmps] = useState<EmployeeResponseDTO[]>([]);
  const managerId = claims?.managerId ?? claims?.employeeId; // some managers may use employeeId as managerId

  useEffect(() => {
  // Prevent back navigation from cached pages
  window.history.replaceState(null, '', window.location.href);
  window.onpopstate = () => {
    window.history.go(1);
  };
}, []);

  useEffect(() => {
    if (!managerId) return;
    (async () => {
      const [r, e] = await Promise.all([
        listByManager(managerId),
        listEmployeesByManager(managerId),
      ]);
      setReims(r);
      setEmps(e);
    })().catch(err => alert(err?.response?.data || 'Failed to load'));
  }, [managerId]);

  async function handleApprove(id: number) {
    try {
      const updated = await approveReimbursement(id);
      setReims(prev => prev.map(r => r.id === id ? updated : r));
    } catch (e: any) {
      alert(e?.response?.data || 'Approve failed');
    }
  }

  async function handleReject(id: number) {
    try {
      const updated = await rejectReimbursement(id);
      setReims(prev => prev.map(r => r.id === id ? updated : r));
    } catch (e: any) {
      alert(e?.response?.data || 'Reject failed');
    }
  }

  return (
    <div className="page">
      <h2>Manager Dashboard</h2>
     <section className="card card22">
       <h3>My Employees</h3>
      <ul className="list">
        {emps.map((e, idx) => (
          <li key={e.id}>
            <span className="index">{idx + 1}.</span>
            <span>{e.username}</span>
            <span>empId: {e.employeeId}</span>
          </li>
        ))}
        {!emps.length && <li>No employees yet.</li>}
      </ul>
</section>


      <section className="card">
        <h3>Reimbursements (My Team)</h3>
        <ReimbursementTable
          data={reims}
          showActions
          onApprove={handleApprove}
          onReject={handleReject}
        />
      </section>
    </div>
  );
}
