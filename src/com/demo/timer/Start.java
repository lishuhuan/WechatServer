package com.demo.timer;

public class Start{
	
	public void execute() {
		System.out.println("start timer");
		NotifyAndAutoDo notifyAndAutoDo=new NotifyAndAutoDo();
		new Thread(notifyAndAutoDo).start();
	}
	

	
	
}
