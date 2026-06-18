import { useEffect, useState } from 'react';
import { api } from '../api/api';
import { ApplicationForm } from '../components/ApplicationForm';
import type { ApplicationStatus, JobApplication, JobApplicationPayload } from '../types';

const statuses: ApplicationStatus[] = ['NOT_APPLIED', 'APPLIED', 'ONLINE_TEST', 'INTERVIEW', 'OFFER', 'REJECTED'];

export function ApplicationsPage() {
  const [applications, setApplications] = useState<JobApplication[]>([]);
  const [editing, setEditing] = useState<JobApplication | null>(null);
  const [statusFilter, setStatusFilter] = useState('');
  const [keyword, setKeyword] = useState('');
  const [error, setError] = useState('');

  async function loadApplications() {
    setError('');
    try {
      if (keyword.trim()) {
        setApplications(await api.searchApplications(keyword.trim()));
      } else {
        setApplications(await api.getApplications(statusFilter ? (statusFilter as ApplicationStatus) : undefined));
      }
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Could not load applications');
    }
  }

  useEffect(() => {
    loadApplications();
  }, [statusFilter]);

  async function handleSubmit(payload: JobApplicationPayload) {
    if (editing) {
      await api.updateApplication(editing.id, payload);
      setEditing(null);
    } else {
      await api.createApplication(payload);
    }
    await loadApplications();
  }

  async function handleDelete(id: number) {
    if (!window.confirm('Delete this application?')) {
      return;
    }
    await api.deleteApplication(id);
    await loadApplications();
  }

  return (
    <section>
      <div className="page-header">
        <h2>Applications</h2>
      </div>
      <div className="toolbar">
        <select value={statusFilter} onChange={(event) => setStatusFilter(event.target.value)}>
          <option value="">All statuses</option>
          {statuses.map((status) => (
            <option key={status} value={status}>
              {status}
            </option>
          ))}
        </select>
        <input value={keyword} onChange={(event) => setKeyword(event.target.value)} placeholder="Search keyword" />
        <button type="button" onClick={loadApplications}>
          Search
        </button>
      </div>
      {error && <p className="error">{error}</p>}
      <ApplicationForm initialValue={editing} onSubmit={handleSubmit} onCancel={editing ? () => setEditing(null) : undefined} />
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Company</th>
              <th>Role</th>
              <th>Location</th>
              <th>Status</th>
              <th>Applied</th>
              <th>Deadline</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {applications.map((application) => (
              <tr key={application.id}>
                <td>{application.companyName}</td>
                <td>{application.roleTitle}</td>
                <td>{application.location}</td>
                <td>{application.status}</td>
                <td>{application.applicationDate || '-'}</td>
                <td>{application.deadline || '-'}</td>
                <td className="row-actions">
                  <button type="button" onClick={() => setEditing(application)}>
                    Edit
                  </button>
                  <button type="button" className="danger-button" onClick={() => handleDelete(application.id)}>
                    Delete
                  </button>
                </td>
              </tr>
            ))}
            {applications.length === 0 && (
              <tr>
                <td colSpan={7}>No applications yet.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}
