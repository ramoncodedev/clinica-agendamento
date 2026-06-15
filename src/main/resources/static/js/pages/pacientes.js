async function pagePacientes() {
  document.getElementById('main').innerHTML = `
    <div class="page-header">
      <div>
        <div class="page-title">Pacientes</div>
        <div class="page-subtitle">Cadastro de pacientes da clínica</div>
      </div>
      <button class="btn btn-primary" id="btn-add-pac">+ Novo Paciente</button>
    </div>
    <div class="page-content">
      <div class="card">
        <div class="card-toolbar">
          <input class="input-field" id="filter-pac" placeholder="Buscar por nome ou CPF…" style="width:280px">
        </div>
        <div id="pac-body"><div class="loading"><div class="spinner"></div> Carregando…</div></div>
      </div>
    </div>`;

  document.getElementById('btn-add-pac').onclick = () => formPaciente();

  try {
    store.pacientes = await api.pacientes.list();
  } catch (e) {
    document.getElementById('pac-body').innerHTML = errState(e.message);
    return;
  }

  renderPacientesTable(store.pacientes);

  document.getElementById('filter-pac').addEventListener('input', e => {
    const q = e.target.value.toLowerCase();
    renderPacientesTable(store.pacientes.filter(p =>
      p.nome.toLowerCase().includes(q) || p.cpf.includes(q)
    ));
  });
}

function renderPacientesTable(list) {
  if (!list.length) {
    document.getElementById('pac-body').innerHTML =
      emptyState('👤', 'Nenhum paciente', 'Cadastre o primeiro paciente');
    return;
  }
  const rows = list.map((p, i) => `
    <tr style="animation-delay:${i * 25}ms">
      <td class="td-main">${esc(p.nome)}</td>
      <td class="td-sub">${esc(p.cpf)}</td>
      <td class="td-sub">${esc(p.email)}</td>
      <td class="td-sub">${esc(p.telefone)}</td>
      <td class="td-actions">
        <button class="btn-icon btn-danger" onclick="deletarPaciente(${p.id})" title="Excluir paciente">🗑️</button>
      </td>
    </tr>`).join('');
  document.getElementById('pac-body').innerHTML = `
    <table>
      <thead><tr><th>Nome</th><th>CPF</th><th>E-mail</th><th>Telefone</th><th></th></tr></thead>
      <tbody>${rows}</tbody>
    </table>`;
}

async function deletarPaciente(id) {
  if (!confirm('Tem certeza que deseja excluir este paciente?')) return;
  try {
    await api.pacientes.delete(id);
    toast('Paciente excluído com sucesso');
    pagePacientes();
  } catch (e) { toast(e.message, 'err'); }
}

function formPaciente() {
  openModal('Novo Paciente', `
    <div class="form-grid">
      <div class="form-group">
        <label>Nome completo *</label>
        <input class="field" id="p-nome" type="text" placeholder="Ex: Maria Silva">
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>CPF *</label>
          <input class="field" id="p-cpf" type="text" placeholder="000.000.000-00" maxlength="14">
        </div>
        <div class="form-group">
          <label>Telefone *</label>
          <input class="field" id="p-tel" type="tel" placeholder="(00) 00000-0000">
        </div>
      </div>
      <div class="form-group">
        <label>E-mail *</label>
        <input class="field" id="p-email" type="email" placeholder="email@exemplo.com">
      </div>
    </div>
    <div class="form-actions">
      <button class="btn btn-cancel-form" onclick="closeModal()">Cancelar</button>
      <button class="btn btn-save" id="p-salvar">Cadastrar</button>
    </div>`);

  document.getElementById('p-salvar').onclick = async () => {
    const d = {
      nome:     document.getElementById('p-nome').value.trim(),
      cpf:      document.getElementById('p-cpf').value.trim(),
      telefone: document.getElementById('p-tel').value.trim(),
      email:    document.getElementById('p-email').value.trim(),
    };
    if (!d.nome || !d.cpf || !d.telefone || !d.email) {
      toast('Preencha todos os campos', 'err'); return;
    }
    try {
      await api.pacientes.create(d);
      toast('Paciente cadastrado com sucesso');
      closeModal();
      pagePacientes();
    } catch (e) { toast(e.message, 'err'); }
  };
}