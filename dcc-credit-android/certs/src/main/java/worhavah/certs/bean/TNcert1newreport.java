package worhavah.certs.bean;

public class TNcert1newreport {


    /**
     * endorserOrder : {"taskId":"TASKYYS100000201809292043290661522284","data":null,"status":"STARTED"}
     * tongniuData : {"subCode":"137","src":"tongdun","code":"0200","msg":"任务已成功提交","nextStage":null,"authCode":null}
     */

    private EndorserOrderBean endorserOrder;
    private String tongniuData;

    public EndorserOrderBean getEndorserOrder() {
        return endorserOrder;
    }

    public void setEndorserOrder(EndorserOrderBean endorserOrder) {
        this.endorserOrder = endorserOrder;
    }

    public String getTongniuData() {
        return tongniuData;
    }

    public void setTongniuData(String tongniuData) {
        this.tongniuData = tongniuData;
    }

    public static class EndorserOrderBean {
        /**
         * taskId : TASKYYS100000201809292043290661522284
         * data : null
         * status : STARTED
         */

        private String taskId;
        private Object data;
        private String status;

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }


}
