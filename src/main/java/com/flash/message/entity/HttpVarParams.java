package com.flash.message.entity;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 作者 hywang 619201932@qq.com
 *
 * @version 创建时间：2019年9月4日 下午2:50:27
 *
 */
@Setter
@Getter
@ToString
public class HttpVarParams {

    @NotBlank(message = "账户为空")
    @Length(min = 6, max = 6, message = "账户长度为6位字符")
    private String account;

    @NotBlank(message = "密码为空")
    @Length(min = 1, max = 50, message = "密码最大长度为50位字符")
    private String pswd;

    @NotBlank(message = "短信内容为空")
    private String msg;

    @NotBlank(message = "短信状态反馈为空")
    private String needstatus;

    @NotBlank(message = "模板id为空")
    private String productId;

    private String extno;
}