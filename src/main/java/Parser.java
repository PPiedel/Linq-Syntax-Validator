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

    public boolean valid() {
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
        boolean valid = false;
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
                equation();
                pollCurrentToken(TokenType.SEMICOLN);
                break;
            default:
                throw new ParserError("Parser error");
        }
    }


    public void equation() throws ParserError {
        pollCurrentToken(TokenType.VARIABLE); //var
        pollCurrentToken(TokenType.ID); //costam
        pollCurrentToken(TokenType.EQUALS); //=
        eqValue(); //np. null

    }

    public void eqValue() throws ParserError {
        if (currentToken.getTokenType() == TokenType.NULL) {
            pollCurrentToken(TokenType.NULL);
        } else {
            value();
        }

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
            case ID : {
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
        pollCurrentToken(TokenType.ID); //TODO not only ids
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
            }
            case ID: {
                pollCurrentToken(TokenType.ID);
                fieldAccesses();
            }
        }
    }

    public void objectField() throws ParserError {
        pollCurrentToken(TokenType.ID);
        fieldAccesses();
    }

    public void fieldAccesses() throws ParserError {
        if (currentToken.getTokenType() == TokenType.DOT) {
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
            objectMethod();
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
            value();
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
        value(); //na razie tylko value
    }

    public void complexQueryCommand() throws ParserError {
        pollCurrentToken(TokenType.OPENING_BRACKET);
    }

    public void methods() {

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
