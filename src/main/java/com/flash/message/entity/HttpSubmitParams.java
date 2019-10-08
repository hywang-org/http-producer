package com.flash.message.entity;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 作者 :hywang
 *
 * @version 创建时间：2019年8月31日 下午3:43:16
 *
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class HttpSubmitParams {

    @NotBlank(message = "账户为空")
    @Length(min = 6, max = 6, message = "账户长度为6位字符")
    private String account;

    @NotBlank(message = "密码为空")
    @Length(min = 1, max = 50, message = "密码最大长度为50位字符")
    private String pswd;

    @NotBlank(message = "收信人为空")
    private String mobile;

    @NotBlank(message = "短信内容为空")
    private String msg;

    @NotBlank(message = "短信状态反馈为空")
    private String needstatus;

    private String extno;
}
