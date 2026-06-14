const pages = {
  agendamentos:   pageAgendamentos,
  pacientes:      pagePacientes,
  nutricionistas: pageNutricionistas,
  servicos:       pageServicos,
};

function render(section) {
  pages[section]?.();
}

function closeSidebar() {
  document.getElementById('sidebar').classList.remove('open');
  document.getElementById('hamburger').classList.remove('open');
  document.getElementById('mobile-overlay').classList.remove('open');
}

function toggleSidebar() {
  const open = document.getElementById('sidebar').classList.toggle('open');
  document.getElementById('hamburger').classList.toggle('open', open);
  document.getElementById('mobile-overlay').classList.toggle('open', open);
}

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('today-label').textContent = new Date().toLocaleDateString('pt-BR', {
    weekday: 'long', day: 'numeric', month: 'long'
  });

  document.getElementById('hamburger').addEventListener('click', toggleSidebar);
  document.getElementById('mobile-overlay').addEventListener('click', closeSidebar);

  document.querySelectorAll('.nav-item').forEach(el => {
    el.addEventListener('click', () => {
      document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
      el.classList.add('active');
      render(el.dataset.section);
      closeSidebar();
    });
  });

  render('agendamentos');
});