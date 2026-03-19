package com.uepb.compiler;

import com.uepb.ExprBaseVisitor;
import com.uepb.ExprParser.*;

public class Calculadora extends ExprBaseVisitor<Void> {

    private final ScopeControl scopes = new ScopeControl();
    private final MemoryMapper mapper = new MemoryMapper();
    private final StringBuilder code = new StringBuilder();
    private int labelCounter = 0;

    private String createLabel() {
        return "L" + labelCounter++;
    }

    public String getCode() {
        return code.toString();
    }

    // =========================================================================
    // PROGRAMA
    // =========================================================================

    @Override
    public Void visitProg(ProgContext ctx) {
        // ctx.stat() retorna List<StatContext> — visitar cada um em ordem
        // Ao final, emitir hlt para encerrar o programa
        //
        // IMPLEMENTAR:
        //   for (var stat : ctx.stat()) visit(stat);
        //   code.append("hlt\n");
        return null;
    }

    // =========================================================================
    // STATEMENTS
    // =========================================================================

    @Override
    public Void visitDeclVar(DeclVarContext ctx) {
        // ctx.ID()     → token com o nome da variável
        // ctx.expr()   → expressão inicial (NULL se for "var x" sem valor)
        //
        // IMPLEMENTAR:
        // 1. var nome   = ctx.ID().getText()
        // 2. var tk     = ctx.ID().getSymbol()       ← para linha/coluna no erro
        // 3. var scope  = scopes.getCurrentScope()
        // 4. Se scope.exists(nome) → throw RuntimeException("variável já declarada...")
        // 5. var addr   = mapper.alloc()
        // 6. scope.insert(nome, addr)
        //
        // 7. Se ctx.expr() != null:
        //      code: push $addr
        //      visit(ctx.expr())                     ← empilha valor calculado
        //      code: sto
        //
        //    Se ctx.expr() == null (var sem inicialização → padrão = 0):
        //      code: push $addr
        //      code: push 0
        //      code: sto
        return null;
    }

    @Override
    public Void visitAtribuicao(AtribuicaoContext ctx) {
        // ctx.ID()   → nome da variável sendo atribuída
        // ctx.expr() → expressão com o novo valor
        //
        // IMPLEMENTAR:
        // 1. var nome = ctx.ID().getText()
        // 2. var tk   = ctx.ID().getSymbol()
        // 3. var opt  = scopes.lookup(nome)
        // 4. Se opt.isEmpty() → throw RuntimeException("variável não declarada...")
        // 5. var addr = opt.get().address()
        //
        // 6. code: push $addr
        //    visit(ctx.expr())                       ← empilha o valor novo
        //    code: sto
        //
        // ATENÇÃO: NÃO emitir lod após o sto — atribuição é statement, não expr
        return null;
    }

    @Override
    public Void visitIfStat(IfStatContext ctx) {
        // ctx.boolExpr()       → condição do if
        // ctx.stat()           → statements do bloco then (só os do if principal)
        // ctx.elseifClause()   → List<ElseifClauseContext> (pode ser vazia)
        // ctx.elseClause()     → ElseClauseContext (pode ser null)
        //
        // ESTRUTURA DE LABELS:
        //
        //   [condição do if]
        //   fjp L0                    ← L0 = primeiro else-if (ou else, ou fim)
        //     [stats do then]
        //   ujp LFIM
        // L0:
        //   [condição do elseif 0]
        //   fjp L1
        //     [stats do elseif 0]
        //   ujp LFIM
        // L1:
        //   [condição do elseif 1]
        //   fjp L2                    ← L2 = else (ou fim, se não houver else)
        //     [stats do elseif 1]
        //   ujp LFIM
        // L2:                         ← só existe se houver else
        //   [stats do else]
        // LFIM:
        //
        // IMPLEMENTAR:
        // 1. var labelFim = createLabel()
        //
        // 2. Criar labels intermediários — um por elseif + 1 para o else (se existir)
        //    List<String> labelsElse = new ArrayList<>();
        //    for (var _ : ctx.elseifClause()) labelsElse.add(createLabel());
        //    String labelElseFinal = (ctx.elseClause() != null) ? createLabel() : labelFim;
        //
        // 3. Bloco then:
        //    visit(ctx.boolExpr())
        //    String primeiroElse = labelsElse.isEmpty() ? labelElseFinal : labelsElse.get(0);
        //    code: fjp [primeiroElse]
        //    for (var s : ctx.stat()) visit(s)
        //    code: ujp labelFim
        //
        // 4. Cada elseif (com índice i):
        //    code: labelsElse.get(i):
        //    visit(elseif.boolExpr())
        //    String proximo = (i+1 < labelsElse.size()) ? labelsElse.get(i+1) : labelElseFinal;
        //    code: fjp [proximo]
        //    for (var s : elseif.stat()) visit(s)
        //    code: ujp labelFim
        //
        // 5. Se ctx.elseClause() != null:
        //    code: labelElseFinal:
        //    for (var s : ctx.elseClause().stat()) visit(s)
        //
        // 6. code: labelFim:
        return null;
    }

