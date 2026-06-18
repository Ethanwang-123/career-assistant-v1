import { FormEvent, useEffect, useState } from 'react';
import type { ApplicationStatus, JobApplication, JobApplicationPayload } from '../types';

const statuses: ApplicationStatus[] = [
  'NOT_APPLIED',
  'APPLIED',
  'ONLINE_TEST',
  'INTERVIEW',
  'OFFER',
  'REJECTED'
];

const emptyForm: JobApplicationPayload = {
  companyName: '',
  roleTitle: '',
  location: '',
  status: 'APPLIED',
  applicationDate: '',
  deadline: '',
  notes: ''
};

interface ApplicationFormProps {
  initialValue?: JobApplication | null;
  onSubmit: (payload: JobApplicationPayload) => Promise<void>;
  onCancel?: () => void;
}

export function ApplicationForm({ initialValue, onSubmit, onCancel }: ApplicationFormProps) {
  const [form, setForm] = useState<JobApplicationPayload>(emptyForm);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (initialValue) {
      setForm({
        companyName: initialValue.companyName,
        roleTitle: initialValue.roleTitle,
        location: initialValue.location ?? '',
        status: initialValue.status,
        applicationDate: initialValue.applicationDate ?? '',
        deadline: initialValue.deadline ?? '',
        notes: initialValue.notes ?? ''
      });
    } else {
      setForm(emptyForm);
    }
  }, [initialValue]);

  function updateField<K extends keyof JobApplicationPayload>(key: K, value: JobApplicationPayload[K]) {
    setForm((current) => ({ ...current, [key]: value }));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSaving(true);
    try {
      await onSubmit({
        ...form,
        deadline: form.deadline || null,
        applicationDate: form.applicationDate || undefined
      });
      if (!initialValue) {
        setForm(emptyForm);
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <form className="form-grid" onSubmit={handleSubmit}>
      <label>
        Company
        <input value={form.companyName} onChange={(event) => updateField('companyName', event.target.value)} required />
      </label>
      <label>
        Role
        <input value={form.roleTitle} onChange={(event) => updateField('roleTitle', event.target.value)} required />
      </label>
      <label>
        Location
        <input value={form.location} onChange={(event) => updateField('location', event.target.value)} />
      </label>
      <label>
        Status
        <select value={form.status} onChange={(event) => updateField('status', event.target.value as ApplicationStatus)}>
          {statuses.map((status) => (
            <option key={status} value={status}>
              {status}
            </option>
          ))}
        </select>
      </label>
      <label>
        Application date
        <input
          type="date"
          value={form.applicationDate ?? ''}
          onChange={(event) => updateField('applicationDate', event.target.value)}
        />
      </label>
      <label>
        Deadline
        <input type="date" value={form.deadline ?? ''} onChange={(event) => updateField('deadline', event.target.value)} />
      </label>
      <label className="full-width">
        Notes
        <textarea value={form.notes} onChange={(event) => updateField('notes', event.target.value)} />
      </label>
      <div className="form-actions full-width">
        <button type="submit" disabled={saving}>
          {saving ? 'Saving...' : initialValue ? 'Update application' : 'Add application'}
        </button>
        {onCancel && (
          <button type="button" className="secondary-button" onClick={onCancel}>
            Cancel
          </button>
        )}
      </div>
    </form>
  );
}
