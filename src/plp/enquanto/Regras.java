package plp.enquanto;

import java.util.ArrayList;
import java.util.List;

import plp.enquanto.Linguagem.*;
import plp.enquanto.parser.EnquantoBaseListener;
import plp.enquanto.parser.EnquantoParser.*;

import java.util.Map;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class Regras extends EnquantoBaseListener {
	private final Leia leia;
	private final Skip skip;
	private final Propriedades valores;

	private Programa programa;

	public Regras() {
		leia = new Leia();
		skip = new Skip();
		valores = new Propriedades();
	}

	public Programa getPrograma() {
		return programa;
	}

	@Override
	public void exitBool(BoolContext ctx) {
		valores.insira(ctx, new Booleano("verdadeiro".equals(ctx.getText())));
	}

	@Override
	public void exitLeia(LeiaContext ctx) {
		valores.insira(ctx, leia);
	}

	@Override
	public void exitSe(SeContext ctx) {
		final Bool condicao = valores.pegue(ctx.booleano(0));
		final Map<Bool, Comando> senaoses = new HashMap<>();
		
		final int booleanoSize = ctx.booleano().size();
		for (int i = 1; i < booleanoSize; i++) {
			senaoses.put(
			valores.pegue(ctx.booleano(i)),
			valores.pegue(ctx.comando(i))
			);
		}
		
		final Comando entao = valores.pegue(ctx.comando(0));
		final Comando senao = valores.pegue(ctx.comando(ctx.comando().size() - 1));
		valores.insira(ctx, new Se(condicao, entao, senaoses, senao));
	}

	public void exitPara(ParaContext ctx) {
		valores.insira(ctx, new Para(
			ctx.ID().getText(),
			valores.pegue(ctx.expressao(0)),
			valores.pegue(ctx.expressao(1)),
			valores.pegue(ctx.comando())
		));
	}

	@Override
	public void exitRepita(RepitaContext ctx) {
		valores.insira(ctx, new Repita(
			valores.pegue(ctx.expressao()),
			valores.pegue(ctx.comando())
		));
	}

	@Override
	public void exitEscolha(EscolhaContext ctx) {
		final Id id = new Id(ctx.ID().getText());
		final Map<Expressao, Comando> escolhas = new HashMap<>();
		final int expressaoSize = ctx.expressao().size();
		for (int i = 0; i < expressaoSize; i++) {
			escolhas.put(
				valores.pegue(ctx.expressao(i)),
				valores.pegue(ctx.comando(i))
			);
		}
		valores.insira(ctx, new Escolha(
			id,
			escolhas,
			valores.pegue(ctx.comando(ctx.comando().size() - 1))
		));
	}

	@Override
	public void exitInteiro(InteiroContext ctx) {
		valores.insira(ctx, new Inteiro(parseInt(ctx.getText())));
	}

	@Override
	public void exitSkip(SkipContext ctx) {
		valores.insira(ctx, skip);
	}

	@Override
	public void exitEscreva(EscrevaContext ctx) {
		final Expressao exp = valores.pegue(ctx.expressao());
		valores.insira(ctx, new Escreva(exp));
	}

	@Override
	public void exitPrograma(ProgramaContext ctx) {
		final List<Comando> cmds = valores.pegue(ctx.seqComando());
		programa = new Programa(cmds);
		valores.insira(ctx, programa);
	}

	@Override
	public void exitId(IdContext ctx) {
		final String id = ctx.ID().getText();
		valores.insira(ctx, new Id(id));
	}

	@Override
	public void exitSeqComando(SeqComandoContext ctx) {
		final List<Comando> comandos = new ArrayList<>();
		for (ComandoContext c : ctx.comando()) {
			comandos.add(valores.pegue(c));
		}
		valores.insira(ctx, comandos);
	}

	@Override
	public void exitAtribuicao(AtribuicaoContext ctx) {
		final String id = ctx.ID().getText();
		final Expressao exp = valores.pegue(ctx.expressao());
		valores.insira(ctx, new Atribuicao(id, exp));
	}

	public void exitAtribuicaoComDeclaracao(AtribuicaoComDeclaracaoContext ctx) {
		final ArrayList<String> ids = new ArrayList<String>();
		final ArrayList<Expressao> expressoes = new ArrayList<Expressao>();

		for (int i = 0; i < ctx.ID().size(); i++) {
			ids.add(ctx.ID(i).getText());
		}

		for (int i = 0; i < ctx.expressao().size(); i++) {
			expressoes.add(valores.pegue(ctx.expressao(i)));
		}

		valores.insira(ctx, new AtribuicaoComDeclaracao(ids, expressoes));
	}

	@Override
	public void exitBloco(BlocoContext ctx) {
		final List<Comando> cmds = valores.pegue(ctx.seqComando());
		valores.insira(ctx, new Bloco(cmds));
	}

	@Override
	public void exitOpBin(OpBinContext ctx) {
		final Expressao esq = valores.pegue(ctx.expressao(0));
		final Expressao dir = valores.pegue(ctx.expressao(1));
		final String op = ctx.getChild(1).getText();
		final Expressao exp = switch (op) {
			case "*" -> new ExpMult(esq, dir);
			case "-" -> new ExpSub(esq, dir);
			case "/" -> new ExpDiv(esq, dir);
			case "^" -> new ExpExpn(esq, dir);
			default  -> new ExpSoma(esq, dir);
		};
		valores.insira(ctx, exp);
	}

	@Override
	public void exitEnquanto(EnquantoContext ctx) {
		final Bool condicao = valores.pegue(ctx.booleano());
		final Comando comando = valores.pegue(ctx.comando());
		valores.insira(ctx, new Enquanto(condicao, comando));
	}

	@Override
	public void exitELogico(ELogicoContext ctx) {
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new ELogico(esq, dir));
	}
	
	@Override
	public void exitOuLogico(OuLogicoContext ctx) {
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new OuLogico(esq, dir));
	}

	@Override
	public void exitOuExclusivoLogico(OuExclusivoLogicoContext ctx) {
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new OuExclusivoLogico(esq, dir));
	}

	@Override
	public void exitBoolPar(BoolParContext ctx) {
		final Bool booleano = valores.pegue(ctx.booleano());
		valores.insira(ctx, booleano);
	}

	@Override
	public void exitNaoLogico(NaoLogicoContext ctx) {
		final Bool b = valores.pegue(ctx.booleano());
		valores.insira(ctx, new NaoLogico(b));
	}

	@Override
	public void exitExpPar(ExpParContext ctx) {
		final Expressao exp = valores.pegue(ctx.expressao());
		valores.insira(ctx, exp);
	}

	@Override
	public void exitExiba(ExibaContext ctx) {
		final String t = ctx.TEXTO().getText();
		final String texto = t.substring(1, t.length() - 1);
		valores.insira(ctx, new Exiba(texto));
	}

	@Override
	public void exitOpRel(OpRelContext ctx) {
		final Expressao esq = valores.pegue(ctx.expressao(0));
		final Expressao dir = valores.pegue(ctx.expressao(1));
		final String op = ctx.getChild(1).getText();
		final Bool exp = switch (op) {
			case "="  -> new ExpIgual(esq, dir);
			case "<=" -> new ExpMenorIgual(esq, dir);
			case ">=" -> new ExpMaiorIgual(esq, dir);
			case ">" -> new ExpMaiorQue(esq, dir);
			case "<" -> new ExpMenorQue(esq, dir);
			default   -> new ExpIgual(esq, esq);
		};
		valores.insira(ctx, exp);
	}
}
