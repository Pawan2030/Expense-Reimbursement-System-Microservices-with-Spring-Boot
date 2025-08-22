import { useState } from 'react';

type Props = {
  employeeId?: number;
  managerId?: number;
  onSubmit: (payload: { employeeId: number; managerId: number; amount: number; description: string }) => Promise<void>;
};

export default function ReimbursementForm({ employeeId, managerId, onSubmit }: Props) {
  const [amount, setAmount] = useState<number>(0);
  const [description, setDescription] = useState('');

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!employeeId || !managerId) return alert('Invalid user session');
    if (amount <= 0 || !description.trim()) return alert('Amount and description required');
    await onSubmit({ employeeId, managerId, amount, description });
    setAmount(0);
    setDescription('');
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <h3>New Reimbursement</h3>
      <div className="row">
        <label>Employee ID</label>
        <input value={employeeId ?? ''} disabled />
      </div>
      <div className="row">
        <label>Manager ID</label>
        <input value={managerId ?? ''} disabled />
      </div>
      <div className="row">
        <label>Amount</label>
        <input
          type="number"
          step="0.01"
          value={amount}
          onChange={e => setAmount(parseFloat(e.target.value))}
        />
      </div>
      <div className="row">
        <label>Description</label>
        <input value={description} onChange={e => setDescription(e.target.value)} />
      </div>
      <button type="submit">Submit</button>
    </form>
  );
}
