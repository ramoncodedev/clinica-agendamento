# Clínica Agendamento

API REST para gerenciamento de consultas em clínica de nutrição. Permite cadastrar pacientes, nutricionistas e serviços, além de agendar, listar e cancelar consultas com controle de conflito de horários.

**Deploy:** https://clinica-agendamento-production.up.railway.app

## Stack

- Java 21
- Spring Boot 4.1.0
- Spring Data JPA + Spring Validation
- PostgreSQL 16
- Flyway (migrations)
- Lombok
- JUnit 5 + Mockito

## Como rodar localmente

### Pré-requisitos

- Java 21+
- Maven
- Docker

### 1. Subir o banco de dados

```bash
docker-compose up -d
```

Isso sobe um PostgreSQL 16 na porta `5431` com o banco `clinica_db`.

### 2. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### 3. Verificar saúde da aplicação

```
GET http://localhost:8080/actuator/health
```

## Endpoints

### Pacientes

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/pacientes` | Cadastrar paciente |
| `GET` | `/pacientes` | Listar todos os pacientes |
| `GET` | `/pacientes/{id}` | Buscar paciente por ID |

### Nutricionistas

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/nutricionistas` | Cadastrar nutricionista |
| `GET` | `/nutricionistas` | Listar todos os nutricionistas |
| `GET` | `/nutricionistas/{id}` | Buscar nutricionista por ID |

### Serviços

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/servicos` | Cadastrar serviço |
| `GET` | `/servicos` | Listar todos os serviços |
| `GET` | `/servicos/{id}` | Buscar serviço por ID |

### Agendamentos

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/agendamentos` | Criar agendamento |
| `GET` | `/agendamentos?nutricionistaId={id}&data={yyyy-MM-dd}` | Listar por nutricionista e data |
| `PATCH` | `/agendamentos/{id}/cancelar` | Cancelar agendamento |

### Exemplo de requisição — criar agendamento

```json
POST /agendamentos
{
  "pacienteId": 1,
  "nutricionistaId": 1,
  "servicoId": 1,
  "data": "2026-06-16",
  "horaInicio": "08:00"
}
```

## Estrutura do projeto

```
src/main/java/com/agendamento/clinica/
├── Agendamento/
│   ├── Agendamento.java
│   ├── AgendamentoController.java
│   ├── AgendamentoRepository.java
│   ├── AgendamentoRequestDTO.java
│   ├── AgendamentoResponseDTO.java
│   ├── AgendamentoService.java
│   └── StatusAgendamento.java
├── Nutricionista/
│   ├── Nutricionista.java
│   ├── NutricionistaController.java
│   ├── NutricionistaRepository.java
│   ├── NutricionistaRequestDTO.java
│   ├── NutricionistaResponseDTO.java
│   └── NutricionistaService.java
├── Paciente/
│   ├── Paciente.java
│   ├── PacienteController.java
│   ├── PacienteRepository.java
│   ├── PacienteRequestDTO.java
│   ├── PacienteResponseDTO.java
│   └── PacienteService.java
└── Servico/
    ├── Servico.java
    ├── ServicoController.java
    ├── ServicoRepository.java
    ├── ServicoRequestDTO.java
    ├── ServicoResponseDTO.java
    └── ServicoService.java
```

## Testes

```bash
./mvnw test
```

Cobertura de testes unitários com Mockito nos principais fluxos do `AgendamentoService`:
- Lança exceção quando paciente não encontrado
- Lança exceção quando nutricionista não encontrado
- Lança exceção quando serviço não encontrado
- Lança exceção quando há conflito de horário
- Retorna agendamento criado com sucesso

## Autor

Ramon — [github.com/ramoncodedev](https://github.com/ramoncodedev)
