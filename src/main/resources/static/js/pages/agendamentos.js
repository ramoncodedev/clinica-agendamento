async function pageAgendamentos() {
  document.getElementById('main').innerHTML = `
    <div class="page-header">
      <div>
        <div class="page-title">Agendamentos</div>
        <div class="page-subtitle">Agenda por nutricionista e data</div>
      </div>
      <button class="btn btn-primary" id="btn-add-agd">+ Novo Agendamento</button>
    </div>
    <div class="page-content">
      <div class="card">
        <div class="card-toolbar">
          <div style="display:flex;flex-direction:column;gap:4px">
            <label style="font-size:9.5px;letter-spacing:0.12em;text-transform:uppercase;color:var(--text-muted)">Nutricionista</label>
            <select class="input-field" id="filter-nutri" style="min-width:210px">
              <option value="">Selecione…</option>
            </select>
          </div>
          <div style="display:flex;flex-direction:column;gap:4px">
            <label style="font-size:9.5px;letter-spacing:0.12em;text-transform:uppercase;color:var(--text-muted)">Data</label>
            <input class="input-field" type="date" id="filter-data" value="${todayISO()}">
          </div>
          <button class="btn btn-primary" id="btn-buscar" style="margin-top:auto">Buscar</button>
        </div>
        <div id="agd-body">
          <div class="empty">
            <div class="empty-icon">📅</div>
            <div class="empty-title">Selecione uma nutricionista e uma data</div>
          </div>
        </div>
      </div>
    </div>`;

  try {
    [store.nutricionistas, store.pacientes, store.servicos] = await Promise.all([
      api.nutricionistas.list(), api.pacientes.list(), api.servicos.list()
    ]);
  } catch (e) {
    toast('Erro ao carregar dados de apoio: ' + e.message, 'warn');
  }

  const sel = document.getElementById('filter-nutri');
  store.nutricionistas.forEach(n => {
    const opt = document.createElement('option');
    opt.value = n.id;
    opt.textContent = n.nome;
    sel.appendChild(opt);
  });

  document.getElementById('btn-add-agd').onclick = () => formAgendamento();
  document.getElementById('btn-buscar').onclick = buscarAgendamentos;

  if (store.nutricionistas.length > 0) {
    sel.value = store.nutricionistas[0].id;
    buscarAgendamentos();
  }
}

async function buscarAgendamentos() {
  const nid  = document.getElementById('filter-nutri').value;
  const data = document.getElementById('filter-data').value;
  if (!nid || !data) { toast('Selecione nutricionista e data', 'warn'); return; }

  document.getElementById('agd-body').innerHTML =
    `<div class="loading"><div class="spinner"></div> Carregando…</div>`;

  try {
    const list = await api.agendamentos.list(nid, data);
    renderAgendamentosTable(list, data);
  } catch (e) {
    document.getElementById('agd-body').innerHTML = errState(e.message);
  }
}

function renderAgendamentosTable(list, data) {
  const total  = list.length;
  const agend  = list.filter(a => a.status === 'AGENDADO').length;
  const cancel = list.filter(a => a.status === 'CANCELADO').length;
  const concl  = list.filter(a => a.status === 'CONCLUIDO').length;

  const stats = `
    <div class="stats-row">
      <div class="stat-chip">
        <div class="stat-chip-value">${total}</div>
        <div class="stat-chip-label">Total</div>
      </div>
      <div class="stat-chip">
        <div class="stat-chip-value v-green">${agend}</div>
        <div class="stat-chip-label">Agendados</div>
      </div>
      <div class="stat-chip">
        <div class="stat-chip-value v-red">${cancel}</div>
        <div class="stat-chip-label">Cancelados</div>
      </div>
      ${concl > 0 ? `<div class="stat-chip"><div class="stat-chip-value" style="color:var(--accent)">${concl}</div><div class="stat-chip-label">Concluídos</div></div>` : ''}
    </div>`;

  if (!total) {
    document.getElementById('agd-body').innerHTML =
      stats + emptyState('📅', `Sem agendamentos em ${fmtDate(data)}`, 'Clique em "+ Novo Agendamento" para criar');
    return;
  }

  const statusBadge = s => {
    const cls = { AGENDADO:'badge-green', CANCELADO:'badge-red', CONCLUIDO:'badge-blue', NAO_COMPARECEU:'badge-gray' };
    const lbl = { AGENDADO:'Agendado', CANCELADO:'Cancelado', CONCLUIDO:'Concluído', NAO_COMPARECEU:'Não compareceu' };
    return `<span class="badge ${cls[s] || 'badge-gray'}">${lbl[s] || s}</span>`;
  };

  const rows = list.map((a, i) => `
    <tr style="animation-delay:${i * 25}ms">
      <td style="font-family:'Cormorant Garamond',serif;font-size:17px;font-weight:600;color:var(--green-deep)">
        ${fmtTime(a.horaInicio)} – ${fmtTime(a.horaFim)}
      </td>
      <td class="td-main">${esc(a.pacienteNome)}</td>
      <td class="td-sub">${esc(a.nutricionistaNome)}</td>
      <td class="td-sub">${esc(a.servicoNome)}</td>
      <td>${statusBadge(a.status)}</td>
      <td class="actions">
        ${a.status === 'AGENDADO'
          ? `<button class="btn btn-danger" onclick="cancelarAgendamento(${a.id})">Cancelar</button>`
          : ''}
      </td>
    </tr>`).join('');

  document.getElementById('agd-body').innerHTML = stats + `
    <table>
      <thead><tr>
        <th>Horário</th><th>Paciente</th><th>Nutricionista</th><th>Serviço</th><th>Status</th><th></th>
      </tr></thead>
      <tbody>${rows}</tbody>
    </table>`;
}

