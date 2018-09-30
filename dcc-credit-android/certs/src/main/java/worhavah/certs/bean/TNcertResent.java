package worhavah.certs.bean;

public class TNcertResent {


    /**
     * fields : null
     * task_id : 1234567890
     * next_stage : LOGIN
     * auth_code : ///Z
     */

    private Object fields;
    private String task_id;
    private String next_stage;
    private String auth_code;

    public Object getFields() {
        return fields;
    }

    public void setFields(Object fields) {
        this.fields = fields;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getNext_stage() {
        return next_stage;
    }

    public void setNext_stage(String next_stage) {
        this.next_stage = next_stage;
    }

    public String getAuth_code() {
        return auth_code;
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }
}
