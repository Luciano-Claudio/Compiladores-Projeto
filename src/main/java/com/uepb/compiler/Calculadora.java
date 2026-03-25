package com.uepb.compiler;

import java.util.ArrayList;

import com.uepb.ExprBaseVisitor;
import com.uepb.ExprParser.AndContext;
import com.uepb.ExprParser.AndPassthroughContext;
import com.uepb.ExprParser.AtomPassthroughContext;
import com.uepb.ExprParser.AtribuicaoContext;
import com.uepb.ExprParser.BoolParentesesContext;
import com.uepb.ExprParser.ComparacaoContext;
import com.uepb.ExprParser.DeclVarContext;
import com.uepb.ExprParser.ElseClauseContext;
import com.uepb.ExprParser.ElseifClauseContext;
import com.uepb.ExprParser.FactorPassthroughContext;
import com.uepb.ExprParser.IfStatContext;
import com.uepb.ExprParser.InputContext;
import com.uepb.ExprParser.MulDivContext;
import com.uepb.ExprParser.NotContext;
import com.uepb.ExprParser.NotPassthroughContext;
import com.uepb.ExprParser.NumeroContext;
import com.uepb.ExprParser.OrContext;
import com.uepb.ExprParser.OrPassthroughContext;
import com.uepb.ExprParser.ParentesesContext;
import com.uepb.ExprParser.PotenciaContext;
import com.uepb.ExprParser.PrintStatContext;
import com.uepb.ExprParser.PrintStrStatContext;
import com.uepb.ExprParser.ProgContext;
import com.uepb.ExprParser.SomaSubContext;
import com.uepb.ExprParser.TermPassthroughContext;
import com.uepb.ExprParser.UsoVariavelContext;
import com.uepb.ExprParser.WhileStatContext;

public class Calculadora extends ExprBaseVisitor<Void> {

    private final ScopeControl scopes = new ScopeControl();
    private final MemoryMapper mapper = new MemoryMapper();
    private final PCodeWriter pcode = new PCodeWriter();
    private int labelCounter = 0;

    private String createLabel() {
        return "L" + labelCounter++;
    }

    public String getCode() {
        return pcode.getCode();
    }

    // =========================================================================
    // PROGRAMA
    // =========================================================================

    @Override
    public Void visitProg(ProgContext ctx) {
        // ctx.stat() retorna a lista completa de statements
        for (var stat : ctx.stat()) {
            visit(stat);
        }
        pcode.hlt();
        return null;
    }

    // =========================================================================
    // STATEMENTS
    // =========================================================================

    @Override
    public Void visitDeclVar(DeclVarContext ctx) {
        var nome  = ctx.ID().getText();
        var tk    = ctx.ID().getSymbol();
        var scope = scopes.getCurrentScope();

        if (scope.exists(nome)) {
            throw new RuntimeException(
                "Variável '%s' já declarada (linha %d, coluna %d)."
                .formatted(nome, tk.getLine(), tk.getCharPositionInLine())
            );
        }

        var addr = mapper.alloc();
        scope.insert(nome, addr);

        // Padrão: push $addr → valor → sto
        pcode.pushAddress(addr);
        if (ctx.expr() != null) {
            visit(ctx.expr());       // empilha o resultado da expressão
        } else {
            pcode.pushNumber(0);     // var sem valor inicial → padrão 0
        }
        pcode.sto();

        return null;
    }

    @Override
    public Void visitAtribuicao(AtribuicaoContext ctx) {
        var nome = ctx.ID().getText();
        var tk   = ctx.ID().getSymbol();
        var opt  = scopes.lookup(nome);

        if (opt.isEmpty()) {
            throw new RuntimeException(
                "Variável '%s' não declarada (linha %d, coluna %d)."
                .formatted(nome, tk.getLine(), tk.getCharPositionInLine())
            );
        }

        var addr = opt.get().address();

        // push $addr → valor → sto
        pcode.pushAddress(addr);
        visit(ctx.expr());
        pcode.sto();

        return null;
    }

