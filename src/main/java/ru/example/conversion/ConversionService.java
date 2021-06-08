package ru.example.conversion;

import java.io.InputStream;

public interface ConversionService {
	ConversionResultContainer convert(InputStream is, int saveFormat, String fullFileName);

	ConversionResultContainer convert(InputStream is, String format, String fullFileName);

	boolean isFormatSupported(String uploadedFileName);

	boolean isSpreadsheet(String fullFileName);

	boolean isValidFormat(String inExtention, String targetExtention);
}
