import type {
  AiAnalysisResponse,
  ApplicationStatus,
  AuthResponse,
  DashboardStats,
  JobApplication,
  JobApplicationPayload
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('token');
  const headers = new Headers(options.headers);

  if (!headers.has('Content-Type') && options.body) {
    headers.set('Content-Type', 'application/json');
  }
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers
  });

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`;
    try {
      const errorBody = await response.json();
      message = errorBody.error ?? errorBody.message ?? message;
    } catch {
      // Keep the generic message when the backend returns no JSON body.
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export const api = {
  register(email: string, password: string) {
    return request<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
  },
  login(email: string, password: string) {
    return request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
  },
  getApplications(status?: ApplicationStatus) {
    const query = status ? `?status=${status}` : '';
    return request<JobApplication[]>(`/applications${query}`);
  },
  searchApplications(keyword: string) {
    return request<JobApplication[]>(`/applications/search?keyword=${encodeURIComponent(keyword)}`);
  },
  createApplication(payload: JobApplicationPayload) {
    return request<JobApplication>('/applications', {
      method: 'POST',
      body: JSON.stringify(payload)
    });
  },
  updateApplication(id: number, payload: JobApplicationPayload) {
    return request<JobApplication>(`/applications/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload)
    });
  },
  deleteApplication(id: number) {
    return request<void>(`/applications/${id}`, {
      method: 'DELETE'
    });
  },
  getDashboardStats() {
    return request<DashboardStats>('/dashboard/stats');
  },
  analyseJobDescription(jobDescription: string) {
    return request<AiAnalysisResponse>('/ai/analyse-job-description', {
      method: 'POST',
      body: JSON.stringify({ jobDescription })
    });
  }
};
