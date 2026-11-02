import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../api';

export default function ConsentPage() {
  const { token } = useParams();
  const [campaign, setCampaign] = useState(null);
  const [students, setStudents] = useState([]);
  const [submissions, setSubmissions] = useState([]);
  const [studentId, setStudentId] = useState('');
  const [decision, setDecision] = useState('YES');
  const [scopeNote, setScopeNote] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  async function refresh(campaignId) {
    const [stu, subs] = await Promise.all([
      api.students(),
      api.mySubmissions(campaignId),
    ]);
    setStudents(stu);
    setSubmissions(subs);
    if (!studentId && stu.length) {
      setStudentId(String(stu[0].id));
    }
  }

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const c = await api.campaignByToken(token);
        if (cancelled) return;
        setCampaign(c);
        await refresh(c.id);
      } catch (err) {
        setError(err.message);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [token]);

  async function onSubmit(e) {
    e.preventDefault();
    setError('');
    setMessage('');
    try {
      await api.submit({
        campaignId: campaign.id,
        studentId: Number(studentId),
        decision,
        scopeNote: decision === 'LIMITED' ? scopeNote : null,
      });
      setMessage('Consent submitted successfully.');
      await refresh(campaign.id);
    } catch (err) {
      setError(err.message);
    }
  }

  async function onWithdraw(id) {
    setError('');
    setMessage('');
    try {
      await api.withdraw(id);
      setMessage('Submission withdrawn — you may submit again.');
      await refresh(campaign.id);
    } catch (err) {
      setError(err.message);
    }
  }

  if (loading) {
    return <p className="muted">Loading campaign…</p>;
  }
  if (!campaign) {
    return <p className="error">{error || 'Campaign not found.'}</p>;
  }

  const activeByStudent = Object.fromEntries(
    submissions.filter((s) => s.status === 'SUBMITTED').map((s) => [s.studentId, s]),
  );

  return (
    <div className="stack">
      <section className="card">
        <p className="eyebrow">{campaign.schoolName}</p>
        <h2>{campaign.title}</h2>
        <p>{campaign.description}</p>
        <p className="muted">
          Open: {new Date(campaign.startsAt).toLocaleString()} — Deadline:{' '}
          {new Date(campaign.deadlineAt).toLocaleString()}
        </p>
      </section>

      <section className="card">
        <h3>Submit consent</h3>
        <form onSubmit={onSubmit} className="form">
          <label>
            Child
            <select value={studentId} onChange={(e) => setStudentId(e.target.value)}>
              {students.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.fullName} ({s.className}-{s.classNumber})
                </option>
              ))}
            </select>
          </label>
          <fieldset>
            <legend>Decision</legend>
            <label className="inline">
              <input
                type="radio"
                name="decision"
                value="YES"
                checked={decision === 'YES'}
                onChange={() => setDecision('YES')}
              />
              Yes — all listed uses
            </label>
            <label className="inline">
              <input
                type="radio"
                name="decision"
                value="LIMITED"
                checked={decision === 'LIMITED'}
                onChange={() => setDecision('LIMITED')}
              />
              Limited — specify below
            </label>
            <label className="inline">
              <input
                type="radio"
                name="decision"
                value="NO"
                checked={decision === 'NO'}
                onChange={() => setDecision('NO')}
              />
              No — do not use my child&apos;s photo
            </label>
          </fieldset>
          {decision === 'LIMITED' && (
            <label>
              Limited scope (e.g. website only)
              <input value={scopeNote} onChange={(e) => setScopeNote(e.target.value)} />
            </label>
          )}
          {error && <p className="error">{error}</p>}
          {message && <p className="success">{message}</p>}
          <button type="submit">Submit e-consent</button>
        </form>
      </section>

      <section className="card">
        <h3>Your submissions</h3>
        {students.length === 0 && <p className="muted">No linked children on this demo account.</p>}
        <ul className="list">
          {students.map((s) => {
            const sub = activeByStudent[s.id];
            return (
              <li key={s.id}>
                <strong>{s.fullName}</strong>
                {sub ? (
                  <>
                    {' '}
                    — {sub.decision}
                    {sub.scopeNote ? ` (${sub.scopeNote})` : ''}{' '}
                    <button type="button" className="linkish" onClick={() => onWithdraw(sub.id)}>
                      Withdraw
                    </button>
                  </>
                ) : (
                  <span className="muted"> — not submitted</span>
                )}
              </li>
            );
          })}
        </ul>
      </section>
    </div>
  );
}
