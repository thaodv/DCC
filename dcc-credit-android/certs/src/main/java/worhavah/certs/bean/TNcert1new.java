package worhavah.certs.bean;

public class TNcert1new {


    /**
     * endorserOrder : {"taskId":"TASKYYS100000201809292043290661522284","data":null,"status":"STARTED"}
     * tongniuData : {"subCode":"137","src":"tongdun","code":"0200","msg":"任务已成功提交","nextStage":null,"authCode":null}
     */

    private EndorserOrderBean endorserOrder;
    private TongniuDataBean tongniuData;

    public EndorserOrderBean getEndorserOrder() {
        return endorserOrder;
    }

    public void setEndorserOrder(EndorserOrderBean endorserOrder) {
        this.endorserOrder = endorserOrder;
    }

    public TongniuDataBean getTongniuData() {
        return tongniuData;
    }

    public void setTongniuData(TongniuDataBean tongniuData) {
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

    public static class TongniuDataBean {
        /**
         * subCode : 137
         * src : tongdun
         * code : 0200
         * msg : 任务已成功提交
         * nextStage : null
         * authCode : null
         */

        private String subCode;
        private String src;
        private String code;
        private String msg;
        private String nextStage;
        private String authCode;

        public String getSubCode() {
            return subCode;
        }

        public void setSubCode(String subCode) {
            this.subCode = subCode;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getNextStage() {
            return nextStage;
        }

        public void setNextStage(String nextStage) {
            this.nextStage = nextStage;
        }

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }
    }
}
