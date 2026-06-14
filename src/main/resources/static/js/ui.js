function toast(msg, type = 'ok') {
  const icons = { ok: '✓', err: '✕', warn: '!' };
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.textContent = icons[type] + '  ' + msg;
  el.onclick = () => el.remove();
  document.getElementById('toasts').appendChild(el);
  setTimeout(() => el.remove(), 4000);
}

function openModal(title, html) {
  document.getElementById('modal-title').textContent = title;
  document.getElementById('modal-body').innerHTML = html;
  document.getElementById('overlay').classList.add('open');
}

function closeModal() {
  document.getElementById('overlay').classList.remove('open');
}

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('modal-x').onclick = closeModal;
  document.getElementById('overlay').addEventListener('click', e => {
    if (e.target === document.getElementById('overlay')) closeModal();
  });
});