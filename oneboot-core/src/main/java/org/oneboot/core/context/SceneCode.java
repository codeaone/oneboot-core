package org.oneboot.core.context;

public interface SceneCode {

    /**
     * 获取场景码
     * 
     * @return
     */
    public String getCode();

    /**
     * 所属业务事件码
     * 
     * @return
     */
    public String getBizCode();

    /**
     * tag 起个简短的英文名字
     * 
     * @return
     */
    public String getTag();

    /**
     * message
     * 
     * @return
     */
    public String getMessage();

}
