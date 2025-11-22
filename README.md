# FiaDoPay — Mercado

## 1. Contexto

Este projeto (“Opção 2: Mercado”) é uma solução para integrar pagamentos via **Mercado Pago** em uma aplicação de e-commerce (“mercado”). A ideia é construir um modelo confiável de checkout, permitindo que usuários façam pagamentos por cartão, PIX ou outros métodos suportados pela API do Mercado Pago, enquanto a aplicação mantém controle sobre pedidos, notificações (webhooks) e persistência de dados de clientes e transações.

Motivações principais:

- Aprendizado prático da API do Mercado Pago (uso de SDKs ou chamadas REST).  
- Construção de uma arquitetura backend em Java para processamento de pagamentos.  
- Persistência de dados de clientes e pedidos para rastreamento.  
- Implementação de notificações (webhooks) para saber quando uma transação mudou de estado (por exemplo, “pago”, “pendente”, “cancelado”).

## 2. Decisões de Design

Aqui estão algumas das principais decisões de arquitetura e design:

- **Linguagem & Framework**: Usar Java (como indica a estrutura do projeto).  
- **Persistência**: Banco de dados embutido (ou leve) para manter registros de pagamentos, clientes, pedidos, etc. (vejo no repositório algo como `fiadopay_client_db.mv.db`).  
- **Separação de camadas**:  
  - **API / serviço de pagamento**: comunicação com Mercado Pago para criar preferências, executar pagamentos, consultar status.  
  - **Webhook listener**: endpoint para receber notificações de mudança de estado das transações.  
  - **Lógica de negócio**: mapeamento entre pedidos da loja e preferências de pagamento.  
- **Anotações / metadata**: parece haver uma pasta `annotations` no repositório — provavelmente usada para armazenar dados estruturados sobre transações ou clientes.  
- **Segurança**: armazenar credenciais (token do Mercado Pago) fora do código-fonte, por exemplo em `credentials.properties`, que já está no repositório.  
- **Construção / Build**: usar Maven (pom.xml presente) para gerir dependências e empacotamento.

## 3. Anotações Criadas e Metadados

No diretório `annotations/`, você provavelmente tem arquivos que representam anotações de eventos importantes, como:

- `PagamentoCriado.annotation`: pode registrar quando uma preferência de pagamento foi criada.  
- `PagamentoAtualizado.annotation`: quando uma notificação webhook altera o status.  
- Metadados típicos para cada anotação:  
  - `timestamp`: quando o evento ocorreu  
  - `tipo`: tipo do evento (criação, atualização)  
  - `pedidoId` ou `externalReference`: para relacionar com o pedido interno  
  - `statusMercadoPago`: estado recebido da API (ex: “approved”, “pending”)  
  - `payload`: dados brutos recebidos (JSON)  

Essas anotações ajudam no rastreamento do fluxo de pagamento e servem para auditoria ou recuperação de falhas.

## 4. Mecanismo de Reflexão

Para permitir rastreamento mais fácil e depuração, o sistema pode usar:

- Registro (logging) detalhado para cada etapa: criação da preferência, envio para Mercado Pago, recebimento de webhook, atualização de banco de dados.  
- Reflexão sobre os objetos de anotação: talvez usar *reflection* de Java para inspecionar atributos das instâncias de anotação (se você estiver usando classes para representar eventos).  
- Persistência serializada: salvar os objetos de anotação em disco de forma serializada (por exemplo, JSON), para que possam ser revisitados posteriormente ou analisados por uma ferramenta externa.

## 5. Threads

Possíveis usos de threads no sistema:

- Listener de webhook pode rodar em uma thread separada para não bloquear o processamento normal do servidor.  
- Processamento assíncrono: quando uma notificação chega, usar thread pool para processar a atualização (salvar no banco, atualizar status do pedido) sem atrasar a resposta HTTP ao Mercado Pago.  
- Jobs de verificação: thread periódica para “polling” de status de pedidos antigos, caso haja dúvidas se uma notificação foi perdida.

## 6. Padrões Aplicados

Alguns padrões de projeto e arquitetura que fazem sentido para este tipo de aplicação:

- **Observer / Event Sourcing**: com as “anotações” (events) para registrar mudanças de estado das transações.  
- **DAO / Repository**: para separar a lógica de acesso a dados (persistência de pedidos, clientes, anotações) da lógica de negócio.  
- **Strategy / Adapter**: para implementar diferentes modos de pagamento ou diferentes formas de interagir com a API do Mercado Pago (ex: via SDK, via REST).  
- **Singleton**: para gerenciar instância do cliente Mercado Pago (token, configuração) de forma centralizada.  
- **Factory**: para criar objetos de “pagamento” ou “preferência” com base em parâmetros da aplicação.

## 7. Limites Conhecidos

Alguns limites e riscos que devem ser considerados:

