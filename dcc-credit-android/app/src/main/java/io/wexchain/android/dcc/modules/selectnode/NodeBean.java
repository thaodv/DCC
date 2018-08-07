package io.wexchain.android.dcc.modules.selectnode;

/**
 * @author Created by Wangpeng on 2018/8/6 18:05.
 * usage:
 */
public class NodeBean {
    
    public int id;
    
    public String url;
    
    public String name;
    
    public NodeBean(int id, String url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }
    
}
