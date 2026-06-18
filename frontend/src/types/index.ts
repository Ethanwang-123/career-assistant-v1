export type ApplicationStatus =
  | 'NOT_APPLIED'
  | 'APPLIED'
  | 'ONLINE_TEST'
  | 'INTERVIEW'
  | 'OFFER'
  | 'REJECTED';

export interface JobApplication {
  id: number;
  companyName: string;
  roleTitle: string;
  location?: string;
  status: ApplicationStatus;
  applicationDate?: string;
  deadline?: string | null;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export type JobApplicationPayload = Omit<JobApplication, 'id' | 'createdAt' | 'updatedAt'>;

export interface AuthResponse {
  token: string;
  tokenType: string;
  email: string;
}

export interface DashboardStats {
  totalApplications: number;
  totalInterviews: number;
  totalOffers: number;
  totalRejected: number;
}

export interface AiAnalysisResponse {
  roleType: string;
  requiredSkills: string[];
  preferredSkills: string[];
  responsibilities: string[];
  cloudRelated: boolean;
  aiRelated: boolean;
  summary: string;
  suggestedProjects: string[];
}