    @Override
    public Void visitElseifClause(ElseifClauseContext ctx) {
        // NÃO é chamado diretamente — o visitIfStat itera sobre
        // ctx.elseifClause() e acessa cada ElseifClauseContext manualmente.
        // Deixado vazio intencionalmente.
        return null;
    }

    @Override
    public Void visitElseClause(ElseClauseContext ctx) {
        // NÃO é chamado diretamente — tratado dentro do visitIfStat.
        // Deixado vazio intencionalmente.
        return null;
    }

    @Override
    public Void visitWhileStat(WhileStatContext ctx) {
        // ctx.boolExpr() → condição do loop
        // ctx.stat()     → List<StatContext> com o corpo do while
        //
        // ESTRUTURA P-CODE:
        //   LSTART:
        //     [condição]
        //     fjp LEND
        //     [corpo]
        //     ujp LSTART
        //   LEND:
        //
        // IMPLEMENTAR:
        // 1. var lstart = createLabel()
        // 2. var lend   = createLabel()
        // 3. code: lstart:
        // 4. visit(ctx.boolExpr())
        // 5. code: fjp lend
        // 6. for (var s : ctx.stat()) visit(s)
        // 7. code: ujp lstart
        // 8. code: lend:
        return null;
    }

    @Override
    public Void visitPrintStat(PrintStatContext ctx) {
        // ctx.expr() → o que será impresso (string, número, variável ou aritmética)
        //
        // IMPLEMENTAR:
        //   visit(ctx.expr())   ← empilha o valor
        //   code: out           ← retira do topo e imprime
        return null;
    }

    // =========================================================================
    // EXPRESSÕES ARITMÉTICAS — nível 1 (menor precedência): + e -
    // =========================================================================

    @Override
    public Void visitSomaSub(SomaSubContext ctx) {
        // ctx.O1  → expr  (operando esquerdo)
        // ctx.O2  → term  (operando direito)
        // ctx.OP  → token ADD("+") ou SUB("-")
        //
        // IMPLEMENTAR:
        //   visit(ctx.O1)
        //   visit(ctx.O2)
        //   if ctx.OP.getText().equals("+") → code: add
        //   else                            → code: sub
        return null;
    }

    @Override
    public Void visitTermPassthrough(TermPassthroughContext ctx) {
        // Passthrough: propaga para a regra term sem gerar código
        //
        // IMPLEMENTAR:
        //   visit(ctx.term())
        return null;
    }

    // =========================================================================
    // EXPRESSÕES ARITMÉTICAS — nível 2 (média precedência): * e /
    // =========================================================================

    @Override
    public Void visitMulDiv(MulDivContext ctx) {
        // ctx.O1  → term   (operando esquerdo)
        // ctx.O2  → factor (operando direito)
        // ctx.OP  → token MUL("*") ou DIV("/")
        //
        // IMPLEMENTAR:
        //   visit(ctx.O1)
        //   visit(ctx.O2)
        //   if ctx.OP.getText().equals("*") → code: mul
        //   else                            → code: div
        return null;
    }

