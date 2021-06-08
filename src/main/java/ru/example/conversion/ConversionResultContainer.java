package ru.example.conversion;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.Map;

public class ConversionResultContainer {
	@Getter	@Setter
	private InputStream resultStream;
	@Getter
	private String contentType;
	@Getter
	private String fileName;

	private static final Map<String, String> TYPE_EXT_MAP = ImmutableMap.of("application/pdf", ".pdf",
			"application/vnd.oasis.opendocument.text", ".odt",
			"application/vnd.oasis.opendocument.spreadsheet", ".ods");

	private static final Map<Integer, String> SAVE_FORMAT_CONTENT_TYPE_MAP = ImmutableMap.of(13, "application/pdf",
			40, "application/pdf",
			60, "application/vnd.oasis.opendocument.text",
			14, "application/vnd.oasis.opendocument.spreadsheet");

	public void setFileName(String fullFileName) {
		int index = fullFileName.lastIndexOf(".");
		this.fileName = fullFileName.substring(0, index) + TYPE_EXT_MAP.get(contentType);
	}

	public void setContentType(Integer saveFormat) {
		contentType = SAVE_FORMAT_CONTENT_TYPE_MAP.get(saveFormat);
	}
}
