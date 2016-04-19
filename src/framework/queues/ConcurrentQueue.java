package framework.queues;

import java.nio.FloatBuffer;

import world.Chunk;

public class ConcurrentQueue<T extends ConcurrentNode>{
	int size;
	public ConcurrentQueue(){
		size = 0;
	}
	T last;
	T first;
	
	public synchronized void push(T node){
		size++;
		node.next = null;
		node.prev = null;
		if(last != null){
			node.prev = last;
			last.next = node;
		}
		last = node;
		if(first == null)
			first = node;
	}
	public synchronized T pop(){
		if(size > 0)
			size--;
		if(last == first)
			last = null;
		T ret = first;
		if(first != null){
			first = ((T)first.next);
			if(first != null)
				first.prev = null;
			if(ret != null){
				ret.next = null;
				ret.prev = null;
			}
		}
		return ret;
	}
	public T peek(){
		return first;
	}
	public int size(){
		return size;
	}
}