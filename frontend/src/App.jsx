import { Link, Navigate, Route, Routes } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { api, clearStoredUser, getStoredUser, setStoredUser } from './api';
import ConsentPage from './pages/ConsentPage';
import ParentCampaignsPage from './pages/ParentCampaignsPage';
import SchoolPage from './pages/SchoolPage';

function Layout({ user, onLogout, children }) {
  return (
    <div className="app">
      <header className="header">
        <div>
          <p className="eyebrow">Demo only — fictional school</p>
          <h1>Electronic Consent</h1>
        </div>
        <nav>
          <Link to="/">Home</Link>
          {user?.role === 'PARENT' && <Link to="/parent/campaigns">Campaigns</Link>}
          {user?.role === 'SCHOOL' && <Link to="/school">School portal</Link>}
          {!user && <Link to="/login">Log in</Link>}
          {user && (
            <button type="button" className="linkish" onClick={onLogout}>
              Log out ({user.displayName})
            </button>
          )}
        </nav>
      </header>
      <main>{children}</main>
    </div>
  );
}

function HomePage() {
  const user = getStoredUser();
  return (
    <section className="card hero">
      <h2>Photo consent workflow (showcase)</h2>
      <p>
        Inspired by public-sector e-consent patterns: school-specific link, parent selects a child,
        submits before deadline, school downloads the consented list.
      </p>
      <table className="table demo-accounts-table">
        <thead>
          <tr>
            <th>Role</th>
            <th>Email</th>
            <th>Password</th>
            <th>Notes</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Parent</td>
            <td>parent1@demo.local</td>
            <td>demo123</td>
            <td>Chris &amp; Sam — Chris already has LIMITED consent on promo campaign</td>
          </tr>
          <tr>
            <td>Parent</td>
            <td>parent2@demo.local</td>
            <td>demo123</td>
            <td>Taylor, Jamie, Parker — Parker has withdrawn submission (can resubmit)</td>
          </tr>
          <tr>
            <td>Parent</td>
            <td>parent3@demo.local</td>
            <td>demo123</td>
            <td>Avery &amp; Quinn (KG)</td>
          </tr>
          <tr>
            <td>Parent</td>
            <td>parent4@demo.local</td>
            <td>demo123</td>
            <td>Blake &amp; Sky</td>
          </tr>
          <tr>
            <td>Parent</td>
            <td>parent5@demo.local</td>
            <td>demo123</td>
            <td>River (Summit SS grad campaign)</td>
          </tr>
          <tr>
            <td>School</td>
            <td>school@riverside.demo</td>
            <td>demo123</td>
            <td>Riverside Primary — 3 campaigns</td>
          </tr>
          <tr>
            <td>School</td>
            <td>school@harbour.demo</td>
            <td>demo123</td>
            <td>Harbour View KG</td>
          </tr>
          <tr>
            <td>School</td>
            <td>school@summit.demo</td>
            <td>demo123</td>
            <td>Summit Secondary</td>
          </tr>
        </tbody>
      </table>
      <p className="muted">
        Sample QR paths:{' '}
        <Link to="/c/riverside-photo-2027">riverside-photo-2027</Link>,{' '}
        <Link to="/c/harbour-openday-2027">harbour-openday-2027</Link>,{' '}
        <Link to="/c/summit-grad-2027">summit-grad-2027</Link>,{' '}
        <Link to="/c/riverside-closed-demo">riverside-closed-demo</Link> (expired)
      </p>
      {!user && (
        <p>
          <Link className="button" to="/login">
            Log in to start
          </Link>
        </p>
      )}
      {user?.role === 'PARENT' && (
        <p>
          <Link className="button" to="/parent/campaigns">
            Browse campaigns
          </Link>
        </p>
      )}
      {user?.role === 'SCHOOL' && (
        <p>
          <Link className="button" to="/school">
            Open school portal
          </Link>
        </p>
      )}
    </section>
  );
}

function LoginPage() {
  const [email, setEmail] = useState('parent1@demo.local');
  const [password, setPassword] = useState('demo123');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function onSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const user = await api.login(email, password);
      setStoredUser(user);
      window.location.href = user.role === 'SCHOOL' ? '/school' : '/parent/campaigns';
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="card narrow">
      <h2>Demo log in</h2>
      <form onSubmit={onSubmit} className="form">
        <label>
          Email
          <input value={email} onChange={(e) => setEmail(e.target.value)} autoComplete="username" />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
          />
        </label>
        {error && <p className="error">{error}</p>}
        <button type="submit" disabled={loading}>
          {loading ? 'Signing in…' : 'Sign in'}
        </button>
      </form>
    </section>
  );
}

function RequireRole({ role, children }) {
  const user = getStoredUser();
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (role && user.role !== role) {
    return <Navigate to="/" replace />;
  }
  return children;
}

export default function App() {
  const [user, setUser] = useState(getStoredUser());

  useEffect(() => {
    api.health().catch(() => {});
  }, []);

  function logout() {
    clearStoredUser();
    setUser(null);
    window.location.href = '/';
  }

  useEffect(() => {
    const onStorage = () => setUser(getStoredUser());
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, []);

  return (
    <Layout
      user={user}
      onLogout={() => {
        logout();
      }}
    >
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/parent/campaigns"
          element={
            <RequireRole role="PARENT">
              <ParentCampaignsPage />
            </RequireRole>
          }
        />
        <Route
          path="/c/:token"
          element={
            <RequireRole role="PARENT">
              <ConsentPage />
            </RequireRole>
          }
        />
        <Route
          path="/school"
          element={
            <RequireRole role="SCHOOL">
              <SchoolPage />
            </RequireRole>
          }
        />
      </Routes>
    </Layout>
  );
}
