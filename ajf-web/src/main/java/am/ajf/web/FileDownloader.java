package am.ajf.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class FileDownloader {
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

	/**
	 * download resource on the http response
	 * @param baseFilesPath
	 * @param context
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void doDownload(String baseFilesPath, ServletContext context, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Get requested file by path info.
		String requestedFile = request.getPathInfo();

		doDownload(baseFilesPath, requestedFile, false, null, context, response);
	}

	/**
	 * 
	 * @param baseFilesPath
	 * @param requestedFile
	 * @param context
	 * @param response
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static void doDownload(String baseFilesPath, String requestedFile,
			ServletContext context, HttpServletResponse response)
			throws IOException, UnsupportedEncodingException,
			FileNotFoundException {

		doDownload(baseFilesPath, requestedFile, false, null, context, response);
		
	}

	/**
	 * close a closeable resource
	 * @param resource
	 */
	private static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				// Do your thing with the exception. Print it, log it or mail
				// it.
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param baseFilesPath
	 * @param requestedFile
	 * @param inLine
	 * @param contentType
	 * @param context
	 * @param response
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static void doDownload(String baseFilesPath, String requestedFile,
			boolean inLine, String contentType, ServletContext context, HttpServletResponse response)
			throws IOException, UnsupportedEncodingException,
			FileNotFoundException {
		
		// Check if file is actually supplied to the request URI.
		if (requestedFile == null) {
			// Do your thing if the file is not supplied to the request URI.
			// Throw an exception, or send 404, or show default/warning page, or
			// just ignore it.
			response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
			return;
		}

		// Decode the file name (might contain spaces and on) and prepare file
		// object.
		File file = new File(baseFilesPath,
				URLDecoder.decode(requestedFile, "UTF-8"));

		// Check if file actually exists in filesystem.
		if (!file.exists()) {
			// Do your thing if the file appears to be non-existing.
			// Throw an exception, or send 404, or show default/warning page, or
			// just ignore it.
			response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
			return;
		}
		
		// If content type is unknown, then set the default value.
		// For all content types, see:
		// http://www.w3schools.com/media/media_mimeref.asp
		// To add new content types, add new mime-mapping entry in web.xml.
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		// Init servlet response.
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType(contentType);
		response.setHeader("Content-Length", String.valueOf(file.length()));
		
		String attachment = "attachment";
		if (inLine)
			attachment = "inline";
		response.setHeader("Content-Disposition", attachment + "; filename=\""
				+ file.getName() + "\"");

		// Prepare streams.
		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		try {
			// Open streams.
			input = new BufferedInputStream(new FileInputStream(file),
					DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(),
					DEFAULT_BUFFER_SIZE);

			// Write file contents to response.
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
				output.flush();
			}
		} finally {
			// Gently close streams.
			close(output);
			close(input);
		}
		
	}

}
