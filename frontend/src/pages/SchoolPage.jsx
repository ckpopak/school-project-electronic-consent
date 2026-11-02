import { useEffect, useState } from 'react';
import { api, getStoredUser } from '../api';

export default function SchoolPage() {
  const user = getStoredUser();
  const [campaigns, setCampaigns] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    api
      .schoolCampaigns()
      .then((list) => {
        setCampaigns(list);
        if (list.length) setSelectedId(list[0].id);
      })
      .catch((e) => setError(e.message));
  }, []);

  useEffect(() => {
    if (!selectedId) return;
    api
      .schoolSubmissions(selectedId)
      .then(setRows)
      .catch((e) => setError(e.message));
  }, [selectedId]);

  const selected = campaigns.find((c) => c.id === selectedId);

  function downloadCsv() {
    const userId = user?.userId;
    const url = `/api/school/campaigns/${selectedId}/export.csv`;
    fetch(url, { headers: { 'X-Demo-User-Id': String(userId) } })
      .then((res) => {
        if (!res.ok) throw new Error('Export failed');
        return res.blob();
      })
      .then((blob) => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = 'consented-students.csv';
        a.click();
      })
      .catch((e) => setError(e.message));
  }

  return (
    <div className="stack">
      <section className="card">
        <p className="eyebrow">School staff portal</p>
        <h2>{user?.schoolName || 'School'}</h2>
        <p className="muted">View submissions and export the consented student list (demo CSV).</p>
      </section>

      {error && <p className="error">{error}</p>}

      <section className="card">
        <label>
          Campaign
          <select
            value={selectedId ?? ''}
            onChange={(e) => setSelectedId(Number(e.target.value))}
          >
            {campaigns.map((c) => (
              <option key={c.id} value={c.id}>
                {c.title}
              </option>
            ))}
          </select>
        </label>
        {selected && (
          <p className="muted">
            Parent link:{' '}
            <code>
              {window.location.origin}
              {selected.consentUrlPath}
            </code>
          </p>
        )}
        <button type="button" onClick={downloadCsv} disabled={!selectedId}>
          Download CSV export
        </button>
      </section>

      <section className="card">
        <h3>Submitted consents ({rows.length})</h3>
        <table className="table">
          <thead>
            <tr>
              <th>Student</th>
              <th>Class</th>
              <th>Decision</th>
              <th>Scope</th>
              <th>Submitted</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.id}>
                <td>{r.studentName}</td>
                <td>
                  {r.className}-{r.classNumber}
                </td>
                <td>{r.decision}</td>
                <td>{r.scopeNote || '—'}</td>
                <td>{new Date(r.submittedAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {rows.length === 0 && <p className="muted">No submissions yet — try the parent demo account.</p>}
      </section>
    </div>
  );
}
