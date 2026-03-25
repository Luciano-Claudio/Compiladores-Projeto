package com.uepb.compiler;

import org.antlr.v4.runtime.tree.ParseTree;

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
    private final StringBuilder code = new StringBuilder();
    private final PCodeWriter pcode = new PCodeWriter();
    private int labelCounter = 0;

    private String createLabel() {
        return "L" + labelCounter++;
    }

    public String getCode() {
        return code.toString();
    }

    @Override
    public Void visitProg(ProgContext ctx) {
        visit(ctx.stat(labelCounter));
        pcode.hlt();
        code.append("out\n");
        code.append("hlt\n");
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
    @Override
    public Void visitPrintStrStat(PrintStrStatContext ctx) {
        return null;
    }
}