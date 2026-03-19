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

    @Override
    public Void visitProg(ProgContext ctx) {
        return null;
    }

    @Override
    public Void visitDeclVar(DeclVarContext ctx) {
        return null;
    }

    @Override
    public Void visitAtribuicao(AtribuicaoContext ctx) {
        return null;
    }

    @Override
    public Void visitIfStat(IfStatContext ctx) {
        return null;
    }

    @Override
    public Void visitElseifClause(ElseifClauseContext ctx) {
        return null;
    }

    @Override
    public Void visitElseClause(ElseClauseContext ctx) {
        return null;
    }

    @Override
    public Void visitWhileStat(WhileStatContext ctx) {
        return null;
    }

    @Override
    public Void visitPrintStat(PrintStatContext ctx) {
        return null;
    }

    @Override
    public Void visitSomaSub(SomaSubContext ctx) {
        return null;
    }

    @Override
    public Void visitTermPassthrough(TermPassthroughContext ctx) {
        return null;
    }

    @Override
    public Void visitMulDiv(MulDivContext ctx) {
        return null;
    }

    @Override
    public Void visitFactorPassthrough(FactorPassthroughContext ctx) {
        return null;
    }

    @Override
    public Void visitPotencia(PotenciaContext ctx) {
        return null;
    }

    @Override
    public Void visitAtomPassthrough(AtomPassthroughContext ctx) {
        return null;
    }

    @Override
    public Void visitParenteses(ParentesesContext ctx) {
        return null;
    }

    @Override
    public Void visitNumero(NumeroContext ctx) {
        return null;
    }

    @Override
    public Void visitUsoVariavel(UsoVariavelContext ctx) {
        return null;
    }

    @Override
    public Void visitStringLiteral(StringLiteralContext ctx) {
        return null;
    }

    @Override
    public Void visitInput(InputContext ctx) {
        return null;
    }

    @Override
    public Void visitOr(OrContext ctx) {
        return null;
    }

    @Override
    public Void visitOrPassthrough(OrPassthroughContext ctx) {
        return null;
    }

    @Override
    public Void visitAnd(AndContext ctx) {
        return null;
    }

    @Override
    public Void visitAndPassthrough(AndPassthroughContext ctx) {
        return null;
    }

    @Override
    public Void visitNot(NotContext ctx) {
        return null;
    }

    @Override
    public Void visitNotPassthrough(NotPassthroughContext ctx) {
        return null;
    }

    @Override
    public Void visitBoolParenteses(BoolParentesesContext ctx) {
        return null;
    }

    @Override
    public Void visitComparacao(ComparacaoContext ctx) {
        return null;
    }
}