    @Override
    public Void visitIfStat(IfStatContext ctx) {
        var labelFim = createLabel();

        // Um label de desvio por cada bloco else-if
        var labelsElse = new ArrayList<String>();
        for (var ignored : ctx.elseifClause()) {
            labelsElse.add(createLabel());
        }

        // Label do bloco else final (ou labelFim se não houver else)
        var labelElseFinal = ctx.elseClause() != null ? createLabel() : labelFim;

        // ── bloco then ────────────────────────────────────────────────────────
        // Avalia condição; se falsa pula para o primeiro else-if (ou else ou fim)
        visit(ctx.boolExpr());
        pcode.fjp(labelsElse.isEmpty() ? labelElseFinal : labelsElse.get(0));
        for (var s : ctx.stat()) visit(s);
        pcode.ujp(labelFim);

        // ── blocos else if ────────────────────────────────────────────────────
        var clausulas = ctx.elseifClause();
        for (int i = 0; i < clausulas.size(); i++) {
            var elseif = clausulas.get(i);
            pcode.label(labelsElse.get(i));
            visit(elseif.boolExpr());
            // Se falso, pula para o próximo else-if, ou para o else/fim
            var proximo = (i + 1 < labelsElse.size())
                ? labelsElse.get(i + 1)
                : labelElseFinal;
            pcode.fjp(proximo);
            for (var s : elseif.stat()) visit(s);
            pcode.ujp(labelFim);
        }

        // ── bloco else ────────────────────────────────────────────────────────
        if (ctx.elseClause() != null) {
            pcode.label(labelElseFinal);
            for (var s : ctx.elseClause().stat()) visit(s);
        }

        pcode.label(labelFim);
        return null;
    }

    @Override
    public Void visitElseifClause(ElseifClauseContext ctx) {
        // Tratado manualmente dentro do visitIfStat — nunca chamado via visit()
        return null;
    }

    @Override
    public Void visitElseClause(ElseClauseContext ctx) {
        // Tratado manualmente dentro do visitIfStat — nunca chamado via visit()
        return null;
    }

    @Override
    public Void visitWhileStat(WhileStatContext ctx) {
        var lstart = createLabel();
        var lend   = createLabel();

        // LSTART: → avalia condição → fjp LEND → corpo → ujp LSTART → LEND:
        pcode.label(lstart);
        visit(ctx.boolExpr());
        pcode.fjp(lend);
        for (var s : ctx.stat()) visit(s);
        pcode.ujp(lstart);
        pcode.label(lend);

        return null;
    }

    @Override
    public Void visitPrintStat(PrintStatContext ctx) {
        // Imprime resultado de uma expressão numérica/variável/aritmética
        visit(ctx.expr());
        pcode.out();
        return null;
    }

    @Override
    public Void visitPrintStrStat(PrintStrStatContext ctx) {
        // Imprime uma string literal diretamente
        pcode.pushString(ctx.STRING().getText()); // já vem com aspas: "texto"
        pcode.out();
        return null;
    }

    // =========================================================================
    // EXPRESSÕES ARITMÉTICAS — nível 1: + e -
    // =========================================================================

    @Override
    public Void visitSomaSub(SomaSubContext ctx) {
        visit(ctx.O1);  // empilha operando esquerdo
        visit(ctx.O2);  // empilha operando direito
        if (ctx.OP.getText().equals("+")) pcode.add();
        else                              pcode.sub();
        return null;
    }

    @Override
    public Void visitTermPassthrough(TermPassthroughContext ctx) {
        visit(ctx.term());
        return null;
    }

    // =========================================================================
    // EXPRESSÕES ARITMÉTICAS — nível 2: * e /
    // =========================================================================

    @Override
    public Void visitMulDiv(MulDivContext ctx) {
        visit(ctx.O1);  // empilha operando esquerdo (term)
        visit(ctx.O2);  // empilha operando direito  (factor)
        if (ctx.OP.getText().equals("*")) pcode.mul();
        else                              pcode.div();
        return null;
    }

    @Override
    public Void visitFactorPassthrough(FactorPassthroughContext ctx) {
        visit(ctx.factor());
        return null;
    }

    // =========================================================================
    // EXPRESSÕES ARITMÉTICAS — nível 3: ^ (right-assoc)
    // =========================================================================

    @Override
    public Void visitPotencia(PotenciaContext ctx) {
        // Não existe instrução pow nativa no P-code.
        // Implementado como loop de multiplicação com temporários.
        var marker   = mapper.getCurrentAddress(); // salva posição para liberar depois
        var addrBase = mapper.alloc();
        var addrExp  = mapper.alloc();
        var addrAcc  = mapper.alloc();
        var lstart   = createLabel();
        var lend     = createLabel();

        // Salva base
        pcode.pushAddress(addrBase);
        visit(ctx.O1);
        pcode.sto();

        // Salva expoente
        pcode.pushAddress(addrExp);
        visit(ctx.O2);
        pcode.sto();

        // Acumulador = 1
        pcode.pushAddress(addrAcc);
        pcode.pushNumber(1);
        pcode.sto();

        // Loop: enquanto exp > 0 → acc = acc * base; exp = exp - 1
        pcode.label(lstart);
        pcode.load(addrExp);    // carrega exp
        pcode.pushNumber(0);
        pcode.grt();            // exp > 0 ?
        pcode.fjp(lend);        // se falso, sai

        pcode.pushAddress(addrAcc);
        pcode.load(addrAcc);
        pcode.load(addrBase);
        pcode.mul();            // acc * base
        pcode.sto();            // acc = acc * base

        pcode.pushAddress(addrExp);
        pcode.load(addrExp);
        pcode.pushNumber(1);
        pcode.sub();            // exp - 1
        pcode.sto();            // exp = exp - 1

        pcode.ujp(lstart);
        pcode.label(lend);

        pcode.load(addrAcc);    // resultado no topo da pilha

        mapper.restoreTo(marker); // libera os 3 endereços temporários
        return null;
    }

