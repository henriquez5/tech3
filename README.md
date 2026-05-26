# Hospital Tech Challenge - FIAP Fase 3

Backend hospitalar desenvolvido para o **Tech Challenge da Fase 3 - Arquitetura e Desenvolvimento Java**, com foco em segurança, GraphQL, arquitetura distribuída e comunicação assíncrona.

O projeto simula um ambiente hospitalar onde médicos, enfermeiros e pacientes acessam funcionalidades diferentes. O sistema permite criar e editar consultas, consultar histórico do paciente via GraphQL e enviar eventos assíncronos para um serviço de notificações usando RabbitMQ.

---

## Sumário

- [Objetivo do projeto](#objetivo-do-projeto)
- [Requisitos atendidos](#requisitos-atendidos)
- [Arquitetura da solução](#arquitetura-da-solução)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Estrutura do repositório](#estrutura-do-repositório)
- [Pré-requisitos](#pré-requisitos)
- [Como executar o projeto](#como-executar-o-projeto)
- [Como importar no Eclipse](#como-importar-no-eclipse)
- [Usuários de teste](#usuários-de-teste)
- [Como testar no Postman](#como-testar-no-postman)
- [Endpoints REST](#endpoints-rest)
- [GraphQL](#graphql)
- [RabbitMQ](#rabbitmq)
- [Health checks](#health-checks)
- [Solução de problemas comuns](#solução-de-problemas-comuns)
- [Documentação](#documentação)

---

## Objetivo do projeto

O objetivo é entregar um backend modular para um sistema hospitalar com:

- autenticação e autorização por perfil de usuário;
- cadastro, edição e consulta de agendamentos médicos;
- histórico de consultas exposto por GraphQL;
- separação em mais de um serviço;
- comunicação assíncrona entre serviços com RabbitMQ;
- documentação e collection do Postman para validação.

---

## Requisitos atendidos

| Requisito do desafio | Implementação no projeto |
|---|---|
| Autenticação com Spring Security | Login com JWT e validação por filtro de segurança |
| Controle de acesso | Perfis `ROLE_MEDICO`, `ROLE_ENFERMEIRO` e `ROLE_PACIENTE` |
| Médicos visualizam/editam histórico | Médico pode listar, consultar e editar consultas |
| Enfermeiros registram consultas e acessam histórico | Enfermeiro pode criar e consultar consultas |
| Pacientes visualizam apenas suas consultas | Regras de serviço validam o usuário autenticado |
| GraphQL para histórico | Queries para histórico, consultas por paciente e consultas futuras |
| Mais de um serviço | `appointment-service` e `notification-service` |
| Comunicação assíncrona | Eventos publicados no RabbitMQ ao criar/editar consulta |
| Serviço de notificações | Consumer processa eventos e registra notificações |
| Collection para testes | Arquivo Postman em `/postman` |
| Documentação | README + PDF em `/docs` |

---

## Arquitetura da solução

O projeto foi separado em dois serviços principais:

```text
appointment-service
  Responsável por:
  - autenticação e autorização;
  - gerenciamento de consultas;
  - exposição de endpoints REST;
  - exposição de queries/mutations GraphQL;
  - publicação de eventos no RabbitMQ.

notification-service
  Responsável por:
  - consumir mensagens do RabbitMQ;
  - simular envio de lembretes aos pacientes;
  - registrar logs de notificação no banco de dados.
```

Fluxo assíncrono principal:

```text
1. Médico ou enfermeiro cria/edita uma consulta.
2. appointment-service salva a consulta no PostgreSQL.
3. appointment-service publica um evento no RabbitMQ.
4. notification-service consome o evento.
5. notification-service registra a notificação processada.
```

Essa separação reduz acoplamento: o serviço de agendamento não precisa esperar o serviço de notificações terminar o processamento para responder ao usuário.

---

## Tecnologias utilizadas

- Java 21
- Spring Boot 3.3.5
- Spring Web
- Spring Security
- JWT
- BCrypt
- Spring Data JPA
- PostgreSQL
- Spring GraphQL
- RabbitMQ
- Docker Compose
- Spring Actuator
- Maven
- Postman

---

## Estrutura do repositório

```text
hospital-tech-challenge/
  appointment-service/
    src/main/java/com/fiap/hospital/appointment/
    src/main/resources/
    pom.xml
    Dockerfile

  notification-service/
    src/main/java/com/fiap/hospital/notification/
    src/main/resources/
    pom.xml
    Dockerfile

  docs/
    documentacao-tech-challenge-fiap-completa.pdf
    documentacao-tech-challenge-fiap-completa.docx

  postman/
    hospital-tech-challenge.postman_collection.json

  docker-compose.yml
  pom.xml
  README.md
```

---

## Pré-requisitos

Antes de executar, instale/configure:

1. **Java JDK 21**
2. **Eclipse IDE** com suporte a Maven/Spring
3. **Maven** configurado no Eclipse ou instalado no sistema
4. **Docker Desktop** ou Docker Engine compatível
5. **Postman** para testar as APIs
6. **Git** para subir o projeto no GitHub

### Verificar Java

```bash
java -version
```

A versão deve ser Java 21 ou compatível.

### Verificar Docker

```bash
docker version
docker compose version
```

Se os comandos retornarem a versão, o Docker está pronto.

---

## Como executar o projeto

Existem duas formas principais:

1. **Recomendada para desenvolvimento:** subir apenas PostgreSQL e RabbitMQ via Docker, e rodar os serviços Java pelo Eclipse.
2. **Alternativa:** subir também os serviços pelo Docker usando profile `app`.

### 1. Subir infraestrutura com Docker

Na pasta raiz do projeto, onde está o arquivo `docker-compose.yml`, execute:

```bash
docker compose up -d
```

Esse comando sobe:

- RabbitMQ
- PostgreSQL do `appointment-service`
- PostgreSQL do `notification-service`

Para conferir:

```bash
docker ps
```

Containers esperados:

```text
hospital-rabbitmq
hospital-postgres-appointments
hospital-postgres-notifications
```

### 2. Rodar o appointment-service pelo Eclipse

Localize a classe:

```text
appointment-service/src/main/java/com/fiap/hospital/appointment/AppointmentServiceApplication.java
```

Clique com botão direito na classe e selecione:

```text
Run As > Java Application
```

ou, se disponível:

```text
Run As > Spring Boot App
```

Quando subir corretamente, o console deve indicar que o serviço iniciou na porta 8080.

### 3. Rodar o notification-service pelo Eclipse

Localize a classe:

```text
notification-service/src/main/java/com/fiap/hospital/notification/NotificationServiceApplication.java
```

Execute também com:

```text
Run As > Java Application
```

Quando subir corretamente, o console deve indicar que o serviço iniciou na porta 8081.

### 4. Subir também os serviços pelo Docker - opcional

Caso queira subir tudo pelo Docker:

```bash
docker compose --profile app up --build
```

Para desenvolvimento no Eclipse, prefira `docker compose up -d` apenas para infraestrutura.

---

## Como importar no Eclipse

1. Extraia o ZIP do projeto.
2. Abra o Eclipse.
3. Vá em:

```text
File > Import...
```

4. Escolha:

```text
Maven > Existing Maven Projects
```

5. Em **Root Directory**, selecione a pasta raiz do projeto.
6. O Eclipse deve localizar os arquivos `pom.xml`.
7. Marque os módulos:

```text
appointment-service
notification-service
```

8. Clique em **Finish**.
9. Aguarde o Maven baixar as dependências.

Se aparecer erro de dependência:

```text
Botão direito no projeto > Maven > Update Project...
```

Marque:

```text
Force Update of Snapshots/Releases
```

Clique em **OK**.

---

## Configurações importantes

### Portas

| Recurso | Porta |
|---|---:|
| appointment-service | 8080 |
| notification-service | 8081 |
| RabbitMQ AMQP | 5672 |
| RabbitMQ Management | 15672 |
| PostgreSQL appointments | 5433 |
| PostgreSQL notifications | 5434 |

### Banco do appointment-service

```text
Host: localhost
Porta: 5433
Database: appointments_db
User: hospital
Password: hospital
```

### Banco do notification-service

```text
Host: localhost
Porta: 5434
Database: notifications_db
User: hospital
Password: hospital
```

### RabbitMQ

```text
Host: localhost
Porta AMQP: 5672
Management: http://localhost:15672
User: guest
Password: guest
```

---

## Usuários de teste

Todos os usuários usam a senha:

```text
senha123
```

| Perfil | E-mail | Permissão principal |
|---|---|---|
| Médico | medico@hospital.com | Listar e editar consultas |
| Enfermeiro | enfermeiro@hospital.com | Criar consultas e acessar histórico |
| Paciente | paciente@hospital.com | Ver apenas suas próprias consultas |
| Paciente 2 | paciente2@hospital.com | Usado para testar bloqueio de acesso |

Os dados iniciais são criados automaticamente pela classe `DataInitializer` quando o banco está vazio.

---

## Como testar no Postman

A collection está em:

```text
postman/hospital-tech-challenge.postman_collection.json
```

### Importar a collection

1. Abra o Postman.
2. Clique em **Import**.
3. Selecione o arquivo da pasta `postman`.
4. Importe a collection.

### Fluxo de teste recomendado

1. Executar login com usuário médico.
2. Copiar o token JWT retornado.
3. Configurar o token na variável da collection ou no header `Authorization`.
4. Criar uma consulta.
5. Verificar no console do `notification-service` se o evento foi consumido.
6. Consultar a consulta criada.
7. Testar GraphQL.
8. Testar bloqueio de acesso com usuário paciente.

### Header de autenticação

Após o login, use:

```http
Authorization: Bearer SEU_TOKEN_AQUI
```

---

## Endpoints REST

### Login

```http
POST http://localhost:8080/auth/login
Content-Type: application/json
```

Body:

```json
{
  "email": "medico@hospital.com",
  "password": "senha123"
}
```

Resposta esperada:

```json
{
  "token": "eyJ...",
  "tokenType": "Bearer",
  "email": "medico@hospital.com",
  "role": "ROLE_MEDICO"
}
```

### Criar consulta

Permitido para: `ROLE_MEDICO`, `ROLE_ENFERMEIRO`

```http
POST http://localhost:8080/appointments
Authorization: Bearer SEU_TOKEN
Content-Type: application/json
```

Body:

```json
{
  "patientId": 1,
  "doctorId": 1,
  "dateTime": "2026-06-10T14:00:00",
  "status": "SCHEDULED",
  "notes": "Consulta criada via API para teste do Tech Challenge"
}
```

### Editar consulta

Permitido para: `ROLE_MEDICO`

```http
PUT http://localhost:8080/appointments/1
Authorization: Bearer SEU_TOKEN
Content-Type: application/json
```

Body:

```json
{
  "patientId": 1,
  "doctorId": 1,
  "dateTime": "2026-06-15T09:30:00",
  "status": "RESCHEDULED",
  "notes": "Consulta reagendada via API"
}
```

### Listar consultas

Permitido para: `ROLE_MEDICO`, `ROLE_ENFERMEIRO`

```http
GET http://localhost:8080/appointments
Authorization: Bearer SEU_TOKEN
```

### Buscar consulta por ID

Permitido para: `ROLE_MEDICO`, `ROLE_ENFERMEIRO` ou `ROLE_PACIENTE` dono da consulta.

```http
GET http://localhost:8080/appointments/1
Authorization: Bearer SEU_TOKEN
```

---

## GraphQL

Endpoint:

```text
POST http://localhost:8080/graphql
```

Interface GraphiQL:

```text
http://localhost:8080/graphiql
```

### Query - histórico do paciente

```graphql
query {
  medicalHistory(patientId: 1) {
    patient {
      id
      name
      email
    }
    appointments {
      id
      dateTime
      status
      notes
      doctor {
        name
        specialty
      }
    }
  }
}
```

### Query - consultas futuras

```graphql
query {
  futureAppointmentsByPatient(patientId: 1) {
    id
    dateTime
    status
    doctor {
      name
      specialty
    }
  }
}
```

### Query - consultas por paciente

```graphql
query {
  appointmentsByPatient(patientId: 1) {
    id
    dateTime
    status
    notes
    doctor {
      name
    }
    patient {
      name
      email
    }
  }
}
```

### Mutation - criar consulta via GraphQL

```graphql
mutation {
  createAppointment(input: {
    patientId: 1,
    doctorId: 1,
    dateTime: "2026-06-10T14:00:00",
    status: "SCHEDULED",
    notes: "Consulta criada pelo GraphQL"
  }) {
    id
    status
    dateTime
  }
}
```

### Testar GraphQL pelo Postman

Request:

```http
POST http://localhost:8080/graphql
Authorization: Bearer SEU_TOKEN
Content-Type: application/json
```

Body raw JSON:

```json
{
  "query": "query { appointmentsByPatient(patientId: 1) { id dateTime status notes doctor { name specialty } patient { name email } } }"
}
```

---

## RabbitMQ

Acesse o painel:

```text
http://localhost:15672
```

Credenciais:

```text
Usuário: guest
Senha: guest
```

Configurações usadas pelo projeto:

```text
Exchange: hospital.appointments.exchange
Queue: hospital.notifications.queue
Routing key criação: appointment.created
Routing key atualização: appointment.updated
```

### Como validar a mensageria

1. Suba os dois serviços Java.
2. Faça login no Postman.
3. Crie uma consulta pelo endpoint `POST /appointments`.
4. Observe o console do `notification-service`.
5. O consumer deve processar o evento e registrar uma notificação.
6. No RabbitMQ Management, é possível visualizar exchange, fila e mensagens processadas.

---

## Health checks

Appointment service:

```text
http://localhost:8080/actuator/health
```

Notification service:

```text
http://localhost:8081/actuator/health
```

Resposta esperada:

```json
{
  "status": "UP"
}
```

---

## Testes de permissão sugeridos

| Cenário | Resultado esperado |
|---|---|
| Médico cria consulta | Permitido |
| Enfermeiro cria consulta | Permitido |
| Paciente tenta criar consulta | Bloqueado com 403 |
| Médico edita consulta | Permitido |
| Enfermeiro tenta editar consulta | Bloqueado com 403 |
| Paciente lista todas as consultas | Bloqueado com 403 |
| Paciente acessa sua própria consulta | Permitido |
| Paciente tenta acessar consulta de outro paciente | Bloqueado |

---

## Solução de problemas comuns

### Docker não sobe

Verifique:

```bash
docker version
docker compose version
```

Se estiver usando Windows, confirme se WSL2 e virtualização estão habilitados.

### Porta ocupada

Se as portas 8080, 8081, 5433, 5434 ou 15672 estiverem ocupadas, encerre o processo que usa a porta ou altere a configuração.

### Erro de conexão com banco

Confirme se os containers estão rodando:

```bash
docker ps
```

Confirme se os bancos foram criados pelo `docker-compose.yml`.

### Erro de autenticação no Postman

Confirme se o token foi copiado corretamente e se o header está no formato:

```http
Authorization: Bearer SEU_TOKEN
```

### GraphQL retorna 401

Use o endpoint GraphQL pelo Postman com header de autenticação. A interface GraphiQL pode abrir sem token, mas as operações protegidas dependem do JWT.

### Notification-service não recebe evento

Verifique:

1. RabbitMQ está rodando?
2. `notification-service` está iniciado?
3. A consulta foi criada ou editada?
4. Exchange e queue existem no painel do RabbitMQ?
5. O console do serviço mostra erro de conexão?

---

## Documentação

A documentação detalhada está em:

```text
docs/tech challenger 3.pdf
```

A documentação explica:

- contexto do desafio;
- arquitetura proposta;
- serviços implementados;
- segurança e perfis de acesso;
- GraphQL;
- RabbitMQ;
- execução local;
- testes com Postman;
- critérios atendidos.

---

## Considerações finais

Este projeto demonstra uma solução backend modular com autenticação, autorização, GraphQL, mensageria assíncrona e separação de responsabilidades entre serviços. A proposta prioriza clareza, organização e aderência aos requisitos da Fase 3 do Tech Challenge.
