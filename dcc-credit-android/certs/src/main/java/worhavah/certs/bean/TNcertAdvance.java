package worhavah.certs.bean;

public class TNcertAdvance {


    /**
     * data : null
     * status : {"subCode":"2007","src":"123","code":"0200","msg":"任务完成"}
     */

    private Object data;
    private StatusBean status;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public static class StatusBean {
        /**
         * subCode : 2007
         * src : 123
         * code : 0200
         * msg : 任务完成
         */

        private String subCode;
        private String src;
        private String code;
        private String msg;

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
    }
}
