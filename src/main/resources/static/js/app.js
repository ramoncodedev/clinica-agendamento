const pages = {
  agendamentos:   pageAgendamentos,
  pacientes:      pagePacientes,
  nutricionistas: pageNutricionistas,
  servicos:       pageServicos,
};

function render(section) {
  pages[section]?.();
}

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('today-label').textContent = new Date().toLocaleDateString('pt-BR', {
    weekday: 'long', day: 'numeric', month: 'long'
  });

  document.querySelectorAll('.nav-item').forEach(el => {
    el.addEventListener('click', () => {
      document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
      el.classList.add('active');
      render(el.dataset.section);
    });
  });

  render('agendamentos');
});