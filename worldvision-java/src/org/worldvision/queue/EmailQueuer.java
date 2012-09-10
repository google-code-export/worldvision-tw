/**
 * 
 */
package org.worldvision.queue;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

/**
 * @author robbie
 * 
 */
public class EmailQueuer extends HttpServlet {
	Queue queue = QueueFactory.getQueue("email-worker-queue");

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		System.out.println("email-queue-called");
		String email = req.getParameter("email");
		String id = req.getParameter("id");
		String emailId = req.getParameter("mailId");
		String execuse = req.getParameter("execuse");
		String params = "email=" + email;
		if (emailId != null)
			params += "&mailId=" + emailId;
		if (id != null)
			params += "&id=" + id;
		if (execuse != null)
			params += "&execuse=" + execuse;
		String volunteerId = req.getParameter("volunteerId");
		if (volunteerId != null)
			params += "&volunteerId=" + volunteerId;
		if (email != null && !"".equals(email))
			queue.add(TaskOptions.Builder.url("/_ah/queue/email-worker-queue?"+ params ).method(Method.GET));
	}
}
