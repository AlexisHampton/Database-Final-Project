import javax.swing.*;
import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;

public class DBTest_Demo {

    public DBTest_Demo() {
    }

    public static int testconnection_mysql() {
        String connection_host = "184.152.188.144";
        Connection connect = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        int flag = 0;
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Set up the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://ip/world?" + "user=??&password=??");
            String qry1a = "SELECT CURDATE()"; // make method to select sql string
            preparedStatement = connect.prepareStatement(qry1a);
            ResultSet r1 = preparedStatement.executeQuery();
            if (r1.next()) {
                String nt = r1.getString(1);
                System.out.println(" hour(s) ahead of the system clock of mysql at " + connection_host + " is:" + nt);
            }
            r1.close();
            preparedStatement.close();
        } catch (Exception e) {
            try {
                throw e;
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    public static void main(String[] args) {

        try {
            System.out.println("\n");
            DBTest_Demo DBConnect_instance = new DBTest_Demo();
            if (DBConnect_instance.testconnection_mysql() == 0) {
                System.out.println("MYSQL Remote Connection Successful Completion");
            } else {
                System.out.println("mysql DB connection fail");
            }

        } catch (Exception e) {
            // probably error in input
            System.out.println("Hmmm... Looks like input error ....");
        }

        //start of program

            String input = JOptionPane.showInputDialog("Please specify if you are a problem specifier (1), an SQL developer (2), or an information seeker (3)");
            int personClass = Integer.parseInt(input);
            switch (personClass) {
                case 1://problem specifier
                    input = JOptionPane.showInputDialog("Please type in a problem you would like solved:");
                    String problemDesc = input;
                    if (problemDesc == null || problemDesc == "") {
                        System.out.println("Problem description cannot be empty");
                        break;
                    }
                    SQLQuery query = new SQLQuery(problemDesc);
                    SQLInsert(query.InsertProblemIntoDBText());

                    System.out.println("Your problem has been created successfully.");
                    break;
                case 2://sql dev
                    System.out.println("All of the problems without sql");
                    //show all sqls without sql statement
                    GetAllProblems(0);

                    //type sql in
                    input = JOptionPane.showInputDialog("Please type the index of the problem you wish to provide sql for");
                    int index = Integer.parseInt(input);
                    input = JOptionPane.showInputDialog("Please enter the sql:");
                    String sql = input;
                    query = GetSQLQuery(index);
                    query.SetQuery(sql);
                    SQLInsert(query.InsertSQLIntoDBText());

                    //ask if params
                    SetParams(input, query);
                    System.out.println("Your sql has been created successfully");
                    break;
                case 3://information seeker
                    //show all sqls with sql statement, each sql has an index and you pick based on index
                    GetAllProblems(1);
                    input = JOptionPane.showInputDialog("Please type the index of the sql you want to run:");
                    index = Integer.parseInt(input);
                    //run chosen sql
                    query = GetSQLQuery(index);
                    InsertSQLParameters(query);
                    if (query.HasParams()) {
                        System.out.println("Please enter the information for each parameter:");
                        //prompt user for params for each param
                        for (SQLParameter param : query.GetSQLParameters(0)) {
                            input = JOptionPane.showInputDialog(param.GetAttrDesc() + " as a " + param.GetDataType());
                            query.FixSQLQuery(input);
                        }
                    }

                    ResultSet rs = run_sql(query.GetQuery());
                    PrintData(rs, query);
                    break;
                default:
                    System.out.println("Invalid choice. Please be sure to provide a 1,2 or 3.");
                    System.exit(1);
                    break;


        }
    }

    private static void SetParams(String input, SQLQuery query) {
        input = JOptionPane.showInputDialog("How many  input params do you have?");
        int paramsAmt = Integer.parseInt(input);
        InsertInputParams(input, query, paramsAmt);

        input = JOptionPane.showInputDialog("How many output params?");
        paramsAmt = Integer.parseInt(input);
        InsertOutputParams(input, query, paramsAmt);
    }


    private static void InsertInputParams(String input, SQLQuery query, int paramsAmt) {
        for (int i = 0; i < paramsAmt; i++) {
            input = JOptionPane.showInputDialog("Please enter the data type as 'string' or 'int'");
            String dataType = input;
            input = JOptionPane.showInputDialog("Please enter the attribute description:");
            String attrDesc = input;
            SQLInsert(query.InsertSingleSQLDetail(0, dataType, attrDesc, -1));

        }
    }

    private static void InsertOutputParams(String input, SQLQuery query, int paramsAmt) {
        for (int i = 0; i < paramsAmt; i++) {
            input = JOptionPane.showInputDialog("Please enter the data type as 'string' or 'int'");
            String dataType = input;
            input = JOptionPane.showInputDialog("Please enter the attribute description:");
            String attrDesc = input;
            input = JOptionPane.showInputDialog("Please enter the output order index:");
            int oos = Integer.parseInt(input);
            SQLInsert(query.InsertSingleSQLDetail(1, dataType, attrDesc, oos));
        }
    }

    //Gets the pi_index for a given problem, if the SQL fails, it will return the min value
    public static int GetPrimaryKeyFromProblemInventory(String problemDesc) {
        try {
            String sql = MessageFormat.format("select pi_index from problem_inventory where description = \"{0}\"", problemDesc);
            ResultSet resultSet = run_sql(sql);
            while(resultSet.next())
                return resultSet.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Integer.MIN_VALUE;
    }

    public static SQLQuery GetSQLQuery(int probIndex) {
        try {
            ResultSet rs = run_sql(MessageFormat.format("select description, sql_statement from problem_inventory where pi_index = {0}", probIndex));
            while (rs.next()) {
                SQLQuery query = new SQLQuery(rs.getString(1));
                query.SetQuery(rs.getString(2));
                return query;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //gets all the problems with or without sql. 0 for no sql, 1 for sql
    public static void GetAllProblems(int sql) {
        try {
            String query = sql == 1 ? "select * from problem_inventory where sql_statement is not null" :
                    "select * from problem_inventory where sql_statement is null";
            ResultSet rs = run_sql(query);
            if(!rs.isBeforeFirst() || rs == null) {
                System.out.println("There are no available entries, please try again.");
                System.exit(0);
            }

            System.out.println("id  description \t\t\t\t\t\t\t sqlStatement");
            while (rs.next()) {
                int index = rs.getInt(1);
                String desc = rs.getString(2);
                String sqlStatement = rs.getString(3);
                System.out.println(index + " \t" + desc + "\t\t" + sqlStatement);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void SQLInsert(String query) {
        try {
            Connection connect = DriverManager
                    .getConnection("jdbc:mysql://ip/world?" + "user=??&password=??");
            Statement statement = connect.prepareStatement(query);
            System.out.println("insert query: " + query);
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static ResultSet run_sql(String qry1a) {
        try {
            Connection connect = DriverManager
                    .getConnection("jdbc:mysql://ip/world?" + "user=??&password=??");
            PreparedStatement preparedStatement = connect.prepareStatement(qry1a);
            System.out.println("query: " + qry1a);
            ResultSet r1 = preparedStatement.executeQuery();
            //preparedStatement.close();
            return r1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void PrintData(ResultSet resultSet, SQLQuery query) {
        try {
            ArrayList<SQLParameter> orderedParams = query.GetSQLParamInOrder();
            //print col names
            for (int i = 0; i < orderedParams.size(); i++)
                System.out.print(orderedParams.get(i).GetAttrDesc() + "\t\t ");
            System.out.println("");
            while (resultSet.next()) {
                //make sure param_text matches the name of output column
                String nt = "";
                for (int i = 0; i < orderedParams.size(); i++) {
                    switch (orderedParams.get(i).dataType) {
                        case "int":
                            nt += Integer.toString(resultSet.getInt(i+1));
                            break;
                        case "string":
                            nt += resultSet.getString(i+1).toString();
                            break;
                    }
                    nt += "\t\t";
                }
                System.out.println(nt);
            }
            resultSet.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    static void InsertSQLParameters(SQLQuery query)
    {
        try {
            ResultSet rs = run_sql(query.GetSQLDetailsSQL());
            while (rs.next()) {
                int intType = rs.getInt(3);
                String dataType = rs.getString(4);
                String attrDesc = rs.getString(5);
                int outputSeq = rs.getInt(6);
                query.SetSQLParameter(intType, dataType, attrDesc, outputSeq);
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }


}



