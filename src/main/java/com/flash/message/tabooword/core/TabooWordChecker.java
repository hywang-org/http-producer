package com.flash.message.tabooword.core;


import java.util.List;

import org.springframework.util.CollectionUtils;

import com.flash.message.config.MsgDao;
import com.flash.message.entity.tabooword.TabooWord;

/**
 * 作者： chengli
 * 日期： 2019/9/28 16:57
 */
public class TabooWordChecker {

    private volatile static Tree tree = null;

    public static boolean containTabooWord(String content) {
        return isSensitive(tree, content);
    }

    public static boolean isSensitive(Tree tree, String content) {
        IndexedReplaceable replaceable = StringReplaceable.fromString(content);
        // 当前指针index
        int curIndex = 0;
        // 外层循环, 对curIndex进行遍历
        while (curIndex < replaceable.length()) {
            // 初始的查找字符子串
            StringBuilder secondStr = new StringBuilder();
            for (int pIndex = curIndex; pIndex < replaceable.length(); ++pIndex) {
                secondStr.append(replaceable.getAt(pIndex));
                Node t = tree.searchNode(secondStr.toString(), true);
                if (t == null) {
                    // 若找不到t, 则证明curIndex处不是敏感词, 在下一个位置进行查找
                    ++curIndex;
                    break;
                } else if (t.isLeaf()) {
                    return true;
                } else if (pIndex + 1 >= replaceable.length()) {
                    // 若t不是叶子节点, 且pIndex到了最后了, 则跳出循环
                    ++curIndex;
                    break;
                }
            }
        }
        return false;
    }

    public static void init(MsgDao smsDao) {
        List<TabooWord> tabooWords = smsDao.find("from TabooWord");
        tree = new Tree();
        if (!CollectionUtils.isEmpty(tabooWords)) {
            for (TabooWord tabooWord : tabooWords) {
                tree.add(tabooWord.getTabooWord());
            }
        }
    }
}
