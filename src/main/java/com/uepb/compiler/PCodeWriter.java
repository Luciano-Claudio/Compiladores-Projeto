package com.uepb.compiler;

public class PCodeWriter {

    private final StringBuilder code = new StringBuilder();

    // =========================================================================
    // MEMÓRIA
    // =========================================================================

    /** push $N — empilha o endereço de memória N */
    public void pushAddress(int address) {
        code.append("push $").append(address).append("\n");
    }

    /** push N — empilha um literal numérico */
    public void pushNumber(double value) {
        // Remove o .0 se for número inteiro (ex: 5.0 → 5)
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            code.append("push ").append((long) value).append("\n");
        } else {
            code.append("push ").append(value).append("\n");
        }
    }

    /** push N — empilha um literal numérico já em formato String (preserva o texto original do token) */
    public void pushNumberRaw(String value) {
        code.append("push ").append(value).append("\n");
    }

    /** push "texto" — empilha uma string literal (já com aspas) */
    public void pushString(String valueWithQuotes) {
        code.append("push ").append(valueWithQuotes).append("\n");
    }

    /** lod — substitui o endereço no topo da pilha pelo valor armazenado naquele endereço */
    public void lod() {
        code.append("lod\n");
    }

    /** sto — retira X e A do topo; armazena X no endereço A */
    public void sto() {
        code.append("sto\n");
    }

    /** dup — duplica o valor no topo da pilha */
    public void dup() {
        code.append("dup\n");
    }

    /** swap — troca os dois valores do topo da pilha */
    public void swap() {
        code.append("swap\n");
    }

    // =========================================================================
    // I/O
    // =========================================================================

    /** out — retira o topo da pilha e imprime */
    public void out() {
        code.append("out\n");
    }

    /** in — lê um valor do teclado e empilha */
    public void in() {
        code.append("in\n");
    }

    // =========================================================================
    // ARITMÉTICA
    // =========================================================================

    /** add — soma os dois valores do topo */
    public void add() {
        code.append("add\n");
    }

    /** sub — subtrai: penúltimo - topo */
    public void sub() {
        code.append("sub\n");
    }

    /** mul — multiplica os dois valores do topo */
    public void mul() {
        code.append("mul\n");
    }

    /** div — divide: penúltimo / topo */
    public void div() {
        code.append("div\n");
    }

    // =========================================================================
    // COMPARAÇÃO  (retornam booleano na pilha)
    // =========================================================================

    /** grt — greater than: penúltimo > topo */
    public void grt() {
        code.append("grt\n");
    }

    /** let — less than: penúltimo < topo */
    public void let() {
        code.append("let\n");
    }

    /** gte — greater or equal: penúltimo >= topo */
    public void gte() {
        code.append("gte\n");
    }

    /** lte — less or equal: penúltimo <= topo */
    public void lte() {
        code.append("lte\n");
    }

    /** equ — equal: penúltimo == topo */
    public void equ() {
        code.append("equ\n");
    }

    /** neq — not equal: penúltimo != topo */
    public void neq() {
        code.append("neq\n");
    }

    // =========================================================================
    // LÓGICA
    // =========================================================================

    /** and — AND lógico dos dois booleanos do topo */
    public void and() {
        code.append("and\n");
    }

    /** or — OR lógico dos dois booleanos do topo */
    public void or() {
        code.append("or\n");
    }

    /** not — nega o booleano no topo */
    public void not() {
        code.append("not\n");
    }

    // =========================================================================
    // DESVIOS
    // =========================================================================

    /** ujp L — salto incondicional para o label L */
    public void ujp(String label) {
        code.append("ujp ").append(label).append("\n");
    }

    /** fjp L — retira booleano do topo; se falso, salta para o label L */
    public void fjp(String label) {
        code.append("fjp ").append(label).append("\n");
    }

    /** L: — define um label no ponto atual do código */
    public void label(String name) {
        code.append(name).append(":\n");
    }

    // =========================================================================
    // CONTROLE
    // =========================================================================

    /** hlt — encerra a execução do programa */
    public void hlt() {
        code.append("hlt\n");
    }

    // =========================================================================
    // ATALHOS COMPOSTOS
    // =========================================================================

    /**
     * Atalho: carrega o valor de uma variável pelo endereço.
     * Equivale a:  push $address + lod
     */
    public void load(int address) {
        pushAddress(address);
        lod();
    }

    /**
     * Atalho: armazena o topo da pilha em um endereço.
     * Equivale a:  push $address (antes do valor já estar na pilha) + sto
     * ATENÇÃO: o valor a ser armazenado deve já estar na pilha antes de chamar este método.
     * Use pushAddress(address) + [código que gera o valor] + sto() se precisar de controle manual.
     */
    public void store(int address) {
        pushAddress(address);
        swap();
        sto();
    }

    // =========================================================================
    // SAÍDA
    // =========================================================================

    /** Retorna o código P-code gerado até o momento */
    public String getCode() {
        return code.toString();
    }
}