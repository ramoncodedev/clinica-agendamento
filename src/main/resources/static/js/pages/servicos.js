async function pageServicos() {
  document.getElementById('main').innerHTML = `
    <div class="page-header">
      <div>
        <div class="page-title">Serviços</div>
        <div class="page-subtitle">Tipos de atendimento e suas durações</div>
      </div>
      <button class="btn btn-primary" id="btn-add-srv">+ Novo Serviço</button>
    </div>
    <div class="page-content">
      <div class="card">
        <div id="srv-body"><div class="loading"><div class="spinner"></div> Carregando…</div></div>
      </div>
    </div>`;

  document.getElementById('btn-add-srv').onclick = () => formServico();

  try {
    store.servicos = await api.servicos.list();
  } catch (e) {
    document.getElementById('srv-body').innerHTML = errState(e.message);
    return;
  }

  renderServicosTable(store.servicos);
}

function renderServicosTable(list) {
  if (!list.length) {
    document.getElementById('srv-body').innerHTML =
      emptyState('📋', 'Nenhum serviço', 'Cadastre o primeiro tipo de atendimento');
    return;
  }
  const rows = list.map((s, i) => `
    <tr style="animation-delay:${i * 25}ms">
      <td class="td-main">${esc(s.nome)}</td>
      <td class="td-sub">${esc(s.descricao || '—')}</td>
      <td><span class="badge badge-gold">${s.duracaoMinutos} min</span></td>
    </tr>`).join('');
  document.getElementById('srv-body').innerHTML = `
    <table>
      <thead><tr><th>Nome</th><th>Descrição</th><th>Duração</th></tr></thead>
      <tbody>${rows}</tbody>
    </table>`;
}

function formServico() {
  openModal('Novo Serviço', `
    <div class="form-grid">
      <div class="form-row">
        <div class="form-group">
          <label>Nome do serviço *</label>
          <input class="field" id="s-nome" type="text" placeholder="Consulta Inicial">
        </div>
        <div class="form-group">
          <label>Duração (minutos) *</label>
          <input class="field" id="s-dur" type="number" min="15" max="240" step="15" placeholder="60">
        </div>
      </div>
      <div class="form-group">
        <label>Descrição</label>
        <textarea class="field" id="s-desc" placeholder="Descreva o serviço (opcional)…"></textarea>
      </div>
    </div>
    <div class="form-actions">
      <button class="btn btn-cancel-form" onclick="closeModal()">Cancelar</button>
      <button class="btn btn-save" id="s-salvar">Cadastrar</button>
    </div>`);

  document.getElementById('s-salvar').onclick = async () => {
    const d = {
      nome:           document.getElementById('s-nome').value.trim(),
      duracaoMinutos: parseInt(document.getElementById('s-dur').value) || 0,
      descricao:      document.getElementById('s-desc').value.trim() || null,
    };
    if (!d.nome || !d.duracaoMinutos) {
      toast('Nome e duração são obrigatórios', 'err'); return;
    }
    try {
      await api.servicos.create(d);
      toast('Serviço cadastrado com sucesso');
      closeModal();
      pageServicos();
    } catch (e) { toast(e.message, 'err'); }
  };
}