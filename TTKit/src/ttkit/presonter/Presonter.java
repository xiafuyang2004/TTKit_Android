package ttkit.presonter;

import java.util.ArrayList;

/**   	 
 * 架构原则:
 * 		1.单一原则: 	一个函数/类应保证职责单一化
 * 		2.事件驱动UI: 业务层与界面层的通过事件进行交互（都依赖抽象层可以实现解耦，业务层复用性更高，并相对独立，便于单元测试）
 * 		3.高复用性:   支持观察者模式，使独立的业务逻辑能实现高复用性(eg: presonter)
 * 架构模式:
 * 		MVP
 * 备注: 
 * 		抽离业务逻辑代码便于复用，或抽离复杂代码进行降偶，该类为非线程安全
 * 
 * @author xfy
 * @date 2015-06-26
 */
public abstract class Presonter {

	/**
	 * 控制层监听基类，由子类实现具体的监听(需要监听控制层事件时，需要继承并实现该接口,通过事件驱动UI变化)
	 * @param listener
	 */
	public interface IBaseListener {

	}

	protected final ArrayList<IBaseListener> mListeners = new ArrayList<IBaseListener>();

	/**
	 * Presonter中有可能会有多个listener，放入队列中
	 * 
	 * @param listener
	 */
	public final void addListener(IBaseListener listener) {
		synchronized (mListeners) {
			if (!mListeners.contains(listener)) {
				mListeners.add(listener);
			}
		}
	}

	public final void removeListener(IBaseListener listener) {
		synchronized (mListeners) {
			mListeners.remove(listener);
		}
	}
	

	/**
	 * 循环所有的listener()
	 * 
	 * 这里只是一个示例，具体在子类中去完善需要的监听通知方式，在需要驱动UI时再批量通知
	 */
	protected final void notifyListenners(Object obj) {
		//循环通知所有的监听对象，观察者模式应用
        /*for (IBaseListener listener : mListeners) {
        	listener.onSuccess(obj);
        }*/
	}
}
