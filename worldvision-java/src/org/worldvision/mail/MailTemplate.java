package org.worldvision.mail;

import org.worldvision.queue.EmailWorker;

public class MailTemplate {
	public static final String CLAIM_LETTER_TOPIC = "NEXT-志工-信件領取通知";
	public static final String CLAIM_LETTER_CONTENT = 
		"＜此信件為系統發送，請勿直接回覆＞"+
		"\n"+
		"\n"+
		"\n親愛的志工朋友："+
		"\n"+
		"\n收信平安！"+
		"\n"+
		"\n感謝您今日在NEX-T志工翻譯網頁領取了 @letter ！"+
		"\n請您協助於應返日期內將今日領取的信件譯畢，並將譯文上傳。"+
		"\n檔案上傳完成後，您將會收到本系統寄發的上傳成功通知信。"+
		"\n"+
		"\n為了使您翻譯的過程更加順利，我們也在NEX-T志工翻譯網頁提供了各國信件翻譯範本/翻譯注意事項/特殊名詞解釋（http://next.worldvision.org.tw），供您參閱使用。"+
		"\n"+
		"\n感謝您熱心的服務及協助！"+
		"\n"+
		"\n"+
		"\n台灣世界展望會"+
		"\n兒童業務部 敬上"+
		"\n"+
		"\n"+
		"\n"+
		"\n"+
		"\n﹡若有任何問題，歡迎來信至下列連絡信箱：srdvs@worldvision.org.tw";

	public static final String VOLUNETTER_COMPLETE_LETTER_TOPIC = "NEXT-志工-譯返上傳成功通知信";
	public static final String VOLUNETTER_COMPLETE_LETTER_CONTENT = 
		"＜此信件為系統發送，請勿直接回覆＞"+
		"\n"+
		"\n"+
		"\n親愛的志工朋友："+
		"\n"+
		"\n收信平安！"+
		"\n"+
		"\nNEX-T志工翻譯網頁 在此通知，您這次服務的信件 @letter 已成功上傳，謝謝您的熱心幫忙！"+
		"\n"+
		"\n祝您有個愉快的一天，也歡迎您再次登入Nex-t志工翻譯網頁領取信件！（http://next.worldvision.org.tw)"+
		"\n"+
		"\n"+
		"\n台灣世界展望會"+
		"\n兒童業務部 敬上"+
		"\n"+
		"\n"+
		"\n﹡若有任何問題，歡迎來信至下列連絡信箱：srdvs@worldvision.org.tw"+
		"\n"+
		"\n";
	
	public static final String EMPLOYEE_COMPLETE_LETTER_TOPIC ="NEXT-員工-譯返上傳成功通知信";
	public static final String EMPLOYEE_RE_UPLOAD_LETTER_TOPIC ="NEXT-員工-譯返[重新]上傳成功通知信";
	public static final String EMPLOYEE_COMPLETE_LETTER_CONTENT =
		"親愛的同工："+
		"\n"+
		"\n翻譯志工已於今日將譯畢信件 @letter 上傳囉！"+
		"\n可以點選下列網址下載:"+
		"\n @download_url"+ 
		"\n或登入http://next.worldvision.org.tw查看，謝謝！"+
		"\n";
	
	public static final String NEW_LETTER_REMINDER_TOPIC="NEXT-志工-每週信件通知信";
	public static final String NEW_LETTER_REMINDER_CONTENT="親愛的志工朋友：" +
	"＜此信件為系統發送，請勿直接回覆＞"+
	"\n"+
	"\n"+
	"\n親愛的志工朋友："+
	"\n"+
	"\n"+
	"\n您好！NEX-T志工翻譯網頁 在此通知您，網頁上目前仍有兒童信件(中翻英@available_chi_letters /英翻中@available_eng_letters)等待翻譯，若您能協助服務，請點擊以下連結來瀏覽及下載待譯檔案。"+
	"\n（將會填入http://next.worldvision.org.tw之網頁連結）"+
	"\n"+
	"\n謝謝您的熱心幫忙，祝福您有個愉快的一天！"+
	"\n"+
	"\n"+
	"\n台灣世界展望會"+
	"\n兒童業務部  敬上"+
	"\n"+
	"\n"+
	"\n﹡若有任何問題，歡迎來信至下列連絡信箱：srdvs@worldvision.org.tw";
	
