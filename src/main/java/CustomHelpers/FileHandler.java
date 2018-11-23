package CustomHelpers;

import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHandler {
	public static String download(Part chapterFilePart, String path) {
		String fileDestination = null;
		OutputStream os = null;
		InputStream fileContent = null;
		try {
			fileContent = chapterFilePart.getInputStream();
			fileDestination = path + "/" + FileHandler.getFileName(chapterFilePart);
			os = new FileOutputStream(fileDestination);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = fileContent.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileContent != null) {
					fileContent.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileDestination;
	}

	// Esta funcion permite obtener el nombre del archivo
	private static String getFileName(Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
}
