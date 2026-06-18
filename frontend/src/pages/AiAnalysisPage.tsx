import { useState } from 'react';
import { api } from '../api/api';
import type { AiAnalysisResponse } from '../types';

export function AiAnalysisPage() {
  const [jobDescription, setJobDescription] = useState('');
  const [analysis, setAnalysis] = useState<AiAnalysisResponse | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleAnalyse() {
    setLoading(true);
    setError('');
    try {
      setAnalysis(await api.analyseJobDescription(jobDescription));
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Could not analyse job description');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section>
      <div className="page-header">
        <h2>Job Description Analysis</h2>
      </div>
      <textarea
        className="jd-textarea"
        value={jobDescription}
        onChange={(event) => setJobDescription(event.target.value)}
        placeholder="Paste a job description here..."
      />
      <button type="button" onClick={handleAnalyse} disabled={loading || !jobDescription.trim()}>
        {loading ? 'Analysing...' : 'Analyse'}
      </button>
      {error && <p className="error">{error}</p>}
      {analysis && (
        <div className="analysis-grid">
          <div className="result-panel">
            <h3>{analysis.roleType}</h3>
            <p>{analysis.summary}</p>
            <p>
              Cloud related: <strong>{analysis.cloudRelated ? 'Yes' : 'No'}</strong>
            </p>
            <p>
              AI related: <strong>{analysis.aiRelated ? 'Yes' : 'No'}</strong>
            </p>
          </div>
          <ResultList title="Required skills" items={analysis.requiredSkills} />
          <ResultList title="Preferred skills" items={analysis.preferredSkills} />
          <ResultList title="Responsibilities" items={analysis.responsibilities} />
          <ResultList title="Suggested projects" items={analysis.suggestedProjects} />
        </div>
      )}
    </section>
  );
}

function ResultList({ title, items }: { title: string; items: string[] }) {
  return (
    <div className="result-panel">
      <h3>{title}</h3>
      {items.length ? (
        <ul>
          {items.map((item) => (
            <li key={item}>{item}</li>
          ))}
        </ul>
      ) : (
        <p>None detected.</p>
      )}
    </div>
  );
}
