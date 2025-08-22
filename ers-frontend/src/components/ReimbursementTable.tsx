import type { ReimbursementResponseDTO } from '../api/expense';

type Props = {
  data: ReimbursementResponseDTO[];
  showActions?: boolean;
  onApprove?: (id: number) => void;
  onReject?: (id: number) => void;
  onDelete?: (id: number) => void;
};

export default function ReimbursementTable({ data, showActions, onApprove, onReject, onDelete }: Props) {
  return (
    <table className="table">
      <thead>
      <tr>
        <th>ID</th>
        <th>Employee</th>
        <th>Manager</th>
        <th>Amount</th>
        <th>Description</th>
        <th>Status</th>
        <th>Created</th>
        <th>Action</th>
      </tr>
      </thead>
      <tbody>
      {data.map(r => (
        <tr key={r.id}>
          <td>{r.id}</td>
          <td>{r.employeeId}</td>
          <td>{r.managerId}</td>
          <td>{r.amount}</td>
          <td>{r.description}</td>
          <td>{r.status}</td>
          <td>{new Date(r.createdAt).toLocaleString()}</td>
          <td>
            {showActions ? (
              <>
                {onApprove && <button onClick={() => onApprove(r.id)}>Approve</button>}
                {onReject && <button onClick={() => onReject(r.id)}>Reject</button>}
              </>
            ) : onDelete ? (
              <button onClick={() => onDelete(r.id)}>Delete</button>
            ) : null}
          </td>
        </tr>
      ))}
      </tbody>
    </table>
  );
}