	public static final String DUE_REMINDER_TOPIC="NEXT-志工-逾期提醒信";
	public static final String DUE_REMINDER_CONTENT=
		"＜此信件為系統發送，請勿直接回覆＞"+
		"\n"+
		"\n"+
		"\n親愛的志工朋友："+
		"\n"+
		"\n收信平安！"+
		"\n"+
		"\n在此提醒您，您在 NEX-T志工翻譯網頁 所領取的@letter，本系統尚未收到您完成的翻譯檔案！麻煩您收到此通知後，於兩天內將譯畢信件上傳。"+
		"\n"+
		"\n若您遇到臨時狀況，無法如期譯畢此批信件；也請您於兩天內登入 NEX-T志工翻譯網頁 （http://next.worldvision.org.tw） 進行退件，並輸入退件的原因。"+
		"\n"+
		"\n為確保信件能盡速寄給資助人/受助童，若被領取的英翻中信件在逾期三日後(中翻英信件逾期兩日後) 仍未上傳，該筆信件將會回復至未領取的狀態，並重新開放給所有志工領取。屆時，系統也會登記該筆逾期的紀錄。"+
		"\n"+
		"\n為確保信件能盡速寄給資助人/受助童，麻煩您盡速將譯畢信件上傳或進行退件囉！謝謝您的支持與配合！"+
		"\n"+
		"\n"+
		"\n台灣世界展望會"+
		"\n兒童業務部 敬上"+
		"\n"+
		"\n"+
		"\n﹡若有任何問題，歡迎來信至下列連絡信箱：srdvs@worldvision.org.tw";
	public static final String EMP_EMERGENT_NOTICE_OLD_TOPIC="NEXT-員工-信件緊急通知-無人領取";
	public static final String EMP_EMERGENT_NOTICE_OLD_CONTENT=
		"＜此信件為系統發送，請勿直接回覆＞"+
		"\n"+
		"\n"+
		"\n親愛的同工："+
		"\n"+
		"\n"+
		"\n在此通知您所上傳的信件 @letter 因為上傳後無人領取，已變成緊急狀態囉" +
		"\n請您登入http://next.worldvision.org.tw以安排信件的後續處理" +
		"\n"+
		"\n";
	public static final String EMP_EMERGENT_NOTICE_RETURNED_TOPIC="NEXT-員工-信件緊急通知-退件";
	public static final String EMP_EMERGENT_NOTICE_RETURNED_CONTENT=
		"＜此信件為系統發送，請勿直接回覆＞"+
		"\n"+
		"\n"+
		"\n親愛的同工："+
		"\n"+
		"\n"+
		"\n在此通知您所上傳的信件 @letter 因為志工退件而變成緊急狀態囉" +
		"\n志工姓名: @volunteer_name" +
		"\n志工E-mail帳號: @volunteer_email" +
		"\n退件原因: " +
		"\n請您登入http://next.worldvision.org.tw以安排信件的後續處理" +
		"\n"+
		"\n";
	public static final String EMP_EMERGENT_NOTICE_DUED_TOPIC="NEXT-員工-信件緊急通知-志工逾期";
	public static final String EMP_EMERGENT_NOTICE_DUED_CONTENT=
		"＜此信件為系統發送，請勿直接回覆＞"+
		"\n"+
		"\n"+
		"\n親愛的同工："+
		"\n"+
		"\n"+
		"\n在此通知您所上傳的信件 @letter 因為逾期未譯返，而變成緊急狀態囉" +
		"\n志工姓名: @volunteer_name" +
		"\n志工E-mail帳號: @volunteer_email" +
		"\n請您登入http://next.worldvision.org.tw以安排信件的後續處理" +
		"\n"+
		"\n";
	
	public static final String EMP_CLAIM_LETTER_NOTICE_TOPIC="NEXT-員工-領取信件通知信";
	public static final String EMP_CLAIM_LETTER_NOTICE_CONTENT=
		"＜此信件為系統發送，請勿直接回覆＞"+
		"\n"+
		"\n"+
		"\n親愛的同工："+
		"\n"+
		"\n"+
		"\n在此通知您所上傳的信件 @letter 已於今日被領取！"+
		"\n歡迎您登入<http://next.worldvision.org.tw查看，謝謝！";

}
	
