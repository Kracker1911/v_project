package top.kracker1911.vproject.constants;

public enum ErrorCode {
    WX_APPID_SECRET_ERROR(5,"AppId、AppSecret与当前配置不符");

    private int code;
    private String message;

    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }


}
