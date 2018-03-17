import java.util.Objects;

public final class Token {
    private final TokenType tokenType;
    private final String value;
    private final int line;
    private final int column;

    public Token(TokenType tokenType, String value, int line, int column) {
        this.tokenType = tokenType;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return (line == token.line) &&
                (column == token.column) &&
                (tokenType == token.tokenType) &&
                Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenType, value, line, column);
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenType=" + tokenType +
                ", value='" + value + '\'' +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}
