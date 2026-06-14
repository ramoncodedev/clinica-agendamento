async function pageNutricionistas() {
  document.getElementById('main').innerHTML = `
    <div class="page-header">
      <div>
        <div class="page-title">Nutricionistas</div>
        <div class="page-subtitle">Equipe de profissionais da clínica</div>
      </div>
      <button class="btn btn-primary" id="btn-add-nutri">+ Nova Nutricionista</button>
    </div>
    <div class="page-content">
      <div class="card">
        <div id="nutri-body"><div class="loading"><div class="spinner"></div> Carregando…</div></div>
      </div>
    </div>`;

  document.getElementById('btn-add-nutri').onclick = () => formNutricionista();

  try {
    store.nutricionistas = await api.nutricionistas.list();
  } catch (e) {
    document.getElementById('nutri-body').innerHTML = errState(e.message);
    return;
  }

  renderNutriTable(store.nutricionistas);
}

function renderNutriTable(list) {
  if (!list.length) {
    document.getElementById('nutri-body').innerHTML =
      emptyState('🩺', 'Nenhuma nutricionista', 'Cadastre a primeira profissional');
    return;
  }
  const rows = list.map((n, i) => `
    <tr style="animation-delay:${i * 25}ms">
      <td class="td-main">${esc(n.nome)}</td>
      <td><span class="badge badge-gold">${esc(n.crn)}</span></td>
      <td class="td-sub">${esc(n.especialidade)}</td>
      <td class="td-sub">${esc(n.email)}</td>
      <td class="td-sub">${esc(n.telefone)}</td>
    </tr>`).join('');
  document.getElementById('nutri-body').innerHTML = `
    <table>
      <thead><tr><th>Nome</th><th>CRN</th><th>Especialidade</th><th>E-mail</th><th>Telefone</th></tr></thead>
      <tbody>${rows}</tbody>
    </table>`;
}

function formNutricionista() {
  openModal('Nova Nutricionista', `
    <div class="form-grid">
      <div class="form-group">
        <label>Nome completo *</label>
        <input class="field" id="n-nome" type="text" placeholder="Dra. Ana Lima">
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>CRN *</label>
          <input class="field" id="n-crn" type="text" placeholder="CRN-X/00000">
        </div>
        <div class="form-group">
          <label>Especialidade *</label>
          <input class="field" id="n-esp" type="text" placeholder="Nutrição Clínica">
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>E-mail *</label>
          <input class="field" id="n-email" type="email" placeholder="dra@clinica.com">
        </div>
        <div class="form-group">
          <label>Telefone *</label>
          <input class="field" id="n-tel" type="tel" placeholder="(00) 00000-0000">
        </div>
      </div>
    </div>
    <div class="form-actions">
      <button class="btn btn-cancel-form" onclick="closeModal()">Cancelar</button>
      <button class="btn btn-save" id="n-salvar">Cadastrar</button>
    </div>`);

  document.getElementById('n-salvar').onclick = async () => {
    const d = {
      nome:          document.getElementById('n-nome').value.trim(),
      crn:           document.getElementById('n-crn').value.trim(),
      especialidade: document.getElementById('n-esp').value.trim(),
      email:         document.getElementById('n-email').value.trim(),
      telefone:      document.getElementById('n-tel').value.trim(),
    };
    if (!d.nome || !d.crn || !d.especialidade || !d.email || !d.telefone) {
      toast('Preencha todos os campos', 'err'); return;
    }
    try {
      await api.nutricionistas.create(d);
      toast('Nutricionista cadastrada com sucesso');
      closeModal();
      pageNutricionistas();
    } catch (e) { toast(e.message, 'err'); }
  };
}