    @Override
    public Void visitAtomPassthrough(AtomPassthroughContext ctx) {
        visit(ctx.atom());
        return null;
    }

    // =========================================================================
    // ÁTOMOS
    // =========================================================================

    @Override
    public Void visitParenteses(ParentesesContext ctx) {
        // Parênteses não geram instrução — a precedência já foi resolvida pela gramática
        visit(ctx.NESTED);
        return null;
    }

    @Override
    public Void visitNumero(NumeroContext ctx) {
        // Preserva o texto original do token (ex: "42" ou "3.14")
        pcode.pushNumberRaw(ctx.NUMBER().getText());
        return null;
    }

    @Override
    public Void visitUsoVariavel(UsoVariavelContext ctx) {
        var nome = ctx.ID().getText();
        var tk   = ctx.ID().getSymbol();
        var opt  = scopes.lookup(nome);

        if (opt.isEmpty()) {
            throw new RuntimeException(
                "Variável '%s' não declarada (linha %d, coluna %d)."
                .formatted(nome, tk.getLine(), tk.getCharPositionInLine())
            );
        }

        // push $addr → lod  (carrega o valor armazenado no endereço)
        pcode.load(opt.get().address());
        return null;
    }

    @Override
    public Void visitInput(InputContext ctx) {
        // 1. Exibe o prompt
        // 2. Lê o valor digitado e empilha
        // O visitDeclVar / visitAtribuicao faz push $addr + sto em seguida
        pcode.pushString(ctx.STRING().getText()); // push "prompt"
        pcode.out();                              // imprime o prompt
        pcode.in();                               // lê e empilha o valor
        return null;
    }

    // =========================================================================
    // EXPRESSÕES BOOLEANAS — nível 1: or
    // =========================================================================

    @Override
    public Void visitOr(OrContext ctx) {
        visit(ctx.O1); // boolExpr esquerdo
        visit(ctx.O2); // boolAnd direito
        pcode.or();
        return null;
    }

    @Override
    public Void visitOrPassthrough(OrPassthroughContext ctx) {
        visit(ctx.boolAnd());
        return null;
    }

    // =========================================================================
    // EXPRESSÕES BOOLEANAS — nível 2: and
    // =========================================================================

    @Override
    public Void visitAnd(AndContext ctx) {
        visit(ctx.O1); // boolAnd esquerdo
        visit(ctx.O2); // boolNot direito
        pcode.and();
        return null;
    }

    @Override
    public Void visitAndPassthrough(AndPassthroughContext ctx) {
        visit(ctx.boolNot());
        return null;
    }

    // =========================================================================
    // EXPRESSÕES BOOLEANAS — nível 3: not
    // =========================================================================

    @Override
    public Void visitNot(NotContext ctx) {
        visit(ctx.B); // boolNot (recursão à direita)
        pcode.not();
        return null;
    }

    @Override
    public Void visitNotPassthrough(NotPassthroughContext ctx) {
        visit(ctx.boolAtom());
        return null;
    }

    // =========================================================================
    // ÁTOMOS BOOLEANOS
    // =========================================================================

    @Override
    public Void visitBoolParenteses(BoolParentesesContext ctx) {
        // Parênteses booleanos não geram instrução
        visit(ctx.NESTED);
        return null;
    }

    @Override
    public Void visitComparacao(ComparacaoContext ctx) {
        visit(ctx.O1); // expr esquerdo
        visit(ctx.O2); // expr direito
        // O manual define: grt = Y > X, let = Y < X (penúltimo op topo)
        // Como visitamos O1 antes de O2: O1 fica como Y (penúltimo), O2 como X (topo)
        // Então: O1 > O2 → grt, O1 < O2 → let — ordem correta
        switch (ctx.OP.getText()) {
            case "<"  -> pcode.let();
            case ">"  -> pcode.grt();
            case "<=" -> pcode.lte();
            case ">=" -> pcode.gte();
            case "==" -> pcode.equ();
            case "!=" -> pcode.neq();
        }
        return null;
    }
}