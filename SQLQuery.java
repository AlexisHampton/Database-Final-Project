import java.text.MessageFormat;
import java.util.ArrayList;

public class SQLQuery {
    private String query ="";
    private String problemDesc ="";
    private int primaryKey;

    ArrayList<SQLParameter> params = new ArrayList<>();
    public SQLQuery(String pT){
        problemDesc = pT;

    }

    public String GetProblemDesc(){
        return problemDesc;
    }
    public String GetQuery()
    {
        return query;
    }


    public boolean HasQuery()
    {
        if(query == "") return false;
        return true;
    }

    public boolean HasParams()
    {
        return !GetSQLParameters(1).isEmpty();
    }

    public void SetQuery(String q)
    {
        query = q;
    }


    public void SetProblemText(String pT)
    {
        problemDesc = pT;
    }

    public String InsertProblemIntoDBText()
    {
        return MessageFormat.format( "insert into problem_inventory " +
                "(description,  sql_statement) " +
                "values (\"{0}\", null)", problemDesc);
    }

    public String InsertSQLIntoDBText()
    {
        return MessageFormat.format( "update problem_inventory set sql_statement = \"{0}\" where pi_index = {1}",
                query, DBTest_Demo.GetPrimaryKeyFromProblemInventory(problemDesc));
    }

    public String InsertSingleSQLDetail(int intType, String dataType, String attrDescr, int outputOrderSeq)
    {
        int index = DBTest_Demo.GetPrimaryKeyFromProblemInventory(problemDesc);
        return MessageFormat.format("insert into sql_details (ref_pi_index, interface_type," +
                " data_type, attribute_description, output_order_sequence) " +
                "values ({0}, {1}, \"{2}\", \"{3}\", {4})",
                index,intType, dataType, attrDescr, outputOrderSeq);
    }

    public void FixSQLQuery(String paramData)
    {
        query = query.replaceFirst("\\?", paramData);
    }

    public ArrayList<SQLParameter> GetSQLParameters(int interfaceType) {
        ArrayList<SQLParameter> sqlParameters = new ArrayList<>();
        for (SQLParameter param :params)
            if(param.GetInterfaceType() == interfaceType)
                sqlParameters.add(param);

        return  sqlParameters;

    }

    public ArrayList<SQLParameter> GetSQLParamInOrder()
    {
        ArrayList<SQLParameter> orderedParams = new ArrayList<>();
        ArrayList<SQLParameter> sqlOutputParams = GetSQLParameters(1);
        for(int i = 0; i < sqlOutputParams.size(); i++)
            orderedParams.add(i, sqlOutputParams.get(i));
        return orderedParams;
    }

    public String GetSQLDetailsSQL()
    {
        int index = DBTest_Demo.GetPrimaryKeyFromProblemInventory(problemDesc);
        return MessageFormat.format("select * from sql_details where ref_pi_index = {0}", index);
    }

    public void SetSQLParameter(int intType, String dataType, String attrDescr, int outputOrderSeq)
    {
        int index = DBTest_Demo.GetPrimaryKeyFromProblemInventory(problemDesc);
        params.add(new SQLParameter(index, intType, dataType, attrDescr, outputOrderSeq));
    }



/*
    create table problem_inventory (
            pi_index INT AUTO_INCREMENT,
            description VARCHAR(1024),
    sql_statement VARCHAR(4096),
    PRIMARY KEY (pi_index)
);

    create table sql_details (
            sd_index INT AUTO_INCREMENT,
            ref_pi_index INT,
            interface_type INT, //interface_type = 0 for input; 1 for output
            data_type VARCHAR(64),
    attribute_description VARCHAR(256),   //column name if it it for output; i.e., interface_type =1
    output_order_sequence INT,   //Not null only if it is for output
    PRIMARY KEY (sd_index),
    FOREIGN (ref_pi_index)
    REFERENCES problem_inventory (pi_index)
);
*/




}
