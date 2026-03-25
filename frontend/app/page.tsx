'use client';

import { useState } from 'react';

export default function Home() {
  const [personalCode, setPersonalCode] = useState('');
  const [loanAmount, setLoanAmount] = useState(2000);
  const [loanPeriod, setLoanPeriod] = useState(12);

  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async () => {
    setError(null);
    setResult(null);

    try {
      const res = await fetch('http://localhost:8080/api/decision', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ personalCode, loanAmount, loanPeriod }),
      })

      if (!res.ok) {
        const error = await res.json();
        setError(JSON.stringify(error));
        return;
      }

      const data = await res.json();
      setResult(data);
    } catch (err) {
      setError('Server error.');
    }
  };

    return (
    <div className="p-10 max-w-xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Loan Decision</h1>

      <div className="flex flex-col gap-4">
        <input
          placeholder="Personal Code"
          value={personalCode}
          onChange={(e) => setPersonalCode(e.target.value)}
          className="border p-2"
        />

        <input
          type="number"
          value={loanAmount}
          min={2000}
          max={10000}
          onChange={(e) => setLoanAmount(Number(e.target.value))}
          className="border p-2"
        />

        <input
          type="number"
          value={loanPeriod}
          min={12}
          max={60}
          onChange={(e) => setLoanPeriod(Number(e.target.value))}
          className="border p-2"
        />

        <button
          onClick={handleSubmit}
          className="bg-blue-500 text-white p-2"
        >
          Calculate
        </button>
      </div>

      {error && (
        <div className="mt-4 text-red-500">
          Error: {error}
        </div>
      )}

      {result && (
        <div className="mt-6 border p-4">
          <p>Decision: {result.decision}</p>
          <p>Amount: {result.approvedAmount ?? '-'}</p>
          <p>Period: {result.approvedPeriod ?? '-'}</p>
        </div>
      )}
    </div>
  );
}
