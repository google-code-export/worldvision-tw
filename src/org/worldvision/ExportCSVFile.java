package org.worldvision;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.worldvision.model.LetterModel;
import org.worldvision.model.Letters;
import org.worldvision.model.VoulenteerLogModel;
import org.worldvision.model.VoulenteerLogs;

public class ExportCSVFile extends HttpServlet {
	private static final Logger log = Logger.getLogger(ExportCSVFile.class
			.getName());
	private final VoulenteerLogModel logModel = new VoulenteerLogModel();
	private final LetterModel letterModel = new LetterModel();
	private final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Date[] dates = getDates(req);
		Date start_date = dates[0];
		Date end_date = dates[1];
		String type_str = req.getParameter("type");
		StringBuffer content = null;
		if (type_str != null) {
			int type = Integer.parseInt(type_str);
			switch (type) {
				case 1:
					content = generateLogs(start_date, end_date);
					break;
				case 2:
					content = generateDueReport(start_date, end_date);
					break;
				case 3:
					content = generateVouReport(start_date, end_date);
					break;
			}
			exportCSV(req, res, "worldvision.csv", content);
		}
	}
	
	/**
	 * 
	 * 
	 * if (letter.return_date >= s_date && letter.return_date <= e_date)
        @letters.push(letter)
      end
	 * 
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	private StringBuffer generateVouReport(Date begin, Date end) {
		List letters = letterModel.findReturnedLetters(begin, end);
		int size = letters.size();
		System.out.println("found " + size + " letters");
		StringBuffer content = new StringBuffer(
				"志工姓名, 志工編號, 譯返上傳日期, 封數\n");
		for (int i = 0; i < size; i++) {
			Letters letter = (Letters) letters.get(i);
			content.append(letter.getVoulenteer_name() + ","
					+ letter.getVoulenteer_id() + ","
					+ (letter.getReturn_date() == null ? "" : df.format(letter.getReturn_date())) + ","
					+ letter.getNumber_of_letters() + "\n");
		}
		return content;
	}

	private StringBuffer generateDueReport(Date start_date, Date end_date) {
		List letters = letterModel.findDueLetters(0, true);
		int size = letters.size();
		System.out.println("found " + size + " letters");
		StringBuffer content = new StringBuffer(
				"志工姓名, 志工編號, 下載檔案日期\n");
		for (int i = 0; i < size; i++) {
			Letters letter = (Letters) letters.get(i);
			content.append(letter.getVoulenteer_name() + ","
					+ letter.getVoulenteer_id() + ","
					+ (letter.getClaim_date() == null ? "" : df.format(letter.getClaim_date())) 
					+ "\n");
		}
		return content;
	}

	private void exportCSV(HttpServletRequest req, HttpServletResponse res,
			String filename, StringBuffer content) throws IOException {
		int length = 0;
		ServletOutputStream op = res.getOutputStream();
		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(filename);
		res.setContentType((mimetype != null) ? mimetype
				: "application/octet-stream");
		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ filename + "\"");
		ByteArrayInputStream in = new ByteArrayInputStream(content.toString()
				.getBytes("big5"));
		byte[] bbuf = new byte[content.toString().getBytes().length];

		while ((in != null) && ((length = in.read(bbuf)) != -1)) {
			op.write(bbuf, 0, length);
		}

		in.close();
		op.flush();
		op.close();
	}

	private Date[] getDates(HttpServletRequest req) {
		Date[] dates = new Date[2];
		String s_date = req.getParameter("start_date");
		String e_date = req.getParameter("end_date");

		if (s_date != null && e_date != null) {
			try {
				dates[0] = df.parse(s_date);
				dates[1] = df.parse(e_date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dates;
	}

	private StringBuffer generateLogs(Date start_date, Date end_date) {
		List logs = logModel.findLogs(start_date, end_date);
		int size = logs.size();

		System.out.println("found " + size + " logs");
		StringBuffer content = new StringBuffer(
				"志工姓名, 志工編號, 下載檔案日期, 退件日期, 退件原因\n");
		for (int i = 0; i < size; i++) {
			VoulenteerLogs log = (VoulenteerLogs) logs.get(i);
			content.append(log.getVoulenteer_name() + ","
					+ log.getVoulenteer_id() + ","
					+ df.format(log.getClaim_date()) + ","
					+ df.format(log.getReturn_date()) + "," + log.getExcuse()
					+ "\n");
		}
		return content;
	}
}
