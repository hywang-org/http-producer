package com.flash.message.tabooword.core;

/**
 * 允许通过index查询或更新的数据结构
 *
 * @author 曹子钰, 2019-04-06
 */
public interface IndexedReplaceable {

    /**
     * 获取总长度
     *
     * @return 总长度
     */
    int length();

    /**
     * 读取index位置的字符<br/>
     * 若index小于0或者超过了最大的范围, 返回null
     *
     * @param index
     *            要查询的index
     * @return 读取的字符, null代表越界
     */
    Character getAt(int index);

    /**
     * 修改index位置的字符, 返回修改的结果(true/false) <br/>
     * 若index小于0或者超过了最大的范围, 返回null
     *
     * @param index
     *            要修改的index
     * @param c
     *            要修改成的字符
     */
    boolean setAt(int index, char c);

    /**
     * 获取结果字符串
     *
     * @return 结果字符串
     */
    String getResult();

    /**
     * 替换start(包括) 到end(不包括) 范围内的所有字符, <br/>
     * 将它们替换为c
     *
     * @param start
     *            起始的位置(包括)
     * @param end
     *            终止的位置(不包括)
     * @param c
     *            替换成的字符
     */
    default void setAtRange(int start, int end, char c) {
        for (int i = start; i < end; i++) {
            setAt(i, c);
        }
    }

}