async function cancelarAgendamento(id) {
  if (!confirm('Confirma o cancelamento deste agendamento?')) return;
  try {
    await api.agendamentos.cancelar(id);
    toast('Agendamento cancelado');
    buscarAgendamentos();
  } catch (e) { toast(e.message, 'err'); }
}

function formAgendamento() {
  const pacOpts   = store.pacientes.map(p      => `<option value="${p.id}">${esc(p.nome)}</option>`).join('');
  const nutriOpts = store.nutricionistas.map(n  => `<option value="${n.id}">${esc(n.nome)}</option>`).join('');
  const srvOpts   = store.servicos.map(s        => `<option value="${s.id}">${esc(s.nome)} (${s.duracaoMinutos} min)</option>`).join('');

  if (!pacOpts || !nutriOpts || !srvOpts) {
    toast('Cadastre pacientes, nutricionistas e serviços antes de agendar', 'warn');
    return;
  }

  openModal('Novo Agendamento', `
    <div class="form-grid">
      <div class="form-group">
        <label>Paciente *</label>
        <select class="field" id="a-pac"><option value="">Selecione…</option>${pacOpts}</select>
      </div>
      <div class="form-group">
        <label>Nutricionista *</label>
        <select class="field" id="a-nutri"><option value="">Selecione…</option>${nutriOpts}</select>
      </div>
      <div class="form-group">
        <label>Serviço *</label>
        <select class="field" id="a-srv"><option value="">Selecione…</option>${srvOpts}</select>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>Data * <span style="font-size:10px;text-transform:none;letter-spacing:0;color:var(--text-muted)">(deve ser futura)</span></label>
          <input class="field" id="a-data" type="date" min="${tomorrowISO()}" value="${tomorrowISO()}">
        </div>
        <div class="form-group">
          <label>Hora de início *</label>
          <input class="field" id="a-hora" type="time" value="08:00">
        </div>
      </div>
    </div>
    <div class="form-actions">
      <button class="btn btn-cancel-form" onclick="closeModal()">Cancelar</button>
      <button class="btn btn-save" id="a-salvar">Agendar</button>
    </div>`);

  document.getElementById('a-salvar').onclick = async () => {
    const pacId  = parseInt(document.getElementById('a-pac').value);
    const nutId  = parseInt(document.getElementById('a-nutri').value);
    const srvId  = parseInt(document.getElementById('a-srv').value);
    const data   = document.getElementById('a-data').value;
    const hora   = document.getElementById('a-hora').value;

    if (!pacId || !nutId || !srvId || !data || !hora) {
      toast('Preencha todos os campos', 'err'); return;
    }

    try {
      await api.agendamentos.create({
        pacienteId:      pacId,
        nutricionistaId: nutId,
        servicoId:       srvId,
        data:            data,
        horaInicio:      hora + ':00',
      });
      toast('Agendamento criado com sucesso');
      closeModal();
      const filterNutri = document.getElementById('filter-nutri');
      const filterData  = document.getElementById('filter-data');
      if (filterNutri) filterNutri.value = nutId;
      if (filterData)  filterData.value  = data;
      buscarAgendamentos();
    } catch (e) { toast(e.message, 'err'); }
  };
}