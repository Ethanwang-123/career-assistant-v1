import { useEffect, useState } from 'react';
import { api } from '../api/api';
import type { DashboardStats } from '../types';

export function DashboardPage() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api
      .getDashboardStats()
      .then(setStats)
      .catch((exception) => setError(exception instanceof Error ? exception.message : 'Could not load dashboard'));
  }, []);

  return (
    <section>
      <div className="page-header">
        <h2>Dashboard</h2>
      </div>
      {error && <p className="error">{error}</p>}
      <div className="stats-grid">
        <div className="stat-card">
          <span>Total applications</span>
          <strong>{stats?.totalApplications ?? '-'}</strong>
        </div>
        <div className="stat-card">
          <span>Interviews</span>
          <strong>{stats?.totalInterviews ?? '-'}</strong>
        </div>
        <div className="stat-card">
          <span>Offers</span>
          <strong>{stats?.totalOffers ?? '-'}</strong>
        </div>
        <div className="stat-card">
          <span>Rejected</span>
          <strong>{stats?.totalRejected ?? '-'}</strong>
        </div>
      </div>
    </section>
  );
}
