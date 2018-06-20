package com.io;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 동작 상태를 나타내는 인터페이스.
 * DOT MATRIX 또는 APA102 LED를 이용하여 동작 상태를 표시한다.
 *
 */
public interface IOperation
{
	public void startup();
	
	public void wakeup();
	
	public void progress();
	
	public void completed();
	
	public void extraOpertion(Object data);
}
