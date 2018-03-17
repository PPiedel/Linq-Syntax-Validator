import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    public static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-z0-9_]*");
    public static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
    public static final HashMap<String, TokenType> keywords = new HashMap<>();

    private String input;
    private int line;
    private int column;
    private List<Token> tokens;

    static {
        keywords.put("var", TokenType.VARIABLE);
        keywords.put("from", TokenType.FROM);
        keywords.put("in", TokenType.IN);
        keywords.put("where", TokenType.WHERE);
        keywords.put("orderby", TokenType.ORDER_BY);
        keywords.put("descending", TokenType.DESCENDING);
        keywords.put("select", TokenType.SELECT);
        keywords.put("null", TokenType.NULL);
        keywords.put("new", TokenType.NEW);
        keywords.put("Equals", TokenType.EQUALS_TERM);
    }

    public Tokenizer(String input) {
        this.input = input;
        tokens = new ArrayList<>();
        line = 1;
        column = 1;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getInput() {
        return input;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void tokenize() throws TokenizerError {
        skipBeginningWhiteSpaces();

        while (!input.isEmpty()) {
            boolean tokened =
                    tryToMatchCharacter(")", TokenType.OPENING_BRACKET)
                            || tryToMatchCharacter("(", TokenType.CLOSING_BRACKET)
                            || tryToMatchCharacter("{", TokenType.OPENING_CURLING_BRACKET)
                            || tryToMatchCharacter("}", TokenType.CLOSING_CURLING_BRACKET)
                            || tryToMatchCharacter("[", TokenType.OPENING_SQUARE_BRACKET)
                            || tryToMatchCharacter("]", TokenType.CLOSING_SQUARE_BRACKET)
                            || tryToMatchCharacter("=", TokenType.EQUALS)
                            || tryToMatchCharacter(";", TokenType.SEMICOLN)
                            || tryToMatchCharacter(".", TokenType.DOT)
                            || tryToMatchCharacter("\"", TokenType.QUOTATION_MARK)
                            || tryToMatchCharacter("!", TokenType.NEGATION)
                            || tryToMatchKeywords()
                            || tryToMatchRegex(NUMBER_PATTERN, TokenType.NUMBER)
                            || tryToMatchRegex(ID_PATTERN, TokenType.ID);


            if (!tokened) {
                throw new TokenizerError("Tokenizer error at line " + line + " at column " + column + "\nInput not matched:" + input);
            }
        }

        skipBeginningWhiteSpaces();

    }

    public boolean tryToMatchKeywords() {
        boolean matched = false;
        Set<Entry<String, TokenType>> entrySet = keywords.entrySet();
        Iterator<Entry<String, TokenType>> iterator = entrySet.iterator();
        while (iterator.hasNext() && !matched){
            Entry<String,TokenType> entry = iterator.next();
            matched = tryToMatchRegex(Pattern.compile(entry.getKey()), keywords.get(entry.getKey()));
        }
        return matched;
    }

    public boolean tryToMatchCharacter(String excpected, TokenType tokenType) {
        boolean matched = false;
        if (input.startsWith(excpected)) {
            matched = true;
            tokens.add(new Token(tokenType, excpected, line, column));
            consumeCharacters(excpected.length());
            skipBeginningWhiteSpaces();
        }
        return matched;
    }

    public boolean tryToMatchRegex(Pattern pattern, TokenType tokenType) {
        Matcher matcher = pattern.matcher(input);
        boolean matched = false;
        if (matcher.lookingAt()) {
            matched = true;
            tokens.add(new Token(tokenType, matcher.group(), line, column));
            consumeCharacters(matcher.end());
            skipBeginningWhiteSpaces();
        }

        return matched;
    }

    public int skipBeginningWhiteSpaces() {
        int i = 0;
        while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
            i++;
        }

        consumeCharacters(i);

        return i;
    }

    public void consumeCharacters(int numberOfChars) {
        for (int i = 0; i < numberOfChars && i < input.length(); i++) {
            char character = input.charAt(i);
            if (character == '\n') {
                line++;
                column = 0;
            } else if (character == '\r') {
                //ignore
            } else {
                column++;
            }
        }
        if (input.length() != 0) {
            input = input.substring(numberOfChars);
        }

    }
}