public class SQLParameter {
    int refIndex;
    int interfaceType;
    String dataType;
    String attrDesc;
    int outputOrderSequence;

    public SQLParameter(int index, int intType, String dType, String attr, int oos)
    {
        refIndex = index;
        interfaceType = intType;
        dataType=dType;
        attrDesc = attr;
        outputOrderSequence = oos;
    }

    public int GetRefIndex() {
        return refIndex;
    }

    public int GetInterfaceType() {
        return interfaceType;
    }

    public String GetDataType() {
        return dataType;
    }

    public String GetAttrDesc() {
        return attrDesc;
    }

    public int GetOutputOrderSequence() {
        return outputOrderSequence;
    }
}

    /*
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


