package com.model;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 스레드간 전달되는 메시지를 정의한다.
 *
 */
public enum MessageEnum
{
	MESSAGE_AUDIO,			// MP3
	MESSAGE_WAVE,			// 음성
	MESSAGE_WAKEUP,			// HotWord 감지
	MESSAGE_PROGRESS,		// 작업중
	MESSAGE_COMPLETED,		// 작업완료
	MESSAGE_PLAY_MUSIC,		// 음악재생
	MESSAGE_STOP_MUSIC,		// 음악재생 중지
	;
	
    public static MessageEnum getMessageEnum(int i) {
        return MessageEnum.values()[i];
    }
}
