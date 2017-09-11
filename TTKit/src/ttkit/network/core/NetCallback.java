package ttkit.network.core;


/**
 * 网络请求层与调用者间回调接口.
 * @author xfy
 *
 */
public interface NetCallback<T> {
    /**
     * 返回成功信息.
     * @param result    返回对象，可以涵盖任何数据
     */
    void onSuccess(T Modal);
    /**
     * 返回错误信息.
     * @param result    返回连接失败信息
     */
    void onFail(String result);
}