- **Consistência eventual**: notificações via webhook podem atrasar ou nem sempre chegar — portanto, usar polling como fallback pode ser necessário.  
- **Segurança das credenciais**: se `credentials.properties` for comitado sem controle, risco de vazamento.  
- **Ambiente de sandbox vs produção**: testes devem ser feitos no sandbox do Mercado Pago, mas diferenças de comportamento podem surgir quando migrar para produção.  
- **Escalabilidade**: se houver muitos pedidos simultâneos, a arquitetura atual pode não suportar bem sem escalonamento (threads, banco, etc.).  
- **Tratamento de erros**: como lidar com falhas na rede, tempo limite da API, ou rejeição de pagamentos — precisa de lógica para retry ou rollback.  
- **Logs e anotações persistidas** podem crescer muito ao longo do tempo, exigindo estratégia de arquivamento ou giro (log rotation).

## 8. Evidências da Implementação (Prints)

Para comprovar a aplicação dos conceitos e padrões de design discutidos, as seguintes capturas de tela foram inseridas, demonstrando os trechos de código relevantes no projeto:

### 8.1. Estrutura e Decisões de Design (Arquitetura)

A estrutura de pacotes no Package Explorer evidencia a organização do projeto em **camadas** (`domain`, `infra`, `service`), a presença do pacote `annotations` e do arquivo de `credentials.properties` (segurança).

* **Evidência da Arquitetura em Camadas e Arquivos de Configuração:**
  

### 8.2. Anotações Criadas e Metadados

Duas anotações customizadas foram criadas, utilizando o metadado **`@Retention(RUNTIME)`** para garantir que sejam inspecionáveis pelo mecanismo de reflexão em tempo de execução.

* **Evidência da Anotação Customizada (Antifraude):**
  
* **Evidência da Anotação de Estratégia (`PaymentStrategy`):**
  

### 8.3. Padrões Aplicados: Strategy e Reflexão

O **Padrão Strategy** é implementado pelos *Processors* e o uso das anotações customizadas (**`@PaymentStrategy`** e **`@AntiFraud`**) permite que o **Padrão Factory** utilize **Reflexão** para selecionar e injetar o processador correto.

* **Evidência do Padrão Strategy e Anotação Aplicada:**


### 8.4. Threads, Concorrência e Webhook Listener

A concorrência é gerenciada para garantir a estabilidade e evitar *timeouts*. O `WebhookServer` usa uma `BlockingQueue` para processamento assíncrono, e o `JobScheduler` cuida de tarefas periódicas em *background*.

* **Evidência do Webhook Listener e Concorrência (Fila Assíncrona):**
    > 
* **Evidência de Jobs Periódicos (Scheduler):**


### 8.5. Persistência de Dados e Entidades de Domínio

A persistência é evidenciada pela tabela H2, que armazena transações (Padrão Repository/DAO) e dados de rastreamento importantes, como o `STATUS` e o `METHOD`.

* **Evidência do Domínio e Persistência (Tabela de Dados):**
    > 
* **Evidência da Entidade de Domínio:**
![Imagem do WhatsApp de 2025-11-21 à(s) 22 22 54_3adaeae8](https://github.com/user-attachments/assets/826708a9-ca20-4a37-be42-4236c47a33cd)
<img width="421" height="838" alt="Captura de tela 2025-11-22 001339" src="https://github.com/user-attachments/assets/7cf56c94-a4e7-483d-ba2d-e6d771b8f2af" />
<img width="537" height="282" alt="Captura de tela 2025-11-22 001814" src="https://github.com/user-attachments/assets/06768a26-8947-4ee3-88b6-5bf7835c3b32" />
<img width="798" height="328" alt="Captura de tela 2025-11-22 001826" src="https://github.com/user-attachments/assets/178b4cc7-cd15-48cd-b8cb-65875a18d9de" />
<img width="897" height="362" alt="Captura de tela 2025-11-22 001919" src="https://github.com/user-attachments/assets/671cc2fd-9e01-4f15-b6e2-972da4ed91c3" />
<img width="853" height="666" alt="Captura de tela 2025-11-22 001934" src="https://github.com/user-attachments/assets/0901e948-c4cd-4af9-941f-62466ecf5c0f" />
<img width="737" height="672" alt="Captura de tela 2025-11-22 001949" src="https://github.com/user-attachments/assets/b6dbc952-fba6-45f8-bc42-80de604816a7" />
<img width="975" height="693" alt="Captura de tela 2025-11-22 002113" src="https://github.com/user-attachments/assets/eeda9bf6-2141-489b-88ec-9fed460f5653" />


##  Transparência sobre o Uso de Inteligência Artificial
Este projeto contou com o auxílio de ferramentas de Inteligência Artificial (IA) como assistente no processo de desenvolvimento. A IA foi utilizada como suporte na elaboração da documentação técnica, na estruturação de decisões arquiteturais e na organização do design do sistema. Todo o conteúdo gerado com auxílio de IA foi revisado, validado e adaptado pelo grupo para garantir a precisão, coerência e adequação ao contexto do projeto. A responsabilidade final sobre o código, a arquitetura e a documentação permanece integralmente com os autores do repositório.
