public class Validator {

    public static void main(String[] args) {
        String input = "var query_orderby1 = from c in svcContext.ContactSet\n" +
                "                      where !c.CreditLimit.Equals(null)\n" +
                "                      orderby c.CreditLimit descending\n" +
                "                      select new\n" +
                "                      {\n" +
                "                       limit = c.CreditLimit,\n" +
                "                       first = c.FirstName,\n" +
                "                       last = c.LastName\n" +
                "                      }; ";

        System.out.println(input);
    }
}
