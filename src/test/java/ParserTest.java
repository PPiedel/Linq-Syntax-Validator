import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class ParserTest {


    @Test
    public void assigningNumberIsValid() throws TokenizerError {
        String input = "var pawel = 5;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.validate());
    }

    @Test
    public void assigningNullIsValid() throws TokenizerError {
        String input = "var contoso = null;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.validate());
    }

    @Test
    public void assigningWithoutRightSideIsNOTValid() throws TokenizerError {
        String input = "var contoso = ";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertFalse(parser.validate());
    }

    @Test
    public void assigningWithoutLeftSideIsNOTValid() throws TokenizerError {
        String input = "= null;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertFalse(parser.validate());
    }

    @Test
    public void twoSimpleCommandsAreValid() throws TokenizerError {
        String input = "var t123 = 456; var employee = 6;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.validate());
    }

    @Test
    public void twoCommandsWithOneNotValidAreNOTValid() throws TokenizerError {
        String input = "var t123 = 456; var employee = ;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertFalse(parser.validate());
    }

    @Test
    public void simpleQueryCommandIsValid() throws TokenizerError {
        String input = "var numsPlusOne = \n" +
                "        from n in numbers \n" +
                "        select n;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.validate());
    }

    @Test
    public void complexQueryMinCommandIsValid() throws TokenizerError {
        String input = "var numsPlusOne = (from n in numbers select n).Min();";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.validate());
    }

    @Test
    public void complexQueryMinCommandIsNOTValid() throws TokenizerError {
        String input = "var numsPlusOne = (from n in numbers select n).Min;";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertFalse(parser.validate());
    }

    @Test
    public void complexQueryMaxCommandIsValid() throws TokenizerError {
        String input = "var numsPlusOne = (from n in numbers select n).Max();";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.validate());
    }

    @Test
    public void complexQueryMaxCommandIsNOTValid() throws TokenizerError {
        String input = "var numsPlusOne = (from n in numbers select n.Max();";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertFalse(parser.validate());
    }

    @Test
    public void complexQueryFirstOrDefaultCommandIsValid() throws TokenizerError {
        String input = "var numsPlusOne = (from n in numbers select n).FirstOrDefault();";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.validate());
    }

    @Test
    public void complexQueryFirstOrDefaultCommandIsNOTValid() throws TokenizerError {
        String input = "var numsPlusOne = (from n in numbers select n).FirstOrDefault);";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertFalse(parser.validate());
    }

    @Test
    public void complexQueryOrderByDescendingCommandIsValid() throws TokenizerError {
        String input = "var numsPlusOne = (from n in numbers select n).OrderByDescending();";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.validate());
    }

    @Test
    public void complexQueryOrderByDescendingCommandIsNOTValid() throws TokenizerError {
        String input = "var numsPlusOne = (from n in numbers select n)OrderByDescending();";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertFalse(parser.validate());
    }

    @Test
    public void complexCommandWithEquationsIsValid() throws TokenizerError {
        String input = "var query_orderby1 = from c in svcContext.ContactSet\n" +
                "                      where !c.CreditLimit.Equals(null)\n" +
                "                      orderby c.CreditLimit descending\n" +
                "                      select new\n" +
                "                      {\n" +
                "                       limit = c.field1,\n" +
                "                       first = c.field2,\n" +
                "                       last = c.field3\n" +
                "                      };";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertTrue(parser.validate());

    }

    @Test
    public void complexCommandWithEquationsIsNOTValid() throws TokenizerError {
        String input = "var query_orderby1 = from c in svcContext.ContactSet\n" +
                "                      where !c.CreditLimit.Equals(null)\n" +
                "                      orderby c.CreditLimit descending\n" +
                "                      select new\n" +
                "                      {\n" +
                "                       limit = \n" +
                "                       first = c.FirstName,\n" +
                "                       last = c.LastName\n" +
                "                      };";
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        assertFalse(parser.validate());

    }
}
