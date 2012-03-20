package org.worldvision.mail;

import org.worldvision.queue.EmailWorker;

public class MailTemplate {
	public static final String CLAIM_LETTER_TOPIC = "志工系統--信件領取通知";
	public static final String CLAIM_LETTER_CONTENT = 
			"＜此信件為系統發送，請勿直接回覆＞"
			+"\n\n\n親愛的志工朋友："
			+ "\n\n收信平安！"
			+ "\n感謝您今日在NEX-T.志工翻譯網頁領取了待譯信件！"
			+ "\n@letter"
			+ "\n請您協助於七天內將今日領取的信件譯畢，並將譯文上傳。"
			+ "\n檔案上傳完成後，您將會收到本系統寄發的上傳成功通知信。"
			+ "\n\n為了使您翻譯的過程更加順利，我們也在NEX-T.志工翻譯網頁提供了各國信件翻譯範本/翻譯注意事項/各國特殊名詞解釋（網頁連結），供您參閱使用。"
			+ "\n\n感謝您熱心的服務及協助！" + "\n\n\n台灣世界展望會" + "\n兒童業務部 敬上"
			+ "\n\n\n﹡若有任何問題，歡迎來信至下列連絡信箱：srdvs@worldvision.org.tw";

	public static final String VOLUNETTER_COMPLETE_LETTER_TOPIC = "志工系統-譯返上傳成功通知信-志工";
	public static final String VOLUNETTER_COMPLETE_LETTER_CONTENT = 
			"＜此信件為系統發送，請勿直接回覆＞"
			+"\n\n\n親愛的志工朋友："
			+ "\n\n收信平安！"
			+ "\n\nNEX-T.志工翻譯網頁 在此通知，您這次服務的信件已成功上傳，謝謝您的熱心幫忙！祝您有個愉快的一天，也歡迎您再次登入網頁(將填入LOG-IN網址)領取待譯信件！"
			+ "\n@letter"
			+ "\n\n\n台灣世界展望會" 
			+ "\n兒童業務部 敬上"
			+ "\n\n\n﹡若有任何問題，歡迎來信至下列連絡信箱：srdvs@worldvision.org.tw";
	
	public static final String EMPLOYEE_COMPLETE_LETTER_TOPIC ="志工系統-譯返上傳成功通知信-員工";
	public static final String EMPLOYEE_COMPLETE_LETTER_CONTENT ="親愛的同工：" +
			"\n\n\n翻譯志工已於今日將譯畢的信件上傳囉！"
			+"\n@letter"
			+"\n歡迎您登入 NEX-T.志工翻譯網頁 查看，謝謝！";
	
	public static final String NEW_LETTER_REMINDER_TOPIC="志工系統-每日新信件通知信";
	public static final String NEW_LETTER_REMINDER_CONTENT="親愛的志工朋友：" +
			"\n\n\n\n您好！NEX-T.志工翻譯網頁 在此通知您，網頁上目前仍有" + EmailWorker.UN_CLAIMED_LETTER_COUNT + "兒童信件等待翻譯，若您能協助服務，請點擊以下連結來瀏覽及下載待譯檔案。" +
			"\n（將會填入LOG-IN PAGE之網頁連結）" +
			"\n\n\n\n台灣世界展望會" +
			"\n兒童業務部  敬上";
	
	public static final String DUE_REMINDER_TOPIC="志工系統--逾期提醒信";
	public static final String DUE_REMINDER_CONTENT=
			"＜此信件為系統發送，請勿直接回覆＞" + 
			"\n\n\n親愛的志工朋友：" +
			"\n\n\n收信平安！" +
			"\n\n\n在此提醒您，您在七天前於 NEX-T.志工翻譯網頁 所領取的待譯信件，本系統尚未收到您完成的中譯檔案！麻煩您收到此通知後，於兩天內將譯畢信件上傳。" +
			"\n@letter"+
			"\n\n\n若您遇到臨時狀況，無法如期譯畢此批信件；也請您於兩天內登入 NEX-T.志工翻譯網頁 進行退件，並輸入退件的原因。" +
			"\n\n\n再補充說明一點，為確保信件能盡速寄給資助人/受助童，若被領取的信件在逾期三日後仍未譯畢上傳，將會回復至未領取的狀態，並重新開放給所有志工領取。屆時，系統也會登記該筆逾期的紀錄。" +
			"\n\n\n為了保障您日後有優先的服務權益，麻煩您於三天內將譯畢信件上傳或進行信件退件囉！謝謝您的支持與配合！" +
			"\n\n\n\n台灣世界展望會" +
			"\n兒童業務部 敬上"+
			"\n\n\n﹡若有任何問題，歡迎來信至下列連絡信箱：srdvs@worldvision.org.tw";
	public static final String EMP_EMERGENT_NOTICE_TOPIC="志工系統--緊急信件通知信";
	public static final String EMP_EMERGENT_NOTICE_CONTENT=
		"＜此信件為系統發送，請勿直接回覆＞" +
		"\n\n\n親愛的同工:" +
		"\n\n\n在此通知您所上傳的信件 <信件檔案名> 已變成緊急狀態囉，請登入<NEX-T網頁連結>以安排信件的後續處理";

}
	
