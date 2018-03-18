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
        return start();
    }

    public boolean start() {
        boolean valid = false;
        switch (currentToken.getTokenType()) {
            case VARIABLE:
                equation();
                try {
                    pollCurrentToken(TokenType.SEMICOLN);
                    //current token should be EOF
                    valid = true;
                } catch (ParserError error) {
                    parserError();
                }

                break;
            default:
                parserError();
        }
        return valid;
    }

    public void equation() {
        try {
            pollCurrentToken(TokenType.VARIABLE); //var
            pollCurrentToken(TokenType.ID); //costam
            pollCurrentToken(TokenType.EQUALS); //=
            eqValue(); //np. null
        } catch (ParserError error) {
            parserError();
        }
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
        } else {
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
