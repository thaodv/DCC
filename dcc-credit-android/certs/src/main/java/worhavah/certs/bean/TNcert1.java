package worhavah.certs.bean;

import java.util.List;

public class TNcert1 {

        /**
         * data : {"fields":[{"name":"sms_code","lable":null,"type":"text"}],"task_id":"TASKYYS100000201809191849400661523335","next_stage":"LOGIN","auth_code":null}
         * status : {"subCode":"105","src":"tongdun","code":"0200","msg":"请输入手机验证码"}
         */

        private DataBean data;
        private StatusBean status;

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public StatusBean getStatus() {
            return status;
        }

        public void setStatus(StatusBean status) {
            this.status = status;
        }

        public static class DataBean {
            /**
             * fields : [{"name":"sms_code","lable":null,"type":"text"}]
             * task_id : TASKYYS100000201809191849400661523335
             * next_stage : LOGIN
             * auth_code : null
             */

            private String task_id;
            private String next_stage;
            private Object auth_code;
            private List<FieldsBean> fields;

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

            public Object getAuth_code() {
                return auth_code;
            }

            public void setAuth_code(Object auth_code) {
                this.auth_code = auth_code;
            }

            public List<FieldsBean> getFields() {
                return fields;
            }

            public void setFields(List<FieldsBean> fields) {
                this.fields = fields;
            }

            public static class FieldsBean {
                /**
                 * name : sms_code
                 * lable : null
                 * type : text
                 */

                private String name;
                private Object lable;
                private String type;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public Object getLable() {
                    return lable;
                }

                public void setLable(Object lable) {
                    this.lable = lable;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }
            }
        }

        public static class StatusBean {
            /**
             * subCode : 105
             * src : tongdun
             * code : 0200
             * msg : 请输入手机验证码
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
