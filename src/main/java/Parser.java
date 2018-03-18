import javafx.scene.Parent;

public class Parser {
    private Tokenizer tokenizer;
    private Token currentToken;

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        try {
            currentToken = tokenizer.nextToken();
        } catch (TokenizerError error) {
            tokenizerError();
        }

    }

    public void tokenizerError() {
        System.out.println("Tokenizer error with token " + currentToken.toString());
    }

    public void parserError() {
        System.out.println("Parser error with token " + currentToken.toString());
    }

    public boolean validate() {
        boolean valid = false;
        try {
            commands();
            valid = true;
        } catch (ParserError error) {
            parserError();
        }
        return valid;
    }

    protected void commands() throws ParserError {
        if (currentToken.getTokenType() == TokenType.EOF) {
            pollCurrentToken(TokenType.EOF);
        } else {
            command();
            commands();
        }
    }

    protected void command() throws ParserError {
        switch (currentToken.getTokenType()) {
            case VARIABLE:
                pollCurrentToken(TokenType.VARIABLE);
                equation();
                pollCurrentToken(TokenType.SEMICOLN);
                break;
            default:
                throw new ParserError("Parser error");
        }
    }


    public void equation() throws ParserError {
        pollCurrentToken(TokenType.ID);
        pollCurrentToken(TokenType.EQUALS);
        eqValue();
    }

    public void eqValue() throws ParserError {
        if (currentToken.getTokenType() == TokenType.NULL) {
            pollCurrentToken(TokenType.NULL);
        } else if (nextTokenType() == TokenType.DOT && nextTokenType() != null) {
            objectField();
        } else {
            value();
        }

    }

    private TokenType nextTokenType() {
        if (currentToken.getTokenNumber() + 1 < tokenizer.getTokens().size()) {
            return tokenizer.getTokens().get(currentToken.getTokenNumber() + 1).getTokenType();
        } else return null;
    }

    public void value() throws ParserError {
        switch (currentToken.getTokenType()) {
            case NUMBER: {
                pollCurrentToken(TokenType.NUMBER);
                break;
            }
            case FROM: {
                simpleQueryCommand();
                break;
            }
            case OPENING_BRACKET: {
                complexQueryCommand();
                break;
            }
            case ID: {
                pollCurrentToken(TokenType.ID);
                break;
            }

            default: {
                throw new ParserError("Parser error");
            }
        }
    }

    public void simpleQueryCommand() throws ParserError {
        queryBegin();
        queryBody();
        orderBy();
        select();
    }

    public void queryBegin() throws ParserError {
        pollCurrentToken(TokenType.FROM);
        pollCurrentToken(TokenType.ID);
        pollCurrentToken(TokenType.IN);
        collection();
    }

    public void collection() throws ParserError {
        objectField();
    }

    public void queryBody() throws ParserError {
        queries();
    }

    public void queries() throws ParserError {
        if (currentToken.getTokenType() == TokenType.WHERE) {
            query();
            queries();
        }
    }

    public void query() throws ParserError {
        pollCurrentToken(TokenType.WHERE);
        expr();
        whereExprContinuation();
    }

    public void expr() throws ParserError {
        switch (currentToken.getTokenType()) {
            case NEGATION: {
                pollCurrentToken(TokenType.NEGATION);
                objectField();
                oneComponentExprRest();
                break;
            }
            case ID: {
                pollCurrentToken(TokenType.ID);
                fieldAccesses();
                break;
            }
        }
    }

    public void objectField() throws ParserError {
        pollCurrentToken(TokenType.ID);
        fieldAccesses();
    }

    public void fieldAccesses() throws ParserError {
        if (currentToken.getTokenType() == TokenType.DOT && nextTokenType() == TokenType.ID && nextTokenType() != null) {
            fieldAccess();
            fieldAccesses();
        }
    }

    public void fieldAccess() throws ParserError {
        pollCurrentToken(TokenType.DOT);
        pollCurrentToken(TokenType.ID);
    }

    public void oneComponentExprRest() throws ParserError {
        if (currentToken.getTokenType() == TokenType.DOT) {
            pollCurrentToken(TokenType.DOT);
            pollCurrentToken(TokenType.EQUALS_TERM);
            pollCurrentToken(TokenType.OPENING_BRACKET);
            eqValue();
            pollCurrentToken(TokenType.CLOSING_BRACKET);

        }

    }

    public void objectMethod() throws ParserError {
        equalsMethod();
    }

    public void equalsMethod() throws ParserError {
        pollCurrentToken(TokenType.EQUALS_TERM);
        pollCurrentToken(TokenType.OPENING_BRACKET);
        eqValue();
        pollCurrentToken(TokenType.CLOSING_BRACKET);
    }

    public void whereExprContinuation() throws ParserError {
        if (currentToken.getTokenType() == TokenType.LOGICAL_OPERATOR) {
            pollCurrentToken(TokenType.LOGICAL_OPERATOR);
            expr();
        }
    }

    public void orderBy() throws ParserError {
        if (currentToken.getTokenType() == TokenType.ORDER_BY) {
            pollCurrentToken(TokenType.ORDER_BY);
            objectField();
            orderProperty();
        }
    }

    public void orderProperty() throws ParserError {
        if (currentToken.getTokenType() == TokenType.ASCENDING) {
            pollCurrentToken(TokenType.ASCENDING);
        } else if (currentToken.getTokenType() == TokenType.DESCENDING) {
            pollCurrentToken(TokenType.DESCENDING);
        }
    }

    public void select() throws ParserError {
        pollCurrentToken(TokenType.SELECT);
        selectExpression();
    }

    public void selectExpression() throws ParserError {
        if (currentToken.getTokenType() == TokenType.NEW) {
            newSelectExpr();
        } else {
            value();
        }
    }

    public void newSelectExpr() throws ParserError {
        pollCurrentToken(TokenType.NEW);
        pollCurrentToken(TokenType.OPENING_CURLING_BRACKET);
        equations();
        pollCurrentToken(TokenType.CLOSING_CURLING_BRACKET);
    }

    public void equations() throws ParserError {
        equation();
        equationCont();
    }

    public void equationCont() throws ParserError {
        if (currentToken.getTokenType() == TokenType.COMMA) {
            pollCurrentToken(TokenType.COMMA);
            equations();
        }
    }


    public void complexQueryCommand() throws ParserError {
        pollCurrentToken(TokenType.OPENING_BRACKET);
        simpleQueryCommand();
        pollCurrentToken(TokenType.CLOSING_BRACKET);
        methods();
    }

    public void methods() throws ParserError {
        if (currentToken.getTokenType() == TokenType.DOT) {
            pollCurrentToken(TokenType.DOT);
            method();
            methods();
        }
    }

    public void method() throws ParserError {
        switch (currentToken.getTokenType()) {
            case MIN: {
                pollCurrentToken(TokenType.MIN);
                pollCurrentToken(TokenType.OPENING_BRACKET);
                lambdaExpr();
                pollCurrentToken(TokenType.CLOSING_BRACKET);
                break;
            }
            case MAX: {
                pollCurrentToken(TokenType.MAX);
                pollCurrentToken(TokenType.OPENING_BRACKET);
                lambdaExpr();
                pollCurrentToken(TokenType.CLOSING_BRACKET);
                break;
            }
            case FIRST_OR_DEFAULT: {
                pollCurrentToken(TokenType.FIRST_OR_DEFAULT);
                pollCurrentToken(TokenType.OPENING_BRACKET);
                lambdaExpr();
                pollCurrentToken(TokenType.CLOSING_BRACKET);
                break;
            }
            case ORDER_BY_DESCENDING: {
                pollCurrentToken(TokenType.ORDER_BY_DESCENDING);
                pollCurrentToken(TokenType.OPENING_BRACKET);
                lambdaExpr();
                pollCurrentToken(TokenType.CLOSING_BRACKET);
                break;
            }
        }
    }

    public void lambdaExpr() throws ParserError {
        if (currentToken.getTokenType() == TokenType.ID) {
            pollCurrentToken(TokenType.ID);
            pollCurrentToken(TokenType.LAMBDA_OPERATOR);
            expr();
        }
    }

    public void pollCurrentToken(TokenType tokenType) throws ParserError {
        if (currentToken.getTokenType() != tokenType) {
            throw new ParserError("Parser error");
        } else if (currentToken.getTokenType() != TokenType.EOF) {
            tryToTakeNextToken();
        }
    }

    private void tryToTakeNextToken() {
        try {
            currentToken = tokenizer.nextToken();
        } catch (TokenizerError error) {
            tokenizerError();
        }
    }


}
