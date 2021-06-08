package ru.example.conversion;

import com.aspose.cells.FontConfigs;
import com.aspose.cells.License;
import com.aspose.cells.Workbook;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Service;
import ru.example.App;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DocConversionService implements ConversionService {

	private static final Map<Integer, Integer> WORDS_CELLS_SAVE_FORMAT_MAP = ImmutableMap.of(SaveFormat.PDF,
			com.aspose.cells.SaveFormat.PDF);
	private static final Set<String> supportedFormats = ImmutableSet.of(".pdf", ".doc", ".docx", ".rtf",
			".xls", ".xlsx", ".odt", ".ods");
	private static final Map<String, Integer> FORMAT_MAP = new HashMap<>();

	static {
		FORMAT_MAP.put("pdf", SaveFormat.PDF);
		FORMAT_MAP.put("doc", SaveFormat.DOC);
		FORMAT_MAP.put("docx", SaveFormat.DOCX);
		FORMAT_MAP.put("odt", SaveFormat.ODT);
		FORMAT_MAP.put("xls", com.aspose.cells.SaveFormat.EXCEL_97_TO_2003);
		FORMAT_MAP.put("xlsx", com.aspose.cells.SaveFormat.XLSX);
		FORMAT_MAP.put("ods", com.aspose.cells.SaveFormat.ODS);
	}

	@Override
	public ConversionResultContainer convert(InputStream is, String format, String fullFileName) {
		return convert(is, FORMAT_MAP.get(format), fullFileName);
	}

	@Override
	public ConversionResultContainer convert(InputStream is, int saveFormat, String fullFileName) {
		try {
			if (SystemUtils.IS_OS_WINDOWS) {
				FontConfigs.setFontFolder(File.separator + "fonts", true);
			} else {
				FontConfigs.setFontFolder(File.separator + "fonts", true);
			}
			if (isSpreadsheet(fullFileName)) {
				return convertSpreadsheet(is, saveFormat, fullFileName);
			} else {
				return convertDocument(is, saveFormat, fullFileName);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isFormatSupported(String uploadedFileName) {
		String ext = getFileExt(uploadedFileName);
		return supportedFormats.stream().anyMatch(format -> StringUtils.equalsIgnoreCase(format, ext));
	}

	private String getFileExt(String fullFileName) {
		int index = fullFileName.lastIndexOf(".");
		return index != -1 ? fullFileName.substring(index) : "";
	}

	public boolean isSpreadsheet(String fullFileName) {
		String ext = getFileExt(fullFileName);
		return StringUtils.equalsIgnoreCase(ext, ".ods") || StringUtils.equalsIgnoreCase(ext, ".xls") || StringUtils.equalsIgnoreCase(ext, ".xlsx");
	}

	private ConversionResultContainer convertSpreadsheet(InputStream is, Integer saveFormat, String fullFileName) throws Exception {

		saveFormat = WORDS_CELLS_SAVE_FORMAT_MAP.get(saveFormat) != null ? WORDS_CELLS_SAVE_FORMAT_MAP.get(saveFormat) : saveFormat;
		ConversionResultContainer resultContainer = new ConversionResultContainer();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Workbook spreadsheet = new Workbook(is);
		spreadsheet.save(outputStream, saveFormat);

		resultContainer.setResultStream(new ByteArrayInputStream(outputStream.toByteArray()));
		resultContainer.setContentType(saveFormat);
		resultContainer.setFileName(fullFileName);
		return resultContainer;
	}

	private ConversionResultContainer convertDocument(InputStream is, int saveFormat, String fullFileName) throws Exception {
		ConversionResultContainer resultContainer = new ConversionResultContainer();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Document doc = new Document(is);
		doc.save(outputStream, saveFormat);

		resultContainer.setResultStream(new ByteArrayInputStream(outputStream.toByteArray()));
		resultContainer.setContentType(saveFormat);
		resultContainer.setFileName(fullFileName);
		return resultContainer;
	}

	public static boolean authLicense() {
		boolean result = false;
		try {
			InputStream is = com.aspose.cells.License.class.getResourceAsStream("/com.aspose.cells.lic_2999.xml");
			License asposeLicense = new License();
			asposeLicense.setLicense(is);
			is.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean isValidFormat(String inExtention, String targetExtention) {
		boolean inputSupported = false;
		boolean targetSupported = false;
		if (inExtention.startsWith(".")) {
			inputSupported = supportedFormats.stream().anyMatch(format -> StringUtils.equalsIgnoreCase(format, inExtention));
		} else {
			inputSupported = supportedFormats.stream().anyMatch(format -> StringUtils.equalsIgnoreCase(format, "." + inExtention));
		}
		if (targetExtention.startsWith(".")) {
			targetSupported = supportedFormats.stream().anyMatch(format -> StringUtils.equalsIgnoreCase(format, targetExtention));
		} else {
			targetSupported = supportedFormats.stream().anyMatch(format -> StringUtils.equalsIgnoreCase(format, "." + targetExtention));
		}

		return inputSupported && targetSupported;
	}
}
