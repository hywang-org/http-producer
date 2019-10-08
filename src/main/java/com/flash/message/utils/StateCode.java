package com.flash.message.utils;

/**
 * @author 作者 :hywang
 *
 * @version 创建时间：2019年9月7日 下午5:19:52
 *
 * @version 1.0
 */
public enum StateCode {
    FEE_NOT_ENOUGN("300001", "FEE_NOT_ENOUGN"),
	ILLEGAL_CONTENT("300002", "ILLEGAL_CONTENT"),
	PWD_ERROR("300004", "FEE_NOT_ENOUGN");

    private String code;
    private String name;

    private StateCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
