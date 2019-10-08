package com.flash.message.tabooword.core;

import it.unimi.dsi.fastutil.chars.Char2ObjectAVLTreeMap;

import java.nio.charset.Charset;

/**
 * @author chengli
 */
public class Tree {
    private Node root;
    /**
     * 词组数量
     */
    private int numOfWords;
    /**
     * 大小写敏感
     */
    private boolean caseSensitive;
    /**
     * 编码格式
     */
    private Charset charset;

    /**
     * 私有默认构造，禁止构造方法获取对象
     */
    public Tree() {
        this(false, Charset.forName("UTF-8"));
    }

    /**
     * Constructor.
     *
     * @param caseSensitive
     *            如果区分大小写 true
     * @param charset
     *            字符集
     */
    private Tree(boolean caseSensitive, Charset charset) {
        root = new Node();
        root.setRoot(true);
        setNumberOfWords(0);
        setCaseSensitive(caseSensitive);
        setCharset(charset);
    }

    /**
     * 添加一个单词到字典树中
     *
     * @param word
     */
    public void add(String word) {
        word = preprocessWord(word);
        Char2ObjectAVLTreeMap<Node> children = root.children;

        if (!search(word, false)) {

            Node currentParent;
            currentParent = root;

            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                Node node;
                if (children.containsKey(c)) {
                    node = children.get(c);
                } else {
                    node = new Node(c);
                    node.setRoot(false);
                    node.setParent(currentParent);
                    children.put(c, node);
                }

                children = node.children;
                currentParent = node;

                if (i == word.length() - 1) {
                    node.setLeaf(true);
                    this.numOfWords++;
                }
                node.setCount(node.getCount() + 1);
            }
        }
    }

    /**
     * 清除字典树分支
     *
     * @return
     */
    public boolean clear() {
        Char2ObjectAVLTreeMap<Node> children = root.children;
        children.clear();
        return true;
    }

    /**
     * 从字典树中溢出一个单词
     *
     * @param word
     * @return
     */
    public boolean remove(String word) {

        word = preprocessWord(word);

        int previousWord = 1;

        if (!startsWith(word, false)) {
            return false;
        }

        Node currentNode = searchNode(word, false);
        Node currentParent = currentNode.getParent();

        if (currentParent.isRoot()) {
            if (currentNode.getCount() > 1) {
                currentNode.setCount(currentNode.getCount() - 1);
                if (currentNode.isLeaf()) {
                    currentNode.setLeaf(false);
                }
            } else {
                this.root.children.remove(currentNode.getC());
            }
        }

        while (!currentParent.isRoot()) {

            if (currentParent.getCount() > 1 && previousWord == 1) {
                if (currentNode.getCount() <= 1) {
                    currentParent.children.remove(currentNode.getC());
                } else {
                    currentNode.setCount(currentNode.getCount() - 1);
                    if (currentNode.isLeaf()) {
                        currentNode.setLeaf(false);
                    }
                }
                previousWord = 0;
            }
            currentParent.setCount(currentParent.getCount() - 1);
            if (currentParent.getCount() == 0) {
                currentParent.children.remove(currentNode.getC());
            }
            currentNode = currentParent;
            currentParent = currentNode.getParent();

            if (currentParent.isRoot() && currentNode.getCount() == 0) {
                root.children.remove(currentNode.getC());
            }
        }

        this.setNumberOfWords(this.getNumberOfWords() - 1);

        return true;
    }

    /**
     * 判断字典树中是否包含给定开头的单词
     *
     * @param prefix
     * @param doPreprocess
     * @return true|false
     */
    private boolean startsWith(String prefix, boolean doPreprocess) {
        if (doPreprocess) {
            prefix = preprocessWord(prefix);
        }
        return searchNode(prefix, false) != null;
    }

    /**
     * 在树中搜索一个单词
     *
     * @param word
     * @param doPreprocess
     */
    public Node searchNode(String word, boolean doPreprocess) {
        try {
            if (doPreprocess) {
                word = preprocessWord(word);
            }
            Char2ObjectAVLTreeMap<Node> children = root.children;
            Node node = null;
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                if (children.containsKey(c)) {
                    node = children.get(c);
                    children = node.children;
                } else {
                    return null;
                }
            }
            return node;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 如果单词在字典树结构中则返回true。
     *
     * @param word
     * @return true|false
     */
    public boolean search(String word) {
        return search(word, true);
    }

    /**
     * 如果单词在字典树结构中则返回true。
     *
     * @param word
     * @param doPreprocess
     * @return true|false
     */
    public boolean search(String word, boolean doPreprocess) {
        if (doPreprocess) {
            word = preprocessWord(word);
        }
        Node t = searchNode(word, false);
        return t != null && t.isLeaf();
    }

    /**
     * 如果单词区分大小写，则对单词和小写进行编码
     *
     * @param word
     * @return
     */
    private String preprocessWord(String word) {
        // Encode String
        String w = encodeWord(word);
        // Case sensitive
        return caseSensitive(w);
    }

    /**
     * Encode String 编码字符串
     *
     * @param word
     * @return word encoded
     */
    private String encodeWord(String word) {
        byte[] wordBytes = word.getBytes(this.getCharset());
        return new String(wordBytes, this.getCharset());
    }

    /**
     * 单词大小写设置
     *
     * @param word
     * @return
     */
    private String caseSensitive(String word) {
        return this.caseSensitive ? word : word.toLowerCase();
    }

    public int getNumberOfWords() {
        return numOfWords;
    }

    private void setNumberOfWords(int words) {
        this.numOfWords = words;
    }

    private void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

}
