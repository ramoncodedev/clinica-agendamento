# Sistema de Agendamento - Clínica de Nutrição

## Stack
- Java 21 + Spring Boot 4.1.0
- Spring Security, Spring Data JPA, Spring Validation
- PostgreSQL 16 (Docker, porta 5431)
- Flyway (migrations)
- Lombok

## Estrutura do projeto
```
Sistema agendamento clinica/
├── docker-compose.yml
└── clinica/                        ← projeto Spring Boot
    ├── pom.xml
    └── src/main/
        ├── resources/
        │   ├── application.yaml
        │   └── db/migration/
        │       └── V1__criar_schema_inicial.sql
        └── java/com/agendamento/clinica/
            ├── ClinicaApplication.java
            ├── paciente/
            │   └── Paciente.java
            ├── nutricionista/
            │   ├── Nutricionista.java
            │   └── DisponibilidadeNutricionista.java
            ├── servico/
            │   └── Servico.java
            └── agendamento/
                ├── Agendamento.java
                └── StatusAgendamento.java
```

## Cronograma (12 dias úteis)
- [x] Dias 1-2: Modelagem e banco de dados (schema SQL + Docker + configuração)
- [x] Dias 3-5: Backend (entidades JPA → repositórios → services → controllers)
- [x] Dias 6-7: Frontend
- [x] Dias 8-9: Testes unitários (AgendamentoServiceTest + PacienteServiceTest — testes verdes)
- [x] Dia 10: Observabilidade e logs (Actuator + @Slf4j no AgendamentoService)
- [ ] Dia 11: Debug / "Terapia do Caos" (usuário tem vídeo específico)
- [ ] Dia 12: Deploy + GitHub + README

## Onde paramos (Dia 10)
Iniciando observabilidade e logs.

Arquivos a criar em `src/main/java/com/agendamento/clinica/`:

### paciente/Paciente.java
```java
package com.agendamento.clinica.paciente;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "paciente")
@Getter
@Setter
@NoArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    private void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}
```

### nutricionista/Nutricionista.java
```java
package com.agendamento.clinica.nutricionista;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "nutricionista")
@Getter
@Setter
@NoArgsConstructor
public class Nutricionista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false, unique = true, length = 20)
    private String crn;

    @Column(nullable = false, length = 100)
    private String especialidade;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    private void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}
```

### nutricionista/DisponibilidadeNutricionista.java
```java
package com.agendamento.clinica.nutricionista;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "disponibilidade_nutricionista")
@Getter
@Setter
@NoArgsConstructor
public class DisponibilidadeNutricionista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nutricionista_id")
    private Nutricionista nutricionista;

    @Column(name = "dia_semana", nullable = false, length = 15)
    private String diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;
}
```

### servico/Servico.java
```java
package com.agendamento.clinica.servico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "servico")
@Getter
@Setter
@NoArgsConstructor
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos;
}
```

### agendamento/StatusAgendamento.java
```java
package com.agendamento.clinica.agendamento;

public enum StatusAgendamento {
    AGENDADO,
    CANCELADO,
    CONCLUIDO,
    NAO_COMPARECEU
}
```

### agendamento/Agendamento.java
```java
package com.agendamento.clinica.agendamento;

import com.agendamento.clinica.nutricionista.Nutricionista;
import com.agendamento.clinica.paciente.Paciente;
import com.agendamento.clinica.servico.Servico;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamento")
@Getter
@Setter
@NoArgsConstructor
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nutricionista_id")
    private Nutricionista nutricionista;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id")
    private Servico servico;

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    private void prePersist() {
        this.criadoEm = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusAgendamento.AGENDADO;
        }
    }
}
```

## Próximos passos (após entidades prontas)
1. Subir Docker: `docker-compose up -d`
2. Rodar a aplicação para validar o Flyway (sem erros = schema OK)
3. Criar os Repositories (interfaces JPA)
4. Criar os Services (regras de negócio)
5. Criar os Controllers (endpoints REST)

## Diretrizes de ensino
- Nunca entregar código completo de uma vez
- Toda linha de código vem com explicação imediata
- Validar entendimento com perguntas antes de avançar
- Apresentar opções com trade-offs e aguardar decisão do usuário
- Organização por feature (pacote por funcionalidade, não por camada)