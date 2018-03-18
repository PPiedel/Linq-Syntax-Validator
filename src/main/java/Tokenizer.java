import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    public static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-z0-9_]*");
    public static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
    private static final LinkedHashMap<String, TokenType> keywords = new LinkedHashMap<>();
    private static final HashMap<String, TokenType> characters = new HashMap<>();
    private String input;
    private int line;
    private int column;
    private int currentTokenNumber;
    private List<Token> tokens;

    static {
        keywords.put("var", TokenType.VARIABLE);
        keywords.put("from", TokenType.FROM);
        keywords.put("int", TokenType.DATA_TYPE);
        keywords.put("in", TokenType.IN);
        keywords.put("where", TokenType.WHERE);
        keywords.put("FirstOrDefault", TokenType.FIRST_OR_DEFAULT);
        keywords.put("Equals", TokenType.EQUALS_TERM);
        keywords.put("orderby", TokenType.ORDER_BY);
        keywords.put("ascending", TokenType.ASCENDING);
        keywords.put("descending", TokenType.DESCENDING);
        keywords.put("select", TokenType.SELECT);
        keywords.put("null", TokenType.NULL);
        keywords.put("new", TokenType.NEW);

        keywords.put("Min", TokenType.MIN);
        keywords.put("Max", TokenType.MAX);
        keywords.put("OrderByDescending", TokenType.ORDER_BY_DESCENDING);

        characters.put(")", TokenType.CLOSING_BRACKET);
        characters.put("(", TokenType.OPENING_BRACKET);
        characters.put("{", TokenType.OPENING_CURLING_BRACKET);
        characters.put("}", TokenType.CLOSING_CURLING_BRACKET);
        characters.put("[", TokenType.OPENING_SQUARE_BRACKET);
        characters.put("]", TokenType.CLOSING_SQUARE_BRACKET);
        characters.put("=", TokenType.EQUALS);
        characters.put(";", TokenType.SEMICOLN);
        characters.put(".", TokenType.DOT);
        characters.put("\"", TokenType.QUOTATION_MARK);
        characters.put("!", TokenType.NEGATION);
        characters.put("=>", TokenType.LAMBDA_OPERATOR);
        characters.put(",", TokenType.COMMA);
        characters.put("&&", TokenType.LOGICAL_OPERATOR);
        characters.put("||", TokenType.LOGICAL_OPERATOR);
        characters.put(">", TokenType.OPERATOR);
        characters.put("<", TokenType.OPERATOR);
        characters.put("<=", TokenType.OPERATOR);
        characters.put(">=", TokenType.OPERATOR);
        characters.put("==", TokenType.OPERATOR);

    }

    public Tokenizer(String input) {
        this.input = input;
        tokens = new ArrayList<>();
        line = 1;
        column = 1;
        currentTokenNumber = 0;
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
            //try to match characters
            boolean tokened = false;
            Set<Entry<String, TokenType>> entrySet = characters.entrySet();
            Iterator<Entry<String, TokenType>> iterator = entrySet.iterator();
            while (iterator.hasNext() && !tokened) {
                Entry<String, TokenType> entry = iterator.next();
                tokened = tryToMatchCharacter(entry.getKey(), entry.getValue());
            }

            if (!tokened) {
                tokened = tryToMatchRegex(NUMBER_PATTERN, TokenType.NUMBER) || tryToMatchKeywords() || tryToMatchRegex(ID_PATTERN, TokenType.ID);
            }

            if (!tokened) {
                throw new TokenizerError("Tokenizer error at line " + line + " at column " + column + "\nInput not matched:" + input);
            }
        }

        skipBeginningWhiteSpaces();

    }

    public Token nextToken() throws TokenizerError {
        currentTokenNumber++;
        if (currentTokenNumber-1 < tokens.size()) {
            return tokens.get(currentTokenNumber-1);

        } else {
            return new Token(TokenType.EOF, "EOF", line, column + 1);
        }
    }


    public boolean tryToMatchKeywords() {
        boolean matched = false;
        Set<Entry<String, TokenType>> entrySet = keywords.entrySet();
        Iterator<Entry<String, TokenType>> iterator = entrySet.iterator();
        while (iterator.hasNext() && !matched) {
            Entry<String, TokenType> entry = iterator.next();
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
