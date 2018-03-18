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
            default: {
                throw new ParserError("Parser error");
            }
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
