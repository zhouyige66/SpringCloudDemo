package cn.roy.springcloud.api.entity;

/**
 * @Description: 返回数据
 * @Author: Roy Z
 * @Date: 2019-09-06 19:15
 * @Version: v1.0
 */
public class ResultData {
    public static final int CODE_SERVER_ERROR = 400;
    public static final int CODE_PARAMETER_ERROR = 401;


    private int code;
    private String msg;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static ResultData success(Object object) {
        ResultData resultData = new ResultData();
        resultData.setCode(200);
        resultData.setMsg("成功");
        resultData.setData(object);

        return resultData;
    }

    public static ResultData fail(int code, String msg) {
        ResultData resultData = new ResultData();
        resultData.setCode(code);
        resultData.setMsg(msg);
        resultData.setData(null);

        return resultData;
    }

    public static ResultData serverError(String msg) {
        ResultData resultData = new ResultData();
        resultData.setCode(CODE_SERVER_ERROR);
        resultData.setMsg(msg);
        resultData.setData(null);

        return resultData;
    }

}
