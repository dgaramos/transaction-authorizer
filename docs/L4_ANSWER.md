# Explicação sobre o Problema de Transações Simultâneas

Para resolver o problema das transações simultâneas, é necessário considerar alguns aspectos que explicarei brevemente.

## Modelo de Dados

O modelo de dados é formado por três tabelas principais:
- **Account**: Representa a conta do usuário.
- **AccountBalance**: Representa os saldos associados a uma conta, categorizados por tipos.
- **CardTransaction**: Representa as transações realizadas com o cartão.

## Estrutura de Relacionamento
- Um **Account** pode ter várias **AccountBalances**, mas apenas **uma de cada tipo de saldo** (representado por `accountBalanceType`).
- Essa estrutura foi projetada por alguns motivos:

### 1. Benefícios do Modelo
1. **Flexibilidade para adicionar novos tipos de saldo**  
   Se for necessário criar saldos para novos tipos (por exemplo, `COMMUTE`), não será necessário realizar uma migration na tabela `Account` para adicionar novos campos.

2. **Redução de problemas de concorrência**
    - Como os saldos estão em linhas separadas na tabela, a chance de duas transações no mesmo saldo diminuem, permitindo que transações simultâneas de tipos diferentes sejam processadas sem que uma passe por cima da outra.


Contudo, apesar das melhorias trazidas pelo modelo, ainda podem ocorrer cenários em que duas transações tentem acessar simultaneamente o mesmo **AccountBalance**, resultando em possíveis inconsistências. Além disso, a questão traz a seguinte limitação:

#### Transações Síncronas e com Duração Máxima de 100ms
Cada transação é tratada de forma síncrona e deve ser processada em até 100ms. Apesar disso ser colocado como uma limitação, sabemos que isso reduz significativamente a janela de concorrência.

### 2. Tratamento de Concorrência no Mesmo Tipo de Saldo

#### Optimistic Concurrency

Uma alternativa para lidar com concorrência no mesmo tipo de saldo é adotar uma abordagem de **Lock Otimista**. Esse modelo pressupõe que conflitos são raros e utiliza um controle baseado em versões para detectar alterações concorrentes antes de confirmar as transações.

1. **Adição de um Campo `version` no Modelo de Dados**  
   A tabela `AccountBalance` seria atualizada com um campo adicional chamado `version`, que registra a versão atual do registro.  

2. **Leitura da Versão Atual**  
   Quando uma transação é iniciada, a versão do saldo correspondente é lida e armazenada.

3. **Validação Antes da Escrita**  
   Durante a atualização, o sistema verifica se a versão armazenada no início da transação ainda é a mesma no momento da gravação. Caso contrário, a transação é rejeitada para evitar sobrescritas indesejadas.

4. **Incremento da Versão no Momento da Atualização**  
   Se a validação for bem-sucedida, a transação incrementa a versão do registro ao atualizar o saldo.

5. **Reprocessamento em Caso de Conflito**  
   Caso uma transação detecte um conflito (ou seja, a versão do registro foi alterada por outra operação), ela pode recalcular o saldo e tentar novamente, respeitando o tempo limite definido.

6**Erro em Caso de Tempo Excedido**  
   Se o tempo da sessão expirar sem que a transação seja concluída, retorna-se um erro com o código **07**, indicando um problema geral no processamento.

#### Benefícios da Concorrência Otimista
- **Desempenho Melhorado:**  
  Por não bloquear os registros durante a transação, a solução reduz o impacto no desempenho, especialmente em cenários com muitas leituras simultâneas.
- **Menor Contenção de Recursos:**  
  Comparado ao *lock pessimista*, essa abordagem permite maior paralelismo.
- **Detecção de Conflitos:**  
  Garantia de que nenhuma transação sobrescreva alterações feitas por outra de forma acidental.

#### Implementação com Concorrência Otimista no Contexto
No caso do **AccountBalance**, a abordagem otimista seria utilizada principalmente para garantir que apenas uma transação afete um saldo específico por vez, enquanto outras continuam processando saldos de tipos diferentes sem interrupções.
