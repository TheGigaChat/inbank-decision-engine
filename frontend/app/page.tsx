'use client';

import { useState, useEffect } from 'react';

type Config = {
  minAmount: number;
  maxAmount: number;
  minPeriod: number;
  maxPeriod: number;
};

const defaultConfig: Config = {
  minAmount: 2000,
  maxAmount: 10000,
  minPeriod: 12,
  maxPeriod: 60,
};

type DecisionResponse = {
  decision: 'POSITIVE' | 'NEGATIVE';
  approvedAmount: number | null;
  approvedPeriod: number | null;
};

type ValidationErrorResponse = Record<string, string>;

export default function HomePage() {
  const [personalCode, setPersonalCode] = useState('49002010965');
  const [loanAmount, setLoanAmount] = useState(4100);
  const [loanPeriod, setLoanPeriod] = useState(35);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState<DecisionResponse | null>(null);

  const [config, setConfig] = useState<Config | null>(null);
  const effectiveConfig = config ?? defaultConfig;

  // Load the config from backend in the start of the application
  useEffect(() => {
    fetch("http://localhost:8080/api/config")
      .then(res => res.json())
      .then(data => setConfig(data));
  }, []);

  const amountProgress = ((loanAmount - effectiveConfig.minAmount) / (effectiveConfig.maxAmount - effectiveConfig.minAmount)) * 100;
  const periodProgress = ((loanPeriod - effectiveConfig.minPeriod) / (effectiveConfig.maxPeriod - effectiveConfig.minPeriod)) * 100;

  const handleCalculate = async () => {
    setLoading(true);
    setError('');
    setResult(null);

    try {
      const response = await fetch('http://localhost:8080/api/decision', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          personalCode,
          loanAmount,
          loanPeriod,
        }),
      });

      if (!response.ok) {
        const errorData: ValidationErrorResponse = await response.json();
        setError(Object.values(errorData).join(', '));
        setLoading(false);
        return;
      }

      const data: DecisionResponse = await response.json();
      setResult(data);
    } catch {
      setError('Unable to connect to the server. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="page-shell">
      <div className="loan-card">
        <header className="header-block">
          <h1 className="page-title">Loan Decision</h1>
          <p className="page-subtitle">Check your loan eligibility instantly</p>
        </header>

        <section className="form-section">
          {/* Personal Code Input */}
          <div className="field-group">
            <label htmlFor="personalCode" className="field-label">
              Personal Code
            </label>
            <input
              id="personalCode"
              type="text"
              value={personalCode}
              onChange={(e) => setPersonalCode(e.target.value)}
              placeholder="Enter personal code"
              className={`text-input ${error ? 'input-error' : ''}`}
              maxLength={11}
            />
          </div>

          {/* Loan Amount Slider */}
          <div className="field-group">
            <label htmlFor="loanAmount" className="field-label">
              Loan amount
            </label>
            <div className="value-display">{loanAmount.toLocaleString('et-EE')} €</div>

            <input
              id="loanAmount"
              type="range"
              min={effectiveConfig.minAmount}
              max={effectiveConfig.maxAmount}
              step={100}
              value={loanAmount}
              onChange={(e) => setLoanAmount(Number(e.target.value))}
              className="slider"
              style={{
                background: `linear-gradient(to right, var(--color-primary) 0%, var(--color-primary) ${amountProgress}%, var(--color-border) ${amountProgress}%, var(--color-border) 100%)`,
              }}
            />

            <div className="range-labels">
              <span>2 000 €</span>
              <span>10 000 €</span>
            </div>
          </div>

          {/* Loan Period Slider */}
          <div className="field-group">
            <label htmlFor="loanPeriod" className="field-label">
              Loan period
            </label>
            <div className="value-display">{loanPeriod} months</div>

            <input
              id="loanPeriod"
              type="range"
              min={effectiveConfig.minPeriod}
              max={effectiveConfig.maxPeriod}
              step={1}
              value={loanPeriod}
              onChange={(e) => setLoanPeriod(Number(e.target.value))}
              className="slider"
              style={{
                background: `linear-gradient(to right, var(--color-primary) 0%, var(--color-primary) ${periodProgress}%, var(--color-border) ${periodProgress}%, var(--color-border) 100%)`,
              }}
            />

            <div className="range-labels">
              <span>12 months</span>
              <span>60 months</span>
            </div>
          </div>

          {/* Calculate Button */}
          <button
            type="button"
            onClick={handleCalculate}
            disabled={loading}
            className="primary-button"
          >
            {loading ? 'Calculating...' : 'Calculate'}
          </button>

          {error && <p className="error-text">{error}</p>}
        </section>

        {/* Result Card */}
        {result && (
          <section
            className={`result-card ${
              result.decision === 'POSITIVE' ? 'result-approved' : 'result-rejected'
            }`}
          >
            <div
              className="result-header"
            >
              <div
                className={`result-icon ${
                  result.decision === 'POSITIVE' ? 'result-icon-approved' : 'result-icon-rejected'
                }`}
                aria-hidden="true"
              >
                {result.decision === 'POSITIVE' ? (
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path
                      d="M16.25 5.625L7.5 14.375L3.75 10.625"
                      stroke="white"
                      strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                  </svg>
                ) : (
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path
                      d="M13.75 6.25L6.25 13.75M6.25 6.25L13.75 13.75"
                      stroke="white"
                      strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                  </svg>
                )}
              </div>

              <h2 className="result-title">
                {result.decision === 'POSITIVE' ? 'Approved' : 'Not approved'}
              </h2>
            </div>

            {result.decision === 'POSITIVE' && (
              <div className="result-details">
                <div className="result-row">
                  <span className="result-label">Amount</span>
                  <span className="result-value">
                    {result.approvedAmount?.toLocaleString('et-EE')} €
                  </span>
                </div>

                <div className="result-row">
                  <span className="result-label">Period</span>
                  <span className="result-value">{result.approvedPeriod} months</span>
                </div>
              </div>
            )}
          </section>
        )}
      </div>
    </main>
  );
}