    @Override
    public Void visitFactorPassthrough(FactorPassthroughContext ctx) {
        // Passthrough: propaga para a regra factor sem gerar código
        //
        // IMPLEMENTAR:
        //   visit(ctx.factor())
        return null;
    }

    // =========================================================================
    // EXPRESSÕES ARITMÉTICAS — nível 3 (maior precedência): ^ (right-assoc)
    // =========================================================================

    @Override
    public Void visitPotencia(PotenciaContext ctx) {
        // ctx.O1 → atom   (base)
        // ctx.O2 → factor (expoente — recursão à direita = right-associativo)
        //
        // O P-code não possui instrução pow nativa. Duas opções:
        //
        // OPÇÃO A — instrução "pow" customizada (confirmar com o professor):
        //   visit(ctx.O1)
        //   visit(ctx.O2)
        //   code: pow
        //
        // OPÇÃO B — loop de multiplicação em P-code (sem instrução nativa):
        //   var marker   = mapper.getCurrentAddress()   ← salva posição para liberar depois
        //   var addrBase = mapper.alloc()
        //   var addrExp  = mapper.alloc()
        //   var addrAcc  = mapper.alloc()
        //   var lstart   = createLabel()
        //   var lend     = createLabel()
        //
        //   Salvar base:      push $addrBase / visit(ctx.O1) / sto
        //   Salvar expoente:  push $addrExp  / visit(ctx.O2) / sto
        //   Acumulador = 1:   push $addrAcc  / push 1        / sto
        //
        //   lstart:
        //     push $addrExp / lod / push 0 / grt   ← exp > 0 ?
        //     fjp lend
        //     push $addrAcc / push $addrAcc / lod / push $addrBase / lod / mul / sto
        //     push $addrExp / push $addrExp / lod / push 1 / sub / sto
        //     ujp lstart
        //   lend:
        //     push $addrAcc / lod                  ← resultado no topo
        //
        //   mapper.restoreTo(marker)               ← libera os 3 temporários
        return null;
    }

    @Override
    public Void visitAtomPassthrough(AtomPassthroughContext ctx) {
        // Passthrough: propaga para a regra atom sem gerar código
        //
        // IMPLEMENTAR:
        //   visit(ctx.atom())
        return null;
    }

    // =========================================================================
    // ÁTOMOS
    // =========================================================================

    @Override
    public Void visitParenteses(ParentesesContext ctx) {
        // ctx.NESTED → expr interna (label definido no .g4 como NESTED=expr)
        // Não gera instrução — a precedência já está resolvida pela gramática
        //
        // IMPLEMENTAR:
        //   visit(ctx.NESTED)
        return null;
    }

    @Override
    public Void visitNumero(NumeroContext ctx) {
        // ctx.NUMBER() → token com o literal numérico (ex: "42" ou "3.14")
        //
        // IMPLEMENTAR:
        //   var num = ctx.NUMBER().getText()
        //   code: push [num]
        return null;
    }

    @Override
    public Void visitUsoVariavel(UsoVariavelContext ctx) {
        // ctx.ID() → token com o nome da variável
        //
        // IMPLEMENTAR:
        // 1. var nome = ctx.ID().getText()
        // 2. var tk   = ctx.ID().getSymbol()
        // 3. var opt  = scopes.lookup(nome)
        // 4. Se opt.isEmpty() → throw RuntimeException("variável não declarada na linha X coluna Y")
        // 5. var addr = opt.get().address()
        // 6. code: push $addr
        //    code: lod               ← substitui o endereço pelo valor armazenado
        return null;
    }

    @Override
    public Void visitStringLiteral(StringLiteralContext ctx) {
        // ctx.STRING() → token com a string INCLUINDO as aspas (ex: "hello")
        //
        // IMPLEMENTAR:
        //   var str = ctx.STRING().getText()    ← ex: "hello" (com aspas)
        //   code: push [str]                   ← ex: push "hello"
        //
        // NÃO remover as aspas — o interpretador P-code as espera na instrução push
        return null;
    }

