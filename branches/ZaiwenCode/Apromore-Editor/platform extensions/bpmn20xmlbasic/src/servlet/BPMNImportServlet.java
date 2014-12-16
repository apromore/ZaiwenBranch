
package servlet;



import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * EPMLImportServlet converts a EPML specification (.epml file) to the JSON
 * representation of an Signavio diagram. It only supports POST requests with the
 * EPML submitted as parameter "data".
 * It should be accessible at: /epmlimport
 *
 * @author Felix Mannhardt (University of Applied Sciences Bonn-Rhein-Sieg)
 */
public class BPMNImportServlet extends HttpServlet {

    private static final long serialVersionUID = 4651535054294830523L;
    private static final Logger LOGGER = Logger.getLogger(BPMNImportServlet.class.getCanonicalName());

    /* (non-Javadoc)
      * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
      */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String bpmnData = req.getParameter("data");
        OutputStream out = null;

        /* Transform and return as JSON */
        try {
            out = res.getOutputStream();
            res.setContentType("application/json");
            res.setStatus(200);
            BPMN2DiagramConverter bpmnConverter = new BPMN2DiagramConverter("/signaviocore/editor/");
            bpmnConverter.getBPMN(bpmnData, "UTF-8", out);
        } catch (Exception e) {
            try {
                LOGGER.severe(e.toString());
                res.setStatus(500);
                res.setContentType("text/plain");
                PrintWriter writer = new PrintWriter(out);
                writer.println("Failed to import BPMN due to the exception " + e);
                e.printStackTrace(writer);
                e.printStackTrace();
            } catch (Exception e1) {
                System.err.println("Original exception was:");
                e.printStackTrace();
                System.err.println("Exception in exception handler was:");
                e1.printStackTrace();
            }
        }

    }

}
