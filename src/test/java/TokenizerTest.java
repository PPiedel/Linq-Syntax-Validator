import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TokenizerTest {
    @Test
    public void skipWhiteSpacesShouldSkipFirst5WhiteSpaces() {
        String input = "     abc";
        Tokenizer tokenizer = new Tokenizer(input);

        int skipped = tokenizer.skipBeginningWhiteSpaces();

        assertEquals(5, skipped);
    }

    @Test
    public void skipWhiteSpacesShouldSkipFirst4WhiteSpaces() {
        String input = "    abc\n";
        Tokenizer tokenizer = new Tokenizer(input);

        int skipped = tokenizer.skipBeginningWhiteSpaces();

        assertEquals(4, skipped);
    }

    @Test
    public void skipWhiteSpacesShouldSkipFirst1WhiteSpaces() {
        String input = "\nabcsdfsdffrom()fsdfsd$325243634dsnfkasdhfisda\n";
        Tokenizer tokenizer = new Tokenizer(input);

        int skipped = tokenizer.skipBeginningWhiteSpaces();

        assertEquals(1, skipped);
    }

    @Test
    public void skipWhiteSpacesShouldSkipZeroWhiteSpaces() {
        String input = "var query_orderby1 = from c in svcContext.ContactSet";
        Tokenizer tokenizer = new Tokenizer(input);

        int skipped = tokenizer.skipBeginningWhiteSpaces();

        assertEquals(0, skipped);
    }

    @Test
    public void consumeCharactersShouldReturnCorrectNumber() {
        String input = "\n\nquery_orderby1";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.consumeCharacters(16);

        assertEquals(3, tokenizer.getLine());
        assertEquals(14, tokenizer.getColumn());

        assertEquals("", tokenizer.getInput());
    }

    @Test
    public void consumeCharactersShouldDoNothing() {
        String input = "";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.consumeCharacters(10);

        assertEquals(1, tokenizer.getLine());
        assertEquals(1, tokenizer.getColumn());
    }

    @Test
    public void tryToMatchRegexShouldMatchIdPattern() {
        String input = "a5cd = from c in svcContext.ContactSet";
        Tokenizer tokenizer = new Tokenizer(input);
        boolean matched = tokenizer.tryToMatchRegex(Tokenizer.ID_PATTERN, TokenType.ID);
        assertTrue(matched);
    }

    @Test
    public void tryToMatchRegexShouldMatchNumberPattern() {
        String input = "6345345 = from c in svcContext.ContactSet";
        Tokenizer tokenizer = new Tokenizer(input);
        boolean matched = tokenizer.tryToMatchRegex(Tokenizer.NUMBER_PATTERN, TokenType.NUMBER);
        assertTrue(matched);
    }

    @Test
    public void tryToMatchRegexShouldMatchOpeningCurlingPattern() {
        String input = "(6345345 = from c in svcContext.ContactSet";
        Tokenizer tokenizer = new Tokenizer(input);
        boolean matched = tokenizer.tryToMatchCharacter("(", TokenType.OPENING_BRACKET);
        assertTrue(matched);
    }

    @Test
    public void tokenizeTestShouldTokenizeOk1() {
        String input = "var query_orderby1 = from c in svcContext.ContactSet";
        Tokenizer tokenizer = new Tokenizer(input);
        try {
            tokenizer.tokenize();
            assertEquals(9, tokenizer.getTokens().size());
        } catch (TokenizerError error) {
            System.out.println(error.getMessage());
        }

    }

    @Test
    public void tokenizeTestShouldTokenizeOk() {
        String input = "from c in svcContext.ContactSet\n" +
                "where c.CreditLimit.Equals(null)\n" +
                "orderby c.CreditLimit descending\n\n" +
                "select new";
        Tokenizer tokenizer = new Tokenizer(input);
        try {
            tokenizer.tokenize();
            assertEquals(22, tokenizer.getTokens().size());
        } catch (TokenizerError error) {
            System.out.println(error.getMessage());
        }

    }

    @Test
    public void variableAssignTokenizeTest(){
        String input = "var pawwel = 5;";
        Tokenizer tokenizer = new Tokenizer(input);
        try {
            tokenizer.tokenize();
            assertEquals(5, tokenizer.getTokens().size());
        } catch (TokenizerError error) {
            System.out.println(error.getMessage());
        }
    }

    @Test
    public void simpleQueryTokenizeTest(){
        String input = "var query_where1 = from a in svcContext.AccountSet\n" +
                "                    where a.Name.Contains(id)\n" +
                "                    select a;";
        Tokenizer tokenizer = new Tokenizer(input);
        try {
            tokenizer.tokenize();
            assertEquals(21, tokenizer.getTokens().size());
        } catch (TokenizerError error) {
            System.out.println(error.getMessage());
        }
    }

    @Test
    public void orderByTokenizerTest(){
        String input = " var sortedWords = \n" +
                "        from w in words \n" +
                "        orderby w.field \n" +
                "        select w;";
        Tokenizer tokenizer = new Tokenizer(input);
        try {
            tokenizer.tokenize();
            assertEquals(14, tokenizer.getTokens().size());
        } catch (TokenizerError error) {
            System.out.println(error.getMessage());
        }

    }

    @Test
    public void MaxTokenizerTest(){
        String input = "int[] numbers = { 5, 4, 1, 3, 9, 8, 6, 7, 2, 0 }; \n" +
                "  \n" +
                "    int maxNum = numbers.Max();";
        Tokenizer tokenizer = new Tokenizer(input);
        try {
            tokenizer.tokenize();
            assertEquals(36, tokenizer.getTokens().size());
        } catch (TokenizerError error) {
            System.out.println(error.getMessage());
        }
    }

    @Test
    public void firstOrDefaultTokenizerTest(){
        String input = "int firstNumOrDefault = numbers.FirstOrDefault();";
        Tokenizer tokenizer = new Tokenizer(input);
        try {
            tokenizer.tokenize();

            for (Token token : tokenizer.getTokens()){
                System.out.println(token);
            }
            assertEquals(9, tokenizer.getTokens().size());
        } catch (TokenizerError error) {
            System.out.println(error.getMessage());
        }
    }

    @Test
    public void stringTokenizerTest() {
        String input = "int firstOrDefault = \"abcd\"";
        Tokenizer tokenizer = new Tokenizer(input);
        try {
            tokenizer.tokenize();
            assertEquals(4, tokenizer.getTokens().size());
            assertEquals(TokenType.STRING,tokenizer.getTokens().get(3).getTokenType());
        } catch (TokenizerError error) {
            System.out.println(error.getMessage());
        }
    }


}