    @Override
    public Void visitInput(InputContext ctx) {
        // ctx.STRING() → texto do prompt (ex: "digite um valor")
        //
        // SEQUÊNCIA P-CODE COMPLETA:
        //   push [prompt]   ← empilha a string do prompt (com aspas)
        //   out             ← imprime o prompt para o usuário
        //   in              ← lê valor do teclado e empilha no topo
        //
        // O valor lido fica no topo da pilha.
        // Quem chamou (visitDeclVar ou visitAtribuicao) emite push $addr + sto em seguida.
        //
        // IMPLEMENTAR:
        //   var prompt = ctx.STRING().getText()
        //   code: push [prompt]
        //   code: out
        //   code: in
        return null;
    }

    // =========================================================================
    // EXPRESSÕES BOOLEANAS — nível 1 (menor precedência): or
    // =========================================================================

    @Override
    public Void visitOr(OrContext ctx) {
        // ctx.O1 → boolExpr (operando esquerdo)
        // ctx.O2 → boolAnd  (operando direito)
        //
        // IMPLEMENTAR:
        //   visit(ctx.O1)
        //   visit(ctx.O2)
        //   code: or
        return null;
    }

    @Override
    public Void visitOrPassthrough(OrPassthroughContext ctx) {
        // Passthrough: propaga para boolAnd sem gerar código
        //
        // IMPLEMENTAR:
        //   visit(ctx.boolAnd())
        return null;
    }

    // =========================================================================
    // EXPRESSÕES BOOLEANAS — nível 2 (média precedência): and
    // =========================================================================

    @Override
    public Void visitAnd(AndContext ctx) {
        // ctx.O1 → boolAnd (operando esquerdo)
        // ctx.O2 → boolNot (operando direito)
        //
        // IMPLEMENTAR:
        //   visit(ctx.O1)
        //   visit(ctx.O2)
        //   code: and
        return null;
    }

    @Override
    public Void visitAndPassthrough(AndPassthroughContext ctx) {
        // Passthrough: propaga para boolNot sem gerar código
        //
        // IMPLEMENTAR:
        //   visit(ctx.boolNot())
        return null;
    }

    // =========================================================================
    // EXPRESSÕES BOOLEANAS — nível 3 (maior precedência): not
    // =========================================================================

    @Override
    public Void visitNot(NotContext ctx) {
        // ctx.B → boolNot (expressão a ser negada — recursão à direita)
        //
        // IMPLEMENTAR:
        //   visit(ctx.B)
        //   code: not
        return null;
    }

    @Override
    public Void visitNotPassthrough(NotPassthroughContext ctx) {
        // Passthrough: propaga para boolAtom sem gerar código
        //
        // IMPLEMENTAR:
        //   visit(ctx.boolAtom())
        return null;
    }

    // =========================================================================
    // ÁTOMOS BOOLEANOS
    // =========================================================================

    @Override
    public Void visitBoolParenteses(BoolParentesesContext ctx) {
        // ctx.NESTED → boolExpr interna (label definido no .g4 como NESTED=boolExpr)
        // Não gera instrução — precedência já resolvida pela gramática
        //
        // IMPLEMENTAR:
        //   visit(ctx.NESTED)
        return null;
    }

    @Override
    public Void visitComparacao(ComparacaoContext ctx) {
        // ctx.O1  → expr (operando esquerdo)
        // ctx.O2  → expr (operando direito)
        // ctx.OP  → token com o operador de comparação
        //
        // IMPLEMENTAR:
        //   visit(ctx.O1)
        //   visit(ctx.O2)
        //   switch ctx.OP.getText():
        //     "<"  → code: let   (less than)
        //     ">"  → code: grt   (greater than)
        //     "<=" → code: lte   (less or equal)
        //     ">=" → code: gte   (greater or equal)
        //     "==" → code: equ   (equal)
        //     "!=" → code: neq   (not equal)
        return null;
    }
}