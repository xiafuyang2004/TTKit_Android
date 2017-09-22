package ttkit.network.core;

import java.util.HashMap;

/*
 * 上传或下载线程池管理器
 */
public class ThreadTaskMannager {
	private HashMap<String, Thread> tasks = new HashMap<String, Thread>();
	
	private Thread getTask(String uri) {
		Thread task = tasks.get(uri);
        return task;
    }

    public void removeTask(String uri) {
    	Thread task = tasks.get(uri);
        if (null!=task && task.isAlive()) {
            task.interrupt();
        }
        tasks.remove(uri);
    }
    public void addTask(String uri,Thread task){
    	if (!isTaskAlive(uri)){
    		tasks.put(uri,task);
    	}
    }

    public boolean isTaskAlive(String uri){
        Thread task = getTask(uri);
        if(task==null){
            return false;
        }
        return task.isAlive();
    }

}
