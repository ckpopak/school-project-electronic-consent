import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api';

export default function ParentCampaignsPage() {
  const [campaigns, setCampaigns] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    api
      .listCampaigns()
      .then(setCampaigns)
      .catch((e) => setError(e.message));
  }, []);

  if (error) {
    return <p className="error">{error}</p>;
  }

  return (
    <section className="card">
      <h2>Consent campaigns</h2>
      <p className="muted">Open a school-specific link (same idea as scanning a QR code).</p>
      <table className="table">
        <thead>
          <tr>
            <th>School</th>
            <th>Campaign</th>
            <th>Deadline</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {campaigns.map((c) => (
            <tr key={c.id}>
              <td>{c.schoolName}</td>
              <td>{c.title}</td>
              <td>{new Date(c.deadlineAt).toLocaleDateString()}</td>
              <td>
                <Link to={c.consentUrlPath}>Open</Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
