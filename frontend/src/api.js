const USER_KEY = 'econsent-demo-user';

export function getStoredUser() {
  const raw = sessionStorage.getItem(USER_KEY);
  return raw ? JSON.parse(raw) : null;
}

export function setStoredUser(user) {
  sessionStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function clearStoredUser() {
  sessionStorage.removeItem(USER_KEY);
}

async function request(path, options = {}) {
  const user = getStoredUser();
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  };
  if (user?.userId) {
    headers['X-Demo-User-Id'] = String(user.userId);
  }
  const res = await fetch(path, { ...options, headers });
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) {
    throw new Error(data?.error || res.statusText);
  }
  return data;
}

export const api = {
  login: (email, password) =>
    request('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),
  health: () => request('/api/health'),
  listCampaigns: () => request('/api/parent/campaigns'),
  campaignByToken: (token) => request(`/api/parent/campaigns/token/${encodeURIComponent(token)}`),
  students: () => request('/api/parent/students'),
  mySubmissions: (campaignId) => request(`/api/parent/campaigns/${campaignId}/submissions`),
  submit: (body) =>
    request('/api/parent/submissions', { method: 'POST', body: JSON.stringify(body) }),
  withdraw: (id) => request(`/api/parent/submissions/${id}`, { method: 'DELETE' }),
  schoolCampaigns: () => request('/api/school/campaigns'),
  schoolSubmissions: (id) => request(`/api/school/campaigns/${id}/submissions`),
};
