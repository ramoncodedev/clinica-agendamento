function esc(s) {
  return String(s ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

// Spring Boot serializa LocalTime/LocalDate como arrays por padrão
function fmtTime(t) {
  if (!t) return '—';
  if (Array.isArray(t)) {
    return String(t[0]).padStart(2,'0') + ':' + String(t[1]).padStart(2,'0');
  }
  return String(t).slice(0, 5);
}

function fmtDate(d) {
  if (!d) return '—';
  if (Array.isArray(d)) {
    return String(d[2]).padStart(2,'0') + '/' + String(d[1]).padStart(2,'0') + '/' + d[0];
  }
  const p = String(d).split('-');
  return p[2] + '/' + p[1] + '/' + p[0];
}

function todayISO() {
  return new Date().toISOString().slice(0, 10);
}

function tomorrowISO() {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  return d.toISOString().slice(0, 10);
}

function emptyState(icon, title, sub) {
  return `<div class="empty">
    <div class="empty-icon">${icon}</div>
    <div class="empty-title">${title}</div>
    <div class="empty-sub">${sub || ''}</div>
  </div>`;
}

function errState(msg) {
  return `<div class="empty">
    <div class="empty-icon">⚠️</div>
    <div class="empty-title">Erro ao carregar dados</div>
    <div class="empty-sub">${esc(msg)}</div>
  </div>`;
}