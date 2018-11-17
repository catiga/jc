package com.jeancoder.jdbc.sql;

public enum Symbol {

	// literal general
    LPAREN("("), 
    RPAREN(")"), 
    LBRACE("{"), 
    RBRACE("}"), 
    LBRACKET("["), 
    RBRACKET("]"), 
    SEMI(";"), 
    COMMA(","), 
    DOT("."), 
    DOTDOT(".."), 
    DOTDOTDOT("..,"), 
    EQ("="), 
    GT(">"), 
    LT("<"), 
    BANG("!"),
    BANGBANG("!!"),
    BANG_TILDE("!~"),
    BANG_TILDE_STAR("!~*"),
    TILDE("~"),
    TILDE_STAR("~*"),
    TILDE_EQ("~="),
    QUES("?"), 
    COLON(":"), 
    COLONCOLON(":"), 
    COLONEQ(":="), 
    EQEQ("=="), 
    LTEQ("<="), 
    LTEQGT("<=>"), 
    LTGT("<>"), 
    GTEQ(">="), 
    BANGEQ("!="), 
    BANGGT("!>"), 
    BANGLT("!<"),
    AMPAMP("&&"), 
    BARBAR("||"), 
    BARBARSLASH("||/"), 
    BARSLASH("|/"), 
    PLUS("+"), 
    SUB("-"), 
    SUBGT("->"), 
    SUBGTGT("->>"), 
    STAR("*"), 
    SLASH("/"), 
    AMP("&"), 
    BAR("|"), 
    CARET("^"), 
    PERCENT("%"), 
    LTLT("<<"), 
    GTGT(">>"),
    MONKEYS_AT("@"),
    MONKEYS_AT_AT("@@"),
    POUND("#"),
    POUNDGT("#>"),
    POUNDGTGT("#>>"),
    MONKEYS_AT_GT("@>"),
    LT_MONKEYS_AT("<@");
	
	public final String name;

	Symbol(){
        this(null);
    }

	Symbol(String name){
        this.name = name;
    }
    
}
