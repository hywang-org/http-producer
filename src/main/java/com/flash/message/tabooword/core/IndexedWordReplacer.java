package com.flash.message.tabooword.core;

/**
 * 对IndexedReplaceable接口执行敏感词替换
 *
 * @author 曹子钰, 2019-04-06
 */
public class IndexedWordReplacer {

    /**
     * 根据SensitiveWordTree对一个IndexedReplaceable对象执行敏感词替换操作, <br/>
     * 用replaceTemplate进行替换
     *
     * @param replaceable
     *            要替换的IndexedReplaceable
     * @param replaceTemplate
     *            替换后的字符
     */
    public static void replace(Tree tree, IndexedReplaceable replaceable, char replaceTemplate) {
        // 当前指针index
        int curIndex = 0;
        // 外层循环, 对curIndex进行遍历
        while (curIndex < replaceable.length()) {
            // 初始的查找字符子串
            StringBuilder secondStr = new StringBuilder();
            for (int pIndex = curIndex; pIndex < replaceable.length(); ++pIndex) {
                secondStr.append(replaceable.getAt(pIndex));
                Node t = tree.searchNode(secondStr.toString(), true);
                // 若找不到t, 则证明curIndex处不是敏感词, 在下一个位置进行查找
                if (t == null) {
                    ++curIndex;
                    break;
                }
                // 若找到的t是叶子节点, 证明curIndex到pIndex之间为敏感词, 执行替换
                else if (t.isLeaf()) {
                    replaceable.setAtRange(curIndex, pIndex + 1, replaceTemplate);
                    curIndex = pIndex + 1;
                    break;
                }
                // 若t不是叶子节点, 且pIndex到了最后了, 则跳出循环
                else if (pIndex + 1 >= replaceable.length()) {
                    ++curIndex;
                    break;
                }
            }
        }
    }

}
