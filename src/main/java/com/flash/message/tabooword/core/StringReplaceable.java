package com.flash.message.tabooword.core;

/**
 * 普通字符串的禁忌词检测
 *
 * @author chengli
 */
public class StringReplaceable implements IndexedReplaceable {

    private final char[] content;
    private final int length;

    /**
     * 根据一个字符串生成一个StringReplaceable
     *
     * @param string 字符串
     * @return 生成的StringReplaceable结果
     */
    static StringReplaceable fromString(String string) {
        return new StringReplaceable(string);
    }

    private StringReplaceable(String string) {
        this.content = string.toCharArray();
        this.length = content.length;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public Character getAt(int index) {
        if (index < 0 || index >= length()) {
            return null;
        }
        return content[index];
    }

    @Override
    public boolean setAt(int index, char c) {
        if (index < 0 || index >= length()) {
            return false;
        }
        content[index] = c;
        return true;
    }

    @Override
    public String getResult() {
        return new String(content);
    }

}
