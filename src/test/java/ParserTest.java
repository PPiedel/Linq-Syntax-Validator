import org.junit.Test;

import java.util.Scanner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class ParserTest {


    @Test
    public void assigningNumberIsValid() throws TokenizerError {
        String input = "var pawel = 5;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.valid());
    }

    @Test
    public void assigningNullIsValid() throws TokenizerError {
        String input = "var contoso = null;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.valid());
    }

    @Test
    public void assigningWithoutRightSideIsNOTValid() throws TokenizerError {
        String input = "var contoso = ";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertFalse(parser.valid());
    }

    @Test
    public void assigningWithoutLeftSideIsNOTValid() throws TokenizerError {
        String input = "= null;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertFalse(parser.valid());
    }

    @Test
    public void twoSimpleCommandsAreValid() throws TokenizerError {
        String input = "var t123 = 456; var employee = 6;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.valid());
    }

    @Test
    public void twoCommandsWithOneNotValidAreNOTValid() throws TokenizerError {
        String input = "var t123 = 456; var employee = ;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertFalse(parser.valid());
    }

    @Test
    public void equation() {
    }

    @Test
    public void eqValue() {
    }

    @Test
    public void value() {
    }

    @Test
    public void pollCurrentToken() {
    }
}
