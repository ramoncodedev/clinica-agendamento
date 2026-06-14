async function call(method, path, body) {
  const opts = { method, headers: { 'Content-Type': 'application/json' } };
  if (body !== undefined) opts.body = JSON.stringify(body);

  const res = await fetch(path, opts);
  if (res.status === 204) return null;

  const text = await res.text();
  let data;
  try { data = JSON.parse(text); } catch { data = text; }

  if (!res.ok) {
    if (data && typeof data === 'object') {
      const msg = data.erro || data.message || Object.values(data).join('; ');
      throw new Error(msg);
    }
    throw new Error(text || `Erro ${res.status}`);
  }

  return data;
}

const api = {
  pacientes: {
    list:   ()    => call('GET',  '/pacientes'),
    create: (d)   => call('POST', '/pacientes', d),
  },
  nutricionistas: {
    list:   ()    => call('GET',  '/nutricionistas'),
    create: (d)   => call('POST', '/nutricionistas', d),
  },
  servicos: {
    list:   ()    => call('GET',  '/servicos'),
    create: (d)   => call('POST', '/servicos', d),
  },
  agendamentos: {
    list:     (nid, data) => call('GET',   `/agendamentos?nutricionistaId=${nid}&data=${data}`),
    create:   (d)         => call('POST',  '/agendamentos', d),
    cancelar: (id)        => call('PATCH', `/agendamentos/${id}/cancelar`),
  },
};